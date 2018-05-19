package myusuf.hackergame;

/**
 * Created by engin on 15 May 2018.
 */

public class answers {
    private boolean chosen;
    private String text;

    public answers(boolean chosen, String text){
        this.text = text;
        this.chosen = chosen;
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
