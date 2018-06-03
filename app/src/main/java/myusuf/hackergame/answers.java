package myusuf.hackergame;

/**
 * Created by engin on 15 May 2018.
 */

public class answers {
    private boolean chosen;
    private String text;
    private boolean correct;

    public answers(boolean correct, String text){
        this.text = text;
        this.chosen = false;
        this.correct = correct;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public boolean isChosen() {
        return chosen;
    }

    public String getText() {
        return text;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public void setText(String text) {
        this.text = text;
    }
}
