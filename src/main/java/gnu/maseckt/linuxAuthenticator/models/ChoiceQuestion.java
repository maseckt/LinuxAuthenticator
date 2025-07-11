package gnu.maseckt.linuxAuthenticator.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChoiceQuestion {
    private final String question;
    private final String correctAnswer;
    private final List<String> options;
    
    public ChoiceQuestion(String question, String correctAnswer, List<String> options) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public List<String> getRandomizedOptions() {
        List<String> randomizedOptions = new ArrayList<>(options);
        Collections.shuffle(randomizedOptions);
        return randomizedOptions;
    }
    
    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer);
    }
} 