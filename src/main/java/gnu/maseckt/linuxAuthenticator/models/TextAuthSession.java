package gnu.maseckt.linuxAuthenticator.models;

import org.bukkit.entity.Player;

import java.util.List;

public class TextAuthSession extends AuthSession {
    private final List<TextQuestion> questions;
    
    public TextAuthSession(Player player, List<TextQuestion> questions) {
        super(player, questions.size());
        this.questions = questions;
    }
    
    @Override
    public boolean isChoiceMode() {
        return false;
    }
    
    @Override
    public boolean isTextMode() {
        return true;
    }
    
    @Override
    public ChoiceQuestion getCurrentChoiceQuestion() {
        return null;
    }
    
    @Override
    public TextQuestion getCurrentTextQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            return null;
        }
        return questions.get(currentQuestionIndex);
    }
} 