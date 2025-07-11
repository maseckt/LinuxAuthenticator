package gnu.maseckt.linuxAuthenticator.managers;

import gnu.maseckt.linuxAuthenticator.LinuxAuthenticator;
import gnu.maseckt.linuxAuthenticator.models.ChoiceQuestion;
import gnu.maseckt.linuxAuthenticator.models.TextQuestion;
import gnu.maseckt.linuxAuthenticator.models.UniversalQuestion;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigManager {
    
    private final LinuxAuthenticator plugin;
    private final Random random = new Random();
    
    public ConfigManager(LinuxAuthenticator plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
    }
    
    public String getAuthMode() {
        return plugin.getConfig().getString("settings.mode", "CHOICE");
    }
    
    public int getQuestionsCount() {
        return plugin.getConfig().getInt("settings.questions_count", 3);
    }
    
    public int getTimeoutSeconds() {
        return plugin.getConfig().getInt("settings.timeout_seconds", 60);
    }
    
    public int getMaxErrors() {
        return plugin.getConfig().getInt("settings.max_errors", 3);
    }
    
    public int getMaxAttempts() {
        return plugin.getConfig().getInt("settings.max_attempts", 3);
    }
    
    public int getBanDurationMinutes() {
        return plugin.getConfig().getInt("settings.ban_duration_minutes", 15);
    }
    
    public String getBanCommand() {
        return plugin.getConfig().getString("settings.ban_command", "");
    }
    
    public boolean isAuthOncePerSession() {
        return plugin.getConfig().getBoolean("settings.auth_once_per_session", true);
    }
    
    public String getMessage(String key) {
        return plugin.getConfig().getString("settings.messages." + key, "§cСообщение не найдено: " + key);
    }
    
    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return message;
    }
    
    public List<UniversalQuestion> getUniversalQuestions() {
        List<UniversalQuestion> questions = new ArrayList<>();
        List<?> questionsList = plugin.getConfig().getList("questions");
        
        if (questionsList != null) {
            for (Object obj : questionsList) {
                if (obj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> questionMap = (java.util.Map<String, Object>) obj;
                    
                    String question = (String) questionMap.get("question");
                    String answer = (String) questionMap.get("answer");
                    @SuppressWarnings("unchecked")
                    List<String> options = (List<String>) questionMap.get("options");
                    
                    if (question != null && answer != null) {
                        questions.add(new UniversalQuestion(question, answer, options));
                    }
                }
            }
        }
        
        return questions;
    }
    
    public List<ChoiceQuestion> getChoiceQuestions() {
        List<ChoiceQuestion> questions = new ArrayList<>();
        List<UniversalQuestion> universalQuestions = getUniversalQuestions();
        
        for (UniversalQuestion universalQuestion : universalQuestions) {
            if (universalQuestion.getOptions() != null && !universalQuestion.getOptions().isEmpty()) {
                questions.add(universalQuestion.toChoiceQuestion());
            }
        }
        
        return questions;
    }
    
    public List<TextQuestion> getTextQuestions() {
        List<TextQuestion> questions = new ArrayList<>();
        List<UniversalQuestion> universalQuestions = getUniversalQuestions();
        
        for (UniversalQuestion universalQuestion : universalQuestions) {
            questions.add(universalQuestion.toTextQuestion());
        }
        
        return questions;
    }
    
    public List<ChoiceQuestion> getRandomChoiceQuestions(int count) {
        List<ChoiceQuestion> allQuestions = getChoiceQuestions();
        List<ChoiceQuestion> selectedQuestions = new ArrayList<>();
        
        if (allQuestions.isEmpty()) {
            return selectedQuestions;
        }
        
        for (int i = 0; i < count && i < allQuestions.size(); i++) {
            int index = random.nextInt(allQuestions.size());
            selectedQuestions.add(allQuestions.get(index));
            allQuestions.remove(index);
        }
        
        return selectedQuestions;
    }
    
    public List<TextQuestion> getRandomTextQuestions(int count) {
        List<TextQuestion> allQuestions = getTextQuestions();
        List<TextQuestion> selectedQuestions = new ArrayList<>();
        
        if (allQuestions.isEmpty()) {
            return selectedQuestions;
        }
        
        for (int i = 0; i < count && i < allQuestions.size(); i++) {
            int index = random.nextInt(allQuestions.size());
            selectedQuestions.add(allQuestions.get(index));
            allQuestions.remove(index);
        }
        
        return selectedQuestions;
    }
} 