package gnu.maseckt.linuxAuthenticator.commands;

import gnu.maseckt.linuxAuthenticator.LinuxAuthenticator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LinuxAuthCommand implements CommandExecutor, TabCompleter {
    
    private final LinuxAuthenticator plugin;
    
    public LinuxAuthCommand(LinuxAuthenticator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission("linuxauthenticator.admin")) {
                sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                return true;
            }
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                handleReload(sender);
                break;
            case "reset":
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                handleReset(sender, args);
                break;
            case "info":
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                handleInfo(sender);
                break;
            case "resetsession":
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                handleResetSession(sender);
                break;
            case "unban":
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                handleUnban(sender, args);
                break;
            default:
                if (!sender.hasPermission("linuxauthenticator.admin")) {
                    sender.sendMessage("§cУ вас нет прав для использования этой команды!");
                    return true;
                }
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage("§aКонфигурация перезагружена!");
    }
    
    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /linuxauth reset <игрок>");
            return;
        }
        
        String playerName = args[1];
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        
        if (targetPlayer == null) {
            sender.sendMessage("§cИгрок " + playerName + " не найден!");
            return;
        }
        
        plugin.getAuthManager().cancelAuth(targetPlayer);
        plugin.getAuthManager().startAuth(targetPlayer);
        sender.sendMessage("§aАутентификация для игрока " + playerName + " сброшена!");
    }
    
    private void handleInfo(CommandSender sender) {
        sender.sendMessage("§6=== LinuxAuthenticator Info ===");
        sender.sendMessage("§7Режим: §f" + plugin.getConfigManager().getAuthMode());
        sender.sendMessage("§7Количество вопросов: §f" + plugin.getConfigManager().getQuestionsCount());
        sender.sendMessage("§7Таймаут: §f" + plugin.getConfigManager().getTimeoutSeconds() + " сек");
        sender.sendMessage("§7Максимум ошибок: §f" + plugin.getConfigManager().getMaxErrors());
        sender.sendMessage("§7Максимум попыток: §f" + plugin.getConfigManager().getMaxAttempts());
        sender.sendMessage("§7Аутентификация раз в сессию: §f" + plugin.getConfigManager().isAuthOncePerSession());
        sender.sendMessage("§7Вопросов с выбором: §f" + plugin.getConfigManager().getChoiceQuestions().size());
        sender.sendMessage("§7Текстовых вопросов: §f" + plugin.getConfigManager().getTextQuestions().size());
    }
    
    private void handleResetSession(CommandSender sender) {
        plugin.getAuthManager().resetSession();
        sender.sendMessage("§aСессия аутентификации сброшена! Все игроки должны будут пройти аутентификацию заново.");
    }
    
    private void handleUnban(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cИспользование: /linuxauth unban <игрок>");
            return;
        }
        
        String playerName = args[1];
        plugin.getAuthManager().unbanPlayer(playerName);
        sender.sendMessage("§aИгрок " + playerName + " разбанен!");
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== LinuxAuthenticator Commands ===");
        sender.sendMessage("§7/linuxauth reload §f- Перезагрузить конфигурацию");
        sender.sendMessage("§7/linuxauth reset <игрок> §f- Сбросить аутентификацию игрока");
        sender.sendMessage("§7/linuxauth unban <игрок> §f- Разбанить игрока");
        sender.sendMessage("§7/linuxauth info §f- Показать информацию о плагине");
        sender.sendMessage("§7/linuxauth resetsession §f- Сбросить сессию аутентификации");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("reload", "reset", "unban", "info", "resetsession");
            
            for (String subcommand : subcommands) {
                if (subcommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subcommand);
                }
            }
        } else if (args.length == 2 && "reset".equalsIgnoreCase(args[0]) && sender.hasPermission("linuxauthenticator.admin")) {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
} 