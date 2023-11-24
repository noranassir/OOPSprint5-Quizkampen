import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Game {

    private ArrayList<String> importList = new ArrayList<String>();
    private String[][] quizCategory = new String[50][2];
    private ArrayList<Question> quizQuestionsList = new ArrayList<Question>();
    private ArrayList<Answer> quizAnswersList = new ArrayList<Answer>();
    private int propertiesAmountOfQuestions;  //IN
    private int propertiesAmountOfRounds; //IN

    public void ImportQuestions() throws IOException {

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/pontuslundin/Desktop/javamapp/Objektorienterad Programmering/OOPSprint5-Quizkampen/src/Quiz.txt"))){

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

        Properties p = new Properties();    //Glöm inte kolla path till quiz och Settings
        try {
            p.load(new FileInputStream("/Users/pontuslundin/Desktop/javamapp/Objektorienterad Programmering/OOPSprint5-Quizkampen/src/Settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        propertiesAmountOfQuestions = Integer.parseInt(p.getProperty("amountOfQuestions", "3"));
        propertiesAmountOfRounds = Integer.parseInt(p.getProperty("amountOfRounds", "3"));

        int amountOfCategories = 0;
        List<Integer> categoryListRandom = new ArrayList<>();
        for (int i = 0; i < quizCategory.length; i++) {
            String temp = quizCategory[i][1];
            if (temp != null) {
                categoryListRandom.add(amountOfCategories);
                amountOfCategories++;
            }
        }




        List<String>amountOfCorrectAnswers = new ArrayList<>();

        for (int i = 0; i < propertiesAmountOfRounds; i++) { //Antal kategorier per runda


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



            List<String>gameQuestions = new ArrayList<>();
            List<Answer>gameAnswers = new ArrayList<>();

            for (int j = 0; j < propertiesAmountOfQuestions; j++) { //Antal frågor
                String tempStringQuestion = "";


                for (Question q : quizQuestionsList) {
                    int temp1 = q.getQuizCategory();
                    int temp2 = q.getQuestionNumber();
                    if (temp1 == selectedCategory && temp2 == questionsListRandom.get(j)) {
                        tempStringQuestion = q.getQuizQuestion();
                        gameQuestions.add(tempStringQuestion);
                        for (Answer a : quizAnswersList) {
                            int temp3 = a.getQuizCategory();
                            int temp4 = a.getQuestionNumber();
                            int temp5 = a.getAnswerNumber();
                            if (temp3 == selectedCategory && temp4 == questionsListRandom.get(j) && temp5 == 0) {
                                String tempAnswer = a.getQuizAnswer();
                                Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, true);
                                gameAnswers.add(answer);
                            }
                            if (temp3 == selectedCategory && temp4 == questionsListRandom.get(j) && temp5 == 1) {
                                String tempAnswer = a.getQuizAnswer();
                                Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                                gameAnswers.add(answer);
                            }
                            if (temp3 == selectedCategory && temp4 == questionsListRandom.get(j) && temp5 == 2) {
                                String tempAnswer = a.getQuizAnswer();
                                Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                                gameAnswers.add(answer);
                            }
                            if (temp3 == selectedCategory && temp4 == questionsListRandom.get(j) && temp5 == 3) {
                                String tempAnswer = a.getQuizAnswer();
                                Answer answer = new Answer(temp3, temp4, temp5, tempAnswer, false);
                                gameAnswers.add(answer);
                            }
                        }
                    }
                }
            }


            int amountOfGameQuestions = gameQuestions.size();
            List<Integer>oneToFourRandom = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                oneToFourRandom.add(j);
            }
            List<Integer>answerNumber = new ArrayList<>();
            int tempAnswerNumber = -4;
            for (int j = 0; j < amountOfGameQuestions; j++) {
                tempAnswerNumber = tempAnswerNumber+4;
                answerNumber.add(tempAnswerNumber);
            }

            String showCorrectAnswers = "";
            char tempChar;
            for (int j = 0; j < amountOfGameQuestions; j++) {
                Collections.shuffle(oneToFourRandom, new Random());
                String stringAnswer = JOptionPane.showInputDialog(gameQuestions.get(j) +
                        "\n" + gameAnswers.get(oneToFourRandom.get(0)+answerNumber.get(j)).getQuizAnswer() +
                        "\n" + gameAnswers.get(oneToFourRandom.get(1)+answerNumber.get(j)).getQuizAnswer() +
                        "\n" + gameAnswers.get(oneToFourRandom.get(2)+answerNumber.get(j)).getQuizAnswer() +
                        "\n" + gameAnswers.get(oneToFourRandom.get(3)+answerNumber.get(j)).getQuizAnswer());
                try{
                    int answer = Integer.parseInt(stringAnswer);
                    if (gameAnswers.get(answer - 1).getCorrectAnswer() == true){
                        amountOfCorrectAnswers.add("V");
                        tempChar = 'V';
                    }
                    else {
                        amountOfCorrectAnswers.add("X");
                        tempChar = 'X';
                    }
                    showCorrectAnswers = showCorrectAnswers + tempChar;
                }
                catch (NumberFormatException e){
                    e.printStackTrace();
                }

            }



            JOptionPane.showMessageDialog(null, "Antal rätt: " + showCorrectAnswers);
            categoryListRandom.remove(categorySelectInt);
        }
        int numberOfCorrectAnswers = 0;
        int correctAnswers = amountOfCorrectAnswers.size();
        for (int i = 0; i < correctAnswers; i++) {
            if (amountOfCorrectAnswers.get(i) == "V"){
                numberOfCorrectAnswers++;
            }
        }
        JOptionPane.showMessageDialog(null, "Antal korrekta svar: " +numberOfCorrectAnswers);

    }

}
