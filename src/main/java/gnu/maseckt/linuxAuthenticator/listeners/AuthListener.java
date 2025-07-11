package gnu.maseckt.linuxAuthenticator.listeners;

import gnu.maseckt.linuxAuthenticator.LinuxAuthenticator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AuthListener implements Listener {
    
    private final LinuxAuthenticator plugin;
    
    public AuthListener(LinuxAuthenticator plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.getServer().getName().contains("Folia")) {
            player.getScheduler().runDelayed(plugin, scheduledTask -> {
                plugin.getAuthManager().startAuth(player);
            }, null, 1L);
        } else {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                plugin.getAuthManager().startAuth(player);
            }, 1L);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getAuthManager().cancelAuth(player);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            event.setCancelled(true);
            
            if (plugin.getServer().getName().contains("Folia")) {
                player.getScheduler().run(plugin, scheduledTask -> {
                    handleAuthResponse(player, message);
                }, null);
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    handleAuthResponse(player, message);
                });
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(org.bukkit.event.player.PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        if (!plugin.getAuthManager().isAuthenticated(player)) {
            if (!message.startsWith("/answer")) {
                event.setCancelled(true);
                player.sendMessage("§cВы должны сначала пройти аутентификацию!");
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (!plugin.getAuthManager().isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!plugin.getAuthManager().isAuthenticated(player)) {
                event.setCancelled(true);
            }
        }
    }
    
    private void handleAuthResponse(Player player, String message) {
        String mode = plugin.getConfigManager().getAuthMode();
        
        if ("CHOICE".equalsIgnoreCase(mode)) {
            try {
                int choiceIndex = Integer.parseInt(message.trim()) - 1;
                var session = plugin.getAuthManager().getSession(player);
                if (session != null && session.isChoiceMode()) {
                    var question = session.getCurrentChoiceQuestion();
                    if (question != null && choiceIndex >= 0 && choiceIndex < question.getOptions().size()) {
                        String selectedAnswer = question.getOptions().get(choiceIndex);
                        plugin.getAuthManager().handleChoiceAnswer(player, selectedAnswer);
                        return;
                    }
                }
            } catch (NumberFormatException ignored) {
            }
            
            plugin.getAuthManager().handleChoiceAnswer(player, message);
        } else {
            plugin.getAuthManager().handleTextAnswer(player, message);
        }
    }
} 