package gnu.maseckt.linuxAuthenticator.models;

import org.bukkit.entity.Player;

import java.util.List;

public class ChoiceAuthSession extends AuthSession {
    private final List<ChoiceQuestion> questions;
    
    public ChoiceAuthSession(Player player, List<ChoiceQuestion> questions) {
        super(player, questions.size());
        this.questions = questions;
    }
    
    @Override
    public boolean isChoiceMode() {
        return true;
    }
    
    @Override
    public boolean isTextMode() {
        return false;
    }
    
    @Override
    public ChoiceQuestion getCurrentChoiceQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentQuestionIndex);
    }
    
    @Override
    public TextQuestion getCurrentTextQuestion() {
        return null;
    }
} 