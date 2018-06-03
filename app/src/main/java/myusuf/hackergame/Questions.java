package myusuf.hackergame;

import java.util.ArrayList;

public class Questions {
    ArrayList<String> questions;
    ArrayList<ArrayList<answers>> answers;
    public Questions(){
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        ArrayList<answers> ans;
        questions.add("What is love?");
        questions.add("Who is you?");
        questions.add("Test Question 1?");
        questions.add("Test Question 2?");
        questions.add("Test Question 3?");
        questions.add("Test Question 4?");
        questions.add("Test Question 5?");
        questions.add("Test Question 6?");

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

        ans = new ArrayList<>();
        ans.add(new answers(true,"Baby dont hurt me"));
        ans.add(new answers(false,"Don't hurt me"));
        ans.add(new answers(false,"No more"));
        ans.add(new answers(false,"IDK maaan"));
        answers.add(ans);

    }

    public ArrayList<ArrayList<answers>> getAnswers() {
        return answers;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }
}
