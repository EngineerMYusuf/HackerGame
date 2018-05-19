package myusuf.hackergame;

import java.util.ArrayList;

public class Questions {
    ArrayList<String> questions;
    ArrayList<ArrayList<String>> answers;
    public Questions(){
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        questions.add("What is love?");
        questions.add("Who is you?");
        questions.add("Test Question 1?");
        questions.add("Test Question 2?");
        questions.add("Test Question 3?");
        questions.add("Test Question 4?");
        questions.add("Test Question 5?");
        questions.add("Test Question 6?");

        answers.get(0).add("TBaby don't hurt me");
        answers.get(0).add("Don't hurt me");
        answers.get(0).add("No more");
        answers.get(0).add("IDK maaan");

        answers.get(1).add("TYou is you");
        answers.get(1).add("We is you");
        answers.get(1).add("No one is you");
        answers.get(1).add("Change subject");

        answers.get(2).add("TYou is you");
        answers.get(2).add("We is you");
        answers.get(2).add("No one is you");
        answers.get(2).add("Change subject");

        answers.get(3).add("TYou is you");
        answers.get(3).add("We is you");
        answers.get(3).add("No one is you");
        answers.get(3).add("Change subject");

        answers.get(4).add("TYou is you");
        answers.get(4).add("We is you");
        answers.get(4).add("No one is you");
        answers.get(4).add("Change subject");

        answers.get(5).add("TYou is you");
        answers.get(5).add("We is you");
        answers.get(5).add("No one is you");
        answers.get(5).add("Change subject");

        answers.get(6).add("TYou is you");
        answers.get(6).add("We is you");
        answers.get(6).add("No one is you");
        answers.get(6).add("Change subject");

        answers.get(7).add("TYou is you");
        answers.get(7).add("We is you");
        answers.get(7).add("No one is you");
        answers.get(7).add("Change subject");
    }

    public ArrayList<ArrayList<String>> getAnswers() {
        return answers;
    }

    public ArrayList<String> getQuestions() {
        return questions;
    }
}
