package gnu.maseckt.linuxAuthenticator.managers;

import gnu.maseckt.linuxAuthenticator.LinuxAuthenticator;
import gnu.maseckt.linuxAuthenticator.models.AuthSession;
import gnu.maseckt.linuxAuthenticator.models.ChoiceAuthSession;
import gnu.maseckt.linuxAuthenticator.models.TextAuthSession;
import gnu.maseckt.linuxAuthenticator.models.ChoiceQuestion;
import gnu.maseckt.linuxAuthenticator.models.TextQuestion;
import gnu.maseckt.linuxAuthenticator.models.PlayerAttempts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthManager {
    
    private final LinuxAuthenticator plugin;
    private final Map<UUID, AuthSession> authSessions = new HashMap<>();
    private final Map<UUID, BukkitTask> timeoutTasks = new HashMap<>();
    private final Map<UUID, PlayerAttempts> playerAttempts = new HashMap<>();
    private final Map<UUID, Boolean> authenticatedPlayers = new HashMap<>();
    
    public AuthManager(LinuxAuthenticator plugin) {
        this.plugin = plugin;
    }
    
    public void startAuth(Player player) {
        if (hasPermission(player, "linuxauthenticator.bypass")) {
            player.sendMessage("§aВы обошли аутентификацию благодаря правам!");
            authenticatedPlayers.put(player.getUniqueId(), true);
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        if (plugin.getConfigManager().isAuthOncePerSession() && authenticatedPlayers.containsKey(playerId)) {
            return;
        }
        
        PlayerAttempts attempts = playerAttempts.get(playerId);
        if (attempts != null && attempts.isBanned()) {
            long remainingTime = attempts.getRemainingBanTime();
            long remainingMinutes = (remainingTime / 1000 / 60) + 1;
            String banMessage = plugin.getConfigManager().getMessage("ban_message", 
                "%duration%", String.valueOf(remainingMinutes));
            player.kickPlayer(banMessage);
            return;
        }
        
        cancelAuth(player);
        
        String mode = plugin.getConfigManager().getAuthMode();
        AuthSession session;
        
        if ("CHOICE".equalsIgnoreCase(mode)) {
            List<ChoiceQuestion> questions = plugin.getConfigManager().getRandomChoiceQuestions(
                plugin.getConfigManager().getQuestionsCount()
            );
            session = new ChoiceAuthSession(player, questions);
        } else {
            List<TextQuestion> questions = plugin.getConfigManager().getRandomTextQuestions(
                plugin.getConfigManager().getQuestionsCount()
            );
            session = new TextAuthSession(player, questions);
        }
        
        authSessions.put(playerId, session);
        
        int timeout = plugin.getConfigManager().getTimeoutSeconds();
        if (Bukkit.getServer().getName().contains("Folia")) {
            player.getScheduler().runDelayed(plugin, 
                scheduledTask -> timeoutPlayer(player), null, timeout * 20L);
        } else {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, 
                () -> timeoutPlayer(player), timeout * 20L);
            timeoutTasks.put(playerId, task);
        }
        
        sendNextQuestion(player);
    }
    
    public void handleChoiceAnswer(Player player, String answer) {
        AuthSession session = authSessions.get(player.getUniqueId());
        if (session == null || !session.isChoiceMode()) {
            return;
        }
        
        ChoiceQuestion currentQuestion = session.getCurrentChoiceQuestion();
        if (currentQuestion == null) {
            return;
        }
        
        if (currentQuestion.isCorrectAnswer(answer)) {
            session.incrementCorrectAnswers();
            
            if (session.isCompleted()) {
                completeAuth(player);
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("correct_answer"));
                session.nextQuestion();
                sendNextQuestion(player);
            }
        } else {
            session.incrementErrors();
            int remainingErrors = plugin.getConfigManager().getMaxErrors() - session.getErrors();
            
            if (session.hasReachedMaxErrors(plugin.getConfigManager().getMaxErrors())) {
                handleMaxErrorsReached(player);
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("wrong_answer", 
                    "%errors%", String.valueOf(remainingErrors)));
                sendCurrentQuestion(player);
            }
        }
    }
    
    public void handleTextAnswer(Player player, String answer) {
        AuthSession session = authSessions.get(player.getUniqueId());
        if (session == null || !session.isTextMode()) {
            return;
        }
        
        TextQuestion currentQuestion = session.getCurrentTextQuestion();
        if (currentQuestion == null) {
            return;
        }
        
        if (currentQuestion.isCorrectAnswer(answer)) {
            session.incrementCorrectAnswers();
            
            if (session.isCompleted()) {
                completeAuth(player);
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("correct_answer"));
                session.nextQuestion();
                sendNextQuestion(player);
            }
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("wrong_answer"));
            sendCurrentQuestion(player);
        }
    }
    
    private void sendNextQuestion(Player player) {
        AuthSession session = authSessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }
        if (session.isChoiceMode()) {
            ChoiceQuestion question = session.getCurrentChoiceQuestion();
            if (question != null) {
                player.sendMessage("");
                player.sendMessage("§e" + question.getQuestion());
                player.sendMessage("§7Варианты ответов:");
                
                List<String> randomizedOptions = question.getRandomizedOptions();
                for (int i = 0; i < randomizedOptions.size(); i++) {
                    String option = randomizedOptions.get(i);
                    String escapedOption = option.replace("\\", "\\\\").replace("\"", "\\\"");
                    Component clickableOption = Component.text("  " + (i + 1) + ". ", NamedTextColor.GRAY)
                        .append(Component.text(option, NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.runCommand("/answer \"" + escapedOption + "\""))
                        .hoverEvent(Component.text("Нажмите, чтобы выбрать этот ответ", NamedTextColor.GREEN)));
                    player.sendMessage(clickableOption);
                }
                player.sendMessage("");
                player.sendMessage("§7Нажмите на ответ или напишите номер/текст в чат");
            }
        } else {
            TextQuestion question = session.getCurrentTextQuestion();
            if (question != null) {
                player.sendMessage("§e§l" + question.getQuestion());
                player.sendMessage("");
                player.sendMessage("§7Напишите ответ в чат:");
            }
        }
    }
    
    private void sendCurrentQuestion(Player player) {
        sendNextQuestion(player);
    }
    
    private void completeAuth(Player player) {
        UUID playerId = player.getUniqueId();
        
        player.sendMessage(plugin.getConfigManager().getMessage("success"));
        
        authenticatedPlayers.put(playerId, true);
        
        PlayerAttempts attempts = playerAttempts.get(playerId);
        if (attempts != null) {
            attempts.reset();
        }
        
        cancelTimeoutTask(playerId);
        authSessions.remove(playerId);
    }
    
    public void cancelAuth(Player player) {
        UUID playerId = player.getUniqueId();
        authSessions.remove(playerId);
        cancelTimeoutTask(playerId);
    }
    
    private void cancelTimeoutTask(UUID playerId) {
        if (!Bukkit.getServer().getName().contains("Folia")) {
            BukkitTask task = timeoutTasks.remove(playerId);
            if (task != null) {
                task.cancel();
            }
        }
    }
    
    public boolean isAuthenticated(Player player) {
        UUID playerId = player.getUniqueId();
        return authenticatedPlayers.containsKey(playerId) || 
               !authSessions.containsKey(playerId) || 
               hasPermission(player, "linuxauthenticator.bypass");
    }
    
    public void timeoutPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (player.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getMessage("timeout"));
            player.kickPlayer(plugin.getConfigManager().getMessage("kick_message"));
        }
        
        handleFailedAttempt(player);
    }
    
    private void handleMaxErrorsReached(Player player) {
        UUID playerId = player.getUniqueId();
        
        if (player.isOnline()) {
            player.sendMessage(plugin.getConfigManager().getMessage("max_errors_reached"));
            player.kickPlayer(plugin.getConfigManager().getMessage("kick_message"));
        }
        
        handleFailedAttempt(player);
    }
    
    private void handleFailedAttempt(Player player) {
        UUID playerId = player.getUniqueId();
        
        PlayerAttempts attempts = playerAttempts.get(playerId);
        if (attempts == null) {
            attempts = new PlayerAttempts(playerId);
            playerAttempts.put(playerId, attempts);
        }
        attempts.incrementAttempts();
        
        if (plugin.getConfigManager().getMaxAttempts() > 0 && 
            attempts.getAttempts() >= plugin.getConfigManager().getMaxAttempts()) {
            
            int banDuration = plugin.getConfigManager().getBanDurationMinutes();
            attempts.setBannedWithDuration(true, banDuration);
            
            String banCommand = plugin.getConfigManager().getBanCommand();
            if (banCommand != null && !banCommand.trim().isEmpty()) {
                final String finalBanCommand = banCommand.replace("%player%", player.getName());
                if (Bukkit.getServer().getName().contains("Folia")) {
                    player.getScheduler().run(plugin, scheduledTask -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalBanCommand);
                    }, null);
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalBanCommand);
                    });
                }
            }
        }
        
        authSessions.remove(playerId);
        cancelTimeoutTask(playerId);
    }
    
    private boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }
    
    public void cleanup() {
        for (BukkitTask task : timeoutTasks.values()) {
            task.cancel();
        }
        timeoutTasks.clear();
        authSessions.clear();
        authenticatedPlayers.clear();
    }
    
    public void resetSession() {
        authenticatedPlayers.clear();
    }
    
    public AuthSession getSession(Player player) {
        return authSessions.get(player.getUniqueId());
    }
    
    public void unbanPlayer(String playerName) {
        for (PlayerAttempts attempts : playerAttempts.values()) {
            if (attempts.isBanned()) {
                attempts.setBanned(false);
            }
        }
    }
} 