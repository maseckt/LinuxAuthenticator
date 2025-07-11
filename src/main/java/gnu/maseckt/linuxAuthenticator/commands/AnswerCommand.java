package gnu.maseckt.linuxAuthenticator.commands;

import gnu.maseckt.linuxAuthenticator.LinuxAuthenticator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnswerCommand implements CommandExecutor {
    
    private final LinuxAuthenticator plugin;
    
    public AnswerCommand(LinuxAuthenticator plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cИспользование: /answer <ответ>");
            return true;
        }
        
        StringBuilder answerBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) answerBuilder.append(" ");
            answerBuilder.append(args[i]);
        }
        String answer = answerBuilder.toString();
        
        if (answer.startsWith("\"") && answer.endsWith("\"")) {
            answer = answer.substring(1, answer.length() - 1);
        }
        
        String mode = plugin.getConfigManager().getAuthMode();
        
        if ("CHOICE".equalsIgnoreCase(mode)) {
            plugin.getAuthManager().handleChoiceAnswer(player, answer);
        } else {
            plugin.getAuthManager().handleTextAnswer(player, answer);
        }
        
        return true;
    }
} 