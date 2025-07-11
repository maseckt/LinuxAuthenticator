package gnu.maseckt.linuxAuthenticator.models;

import java.util.List;

public class UniversalQuestion {
    private final String question;
    private final String answer;
    private final List<String> options;
    
    public UniversalQuestion(String question, String answer, List<String> options) {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public boolean isCorrectAnswer(String playerAnswer) {
        return answer.equalsIgnoreCase(playerAnswer.trim());
    }
    
    public ChoiceQuestion toChoiceQuestion() {
        return new ChoiceQuestion(question, answer, options);
    }
    
    public TextQuestion toTextQuestion() {
        return new TextQuestion(question, answer);
    }
} 