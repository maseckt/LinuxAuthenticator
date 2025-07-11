package gnu.maseckt.linuxAuthenticator.models;

import org.bukkit.entity.Player;

public abstract class AuthSession {
    protected final Player player;
    protected int currentQuestionIndex = 0;
    protected int correctAnswers = 0;
    protected int errors = 0;
    protected final int totalQuestions;
    
    protected AuthSession(Player player, int totalQuestions) {
        this.player = player;
        this.totalQuestions = totalQuestions;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public abstract boolean isChoiceMode();
    public abstract boolean isTextMode();
    public abstract ChoiceQuestion getCurrentChoiceQuestion();
    public abstract TextQuestion getCurrentTextQuestion();
    
    public void nextQuestion() {
        currentQuestionIndex++;
    }
    
    public void incrementCorrectAnswers() {
        correctAnswers++;
    }
    
    public void incrementErrors() {
        errors++;
    }
    
    public int getErrors() {
        return errors;
    }
    
    public boolean isCompleted() {
        return correctAnswers >= totalQuestions;
    }
    
    public boolean hasReachedMaxErrors(int maxErrors) {
        return errors >= maxErrors;
    }
    
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
} 