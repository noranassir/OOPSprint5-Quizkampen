import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {

    //Originallistor och viktiga variabler, RÖR EJ!
    public ArrayList<String> importList = new ArrayList<String>();
    public ArrayList<Category> quizCategoryList = new ArrayList<>();
    public ArrayList<Question> quizQuestionsList = new ArrayList<Question>();
    public ArrayList<Answer> quizAnswersList = new ArrayList<Answer>();


    //Temporära listor för att modifieras

    public ArrayList<Integer> categoryListRandom = new ArrayList<>();
    public ArrayList<Question> quizQuestionRandomiser = new ArrayList<>();
    public ArrayList<Answer> quizAnswersAfterRand = new ArrayList<>();
    public ArrayList<Answer> randomisedAnswers = new ArrayList<>();


    //Diverse variabler för multipla metoder
    int amountOfCategories = 0;
    public int selectedCategory = 0;




    public void importQuestions() throws IOException {

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(".\\src\\Quiz.txt"))) {

            while (true) {
                String input = bufferedReader.readLine();

                if (input != null) {
                    importList.add(input);
                } else {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sortQuestions() {

        int category = 0;
        char tempChar;
        int questionNumber = 0;
        int answerNumber = 0;

        for (String tempStringFromList : importList) {
            if (tempStringFromList.length() > 0) {
                tempChar = tempStringFromList.charAt(0);

                if(tempChar == '#') {
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String temp = String.valueOf(sb);
                    Category category1 = new Category(category, temp);
                    quizCategoryList.add(category1);
                    category++;
                    questionNumber = 0;
                }

                if (tempChar == '%') {
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    Question question = new Question(category, questionNumber, tempString);
                    quizQuestionsList.add(question);
                    answerNumber = 0;
                    questionNumber++;

                }

                if (tempChar == '1') {
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    String tempString2 = String.valueOf(questionNumber);
                    Answer answer = new Answer(category, questionNumber - 1, answerNumber, tempString, true);
                    quizAnswersList.add(answer);
                    answerNumber++;

                }

                if (tempChar == '2') {
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    Answer answer = new Answer(category, questionNumber - 1, answerNumber, tempString, false);
                    quizAnswersList.add(answer);
                    answerNumber++;

                }

            }

        }


    }

    public void amountOfCategories() {
        amountOfCategories = 0;

        for (int i = 0; i < quizCategoryList.size(); i++) {
            categoryListRandom.add(amountOfCategories);
            amountOfCategories++;
        }
    }



    public void importSelectedQuestions(){
        quizQuestionRandomiser.clear();
        for(Question q : quizQuestionsList){
            int temp1 = q.getQuizCategory()-1;
            if(temp1 == selectedCategory){
                quizQuestionRandomiser.add(q);
            }
        }

        Collections.shuffle(quizQuestionRandomiser, new Random());
    }
    public void importAnswers(){


        for(Question q : quizQuestionRandomiser){
            int temp1 = q.getQuizCategory();
            int temp2 = q.getQuestionNumber();
            for(Answer a : quizAnswersList){
                if(temp1 == a.getQuizCategory() && temp2 == a.getQuestionNumber()){
                    quizAnswersAfterRand.add(a);
                }
            }

        }
    }

    public void randomiseAnswers(){

        randomisedAnswers.clear();
        for (int i = 0; i < 4; i++) {
            randomisedAnswers.add(quizAnswersAfterRand.get(i));
        }
        Collections.shuffle(randomisedAnswers, new Random());
        for (int i = 0; i < 4; i++) {
            quizAnswersAfterRand.remove(0);
        }
    }
}