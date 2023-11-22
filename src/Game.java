import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    private ArrayList<String> importList = new ArrayList<String>();
    private String[][] quizCategory = new String[50][2];
    private ArrayList<Question> quizQuestionsList = new ArrayList<Question>();
    private ArrayList<Answer> quizAnswersList = new ArrayList<Answer>();

    public void ImportQuestions() throws IOException {

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\ricka\\Javamapp\\QuizKampen\\OOPSprint5-Quizkampen\\src\\Quiz.txt"))){

            while(true){
                String input = bufferedReader.readLine();

                if(input != null){
                    importList.add(input);
                }
                else{
                    break;
                }
            }

        }

        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void SortQuestions(){

        int category = 0;
        char tempChar;
        int questionNumber = 0;
        int answerNumber = 0;

        for (String tempStringFromList :importList){
            if(tempStringFromList.length() > 0){
                tempChar = tempStringFromList.charAt(0);

                if(tempChar == '#'){
                    String temp = String.valueOf(category);
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    quizCategory[category][0] = temp;
                    temp = String.valueOf(sb);
                    quizCategory[category][1] = temp;
                    category++;
                    questionNumber = 0;

                }

                if(tempChar == '%'){
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    Question question = new Question(category, questionNumber, tempString);
                    quizQuestionsList.add(question);
                    answerNumber = 0;
                    questionNumber++;

                }

                if(tempChar == '1'){
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    String tempString2 = String.valueOf(questionNumber);
                    Answer answer = new Answer(category, questionNumber-1, answerNumber, tempString, true);
                    quizAnswersList.add(answer);
                    answerNumber++;

                }

                if(tempChar == '2'){
                    StringBuilder sb = new StringBuilder(tempStringFromList);
                    sb.deleteCharAt(0);
                    sb.deleteCharAt(0);
                    String tempString = String.valueOf(sb);
                    Answer answer = new Answer(category, questionNumber-1, answerNumber, tempString, false);
                    quizAnswersList.add(answer);
                    answerNumber++;

                }

            }

        }


    }

    public void QuestionOptions() {
        int amountOfCategories = 0;
        List<Integer> categoryListRandom = new ArrayList<>();
        for (int i = 0; i < quizCategory.length; i++) {
            String temp = quizCategory[i][1];
            if (temp != null) {
                categoryListRandom.add(amountOfCategories);
                amountOfCategories++;
            }
        }

        for (int i = 0; i < 6; i++) {


            Collections.shuffle(categoryListRandom, new Random());
            String categorySelect = JOptionPane.showInputDialog("Välj en kategori: " +
                    "\n1: " + quizCategory[categoryListRandom.get(0)][1] +
                    "\n2: " + quizCategory[categoryListRandom.get(1)][1] +
                    "\n3: " + quizCategory[categoryListRandom.get(2)][1]);

            int categorySelectInt = 0;
            try {
                categorySelectInt = Integer.parseInt(categorySelect);
            }
            catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Du måste ange ett giltigt val.");
            }
            categorySelectInt = categorySelectInt - 1;
            int selectedCategory = categoryListRandom.get(categorySelectInt);


            int amountOfQuestions = 0;
            List<Integer> questionsListRandom = new ArrayList<>();
            for (Question q : quizQuestionsList) {
                int temp = q.getQuizCategory();
                if (temp - 1 == selectedCategory) {
                    questionsListRandom.add(amountOfQuestions);
                    amountOfQuestions++;
                }
            }
            Collections.shuffle(questionsListRandom, new Random());
            int question1 = questionsListRandom.get(0);
            int question2 = questionsListRandom.get(1);
            int question3 = questionsListRandom.get(2);
            String firstQuestion = "";
            List<Answer> firstQuestionAnswers = new ArrayList<>();
            String secondQuestion = "";
            List<Answer> secondQuestionAnswers = new ArrayList<>();
            String thirdQuestion = "";
            List<Answer> thirdQuestionAnswers = new ArrayList<>();
            selectedCategory = selectedCategory + 1;

            for (Question q : quizQuestionsList) {
                int temp1 = q.getQuizCategory();
                int temp2 = q.getQuestionNumber();
                if (temp1 == selectedCategory && temp2 == question1) {
                    firstQuestion = q.getQuizQuestion();
                    for (Answer a : quizAnswersList) {
                        int temp3 = a.getQuizCategory();
                        int temp4 = a.getQuestionNumber();
                        int temp5 = a.getAnswerNumber();
                        if (temp3 == selectedCategory && temp4 == question1 && temp5 == 0) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, true);
                            firstQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question1 && temp5 == 1) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            firstQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question1 && temp5 == 2) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            firstQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question1 && temp5 == 3) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            firstQuestionAnswers.add(answer);
                        }
                    }
                }
                if (temp1 == selectedCategory && temp2 == question2) {
                    secondQuestion = q.getQuizQuestion();
                    for (Answer a : quizAnswersList) {
                        int temp3 = a.getQuizCategory();
                        int temp4 = a.getQuestionNumber();
                        int temp5 = a.getAnswerNumber();
                        if (temp3 == selectedCategory && temp4 == question2 && temp5 == 0) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, true);
                            secondQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question2 && temp5 == 1) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            secondQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question2 && temp5 == 2) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            secondQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question2 && temp5 == 3) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            secondQuestionAnswers.add(answer);
                        }
                    }
                }
                if (temp1 == selectedCategory && temp2 == question3) {
                    thirdQuestion = q.getQuizQuestion();
                    for (Answer a : quizAnswersList) {
                        int temp3 = a.getQuizCategory();
                        int temp4 = a.getQuestionNumber();
                        int temp5 = a.getAnswerNumber();
                        if (temp3 == selectedCategory && temp4 == question3 && temp5 == 0) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, true);
                            thirdQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question3 && temp5 == 1) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            thirdQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question3 && temp5 == 2) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            thirdQuestionAnswers.add(answer);
                        }
                        if (temp3 == selectedCategory && temp4 == question3 && temp5 == 3) {
                            String tempAnswer = a.getQuizAnswer();
                            Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                            thirdQuestionAnswers.add(answer);
                        }
                    }
                }
            }

            Collections.shuffle(firstQuestionAnswers, new Random());
            Collections.shuffle(secondQuestionAnswers, new Random());
            Collections.shuffle(thirdQuestionAnswers, new Random());
            char svar1 = 'x';
            char svar2 = 'x';
            char svar3 = 'x';

            String stringAnswer1 = JOptionPane.showInputDialog(firstQuestion +
                    "\n" + firstQuestionAnswers.get(0).getQuizAnswer() +
                    "\n" + firstQuestionAnswers.get(1).getQuizAnswer() +
                    "\n" + firstQuestionAnswers.get(2).getQuizAnswer() +
                    "\n" + firstQuestionAnswers.get(3).getQuizAnswer());
            try {
                int answer1 = Integer.parseInt(stringAnswer1);
                if (firstQuestionAnswers.get(answer1 - 1).getCorrectAnswer() == true) {
                    svar1 = 'v';
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            String stringAnswer2 = JOptionPane.showInputDialog(secondQuestion +
                    "\n" + secondQuestionAnswers.get(0).getQuizAnswer() +
                    "\n" + secondQuestionAnswers.get(1).getQuizAnswer() +
                    "\n" + secondQuestionAnswers.get(2).getQuizAnswer() +
                    "\n" + secondQuestionAnswers.get(3).getQuizAnswer());

            try {
                int answer2 = Integer.parseInt(stringAnswer2);
                if (secondQuestionAnswers.get(answer2 - 1).getCorrectAnswer() == true) {
                    svar2 = 'v';
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            String stringAnswer3 = JOptionPane.showInputDialog(thirdQuestion +
                    "\n" + thirdQuestionAnswers.get(0).getQuizAnswer() +
                    "\n" + thirdQuestionAnswers.get(1).getQuizAnswer() +
                    "\n" + thirdQuestionAnswers.get(2).getQuizAnswer() +
                    "\n" + thirdQuestionAnswers.get(3).getQuizAnswer());

            try {
                int answer3 = Integer.parseInt(stringAnswer3);
                if (thirdQuestionAnswers.get(answer3 - 1).getCorrectAnswer() == true) {
                    svar3 = 'v';
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            JOptionPane.showMessageDialog(null, "Antal rätt: " + svar1 + svar2 + svar3);
            categoryListRandom.remove(categorySelectInt);
        }

    }

}