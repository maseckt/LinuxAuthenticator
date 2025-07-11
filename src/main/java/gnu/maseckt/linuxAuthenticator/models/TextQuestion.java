package gnu.maseckt.linuxAuthenticator.models;

public class TextQuestion {
    private final String question;
    private final String answer;
    
    public TextQuestion(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public boolean isCorrectAnswer(String playerAnswer) {
        return answer.equalsIgnoreCase(playerAnswer.trim());
    }
} 