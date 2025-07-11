package gnu.maseckt.linuxAuthenticator;

import gnu.maseckt.linuxAuthenticator.commands.LinuxAuthCommand;
import gnu.maseckt.linuxAuthenticator.commands.AnswerCommand;
import gnu.maseckt.linuxAuthenticator.listeners.AuthListener;
import gnu.maseckt.linuxAuthenticator.managers.AuthManager;
import gnu.maseckt.linuxAuthenticator.managers.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LinuxAuthenticator extends JavaPlugin {
    
    private static LinuxAuthenticator instance;
    private ConfigManager configManager;
    private AuthManager authManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        this.configManager = new ConfigManager(this);
        this.authManager = new AuthManager(this);
        
        LinuxAuthCommand commandExecutor = new LinuxAuthCommand(this);
        getCommand("linuxauth").setExecutor(commandExecutor);
        getCommand("linuxauth").setTabCompleter(commandExecutor);
        
        getCommand("answer").setExecutor(new AnswerCommand(this));
        
        getServer().getPluginManager().registerEvents(new AuthListener(this), this);
        
        getLogger().info("LinuxAuthenticator успешно загружен!");
        getLogger().info("Режим аутентификации: " + configManager.getAuthMode());
        getLogger().info("Количество вопросов: " + configManager.getQuestionsCount());
    }
    
    @Override
    public void onDisable() {
        if (authManager != null) {
            authManager.cleanup();
        }
        getLogger().info("LinuxAuthenticator выгружен!");
    }
    
    public static LinuxAuthenticator getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public AuthManager getAuthManager() {
        return authManager;
    }
}
