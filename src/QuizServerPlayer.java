import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

/* Här händer spelarlogik, där startar också threadsen som kör för båda våra spelare!
 */
public class QuizServerPlayer extends Thread {


    char tag;
    QuizServerPlayer opponent;

    Socket socket;
    BufferedReader input;
    PrintWriter output;

    QuizServer game;

    Game gameklass;

    //boolean playerturn;

    String kategori = "MESSAGE Välj en kategori!";


    public ArrayList<String> importList = new ArrayList<String>();
    public ArrayList<Category> quizCategoryList = new ArrayList<>();
    public ArrayList<Question> quizQuestionsList = new ArrayList<Question>();
    public ArrayList<Answer> quizAnswersList = new ArrayList<Answer>();
    public int amountOfRounds = 6;
    public int amountOfQuestions = 3;


    //Temporära listor för att modifieras

    public ArrayList<Integer> categoryListRandom = new ArrayList<>();
    public ArrayList<Question> quizQuestionRandomiser = new ArrayList<>();
    public ArrayList<Answer> quizAnswersAfterRand = new ArrayList<>();
    public ArrayList<Answer> randomisedAnswers = new ArrayList<>();


    //Diverse variabler för multipla metoder
    int amountOfCategories = 0;
    int categorySelected = 0;
    public int selectedCategory = 0;


    //gjorde en getter for categorylist
    public ArrayList<Category> getQuizCategoryList() {
        return quizCategoryList;
    }


    public void ImportQuestions() throws IOException {

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

    public void SortQuestions() {

        int category = 0;
        char tempChar;
        int questionNumber = 0;
        int answerNumber = 0;

        for (String tempStringFromList : importList) {
            if (tempStringFromList.length() > 0) {
                tempChar = tempStringFromList.charAt(0);

                if (tempChar == '#') {
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




    public void AmountOfCategories() {
        amountOfCategories = 0;

        for (int i = 0; i < quizCategoryList.size(); i++) {
            categoryListRandom.add(amountOfCategories);
            amountOfCategories++;
        }
    }



    public void CategorySelection(){

        Collections.shuffle(categoryListRandom, new Random());
        String categorySelector =
                quizCategoryList.get(categoryListRandom.get(0)).getCategoryName() +
                quizCategoryList.get(categoryListRandom.get(1)).getCategoryName() +
                quizCategoryList.get(categoryListRandom.get(2)).getCategoryName();
        try{
            categorySelected = Integer.parseInt(categorySelector);
        }
        catch (NumberFormatException e){
            e.printStackTrace();
        }
        categorySelected = categorySelected -1;
        selectedCategory = categoryListRandom.get(categorySelected);



    }

    public void ImportSelectedQuestions(){
        quizQuestionRandomiser.clear();
        for(Question q : quizQuestionsList){
            int temp1 = q.getQuizCategory()-1;
            if(temp1 == selectedCategory){
                quizQuestionRandomiser.add(q);
            }
        }

        Collections.shuffle(quizQuestionRandomiser, new Random());
    }
    public void ImportAnswers(){


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

    public void RandomiseAnswers(){

        randomisedAnswers.clear();
        for (int i = 0; i < 4; i++) {
            randomisedAnswers.add(quizAnswersAfterRand.get(i));
        }
        Collections.shuffle(randomisedAnswers, new Random());
        for (int i = 0; i < 4; i++) {
            quizAnswersAfterRand.remove(0);
        }
    }






























    public QuizServerPlayer(Socket socket, char tag, QuizServer game) {
        this.socket = socket;
        this.tag = tag;
        this.game = game;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Välkommen" + tag);
            output.println("MESSAGE väntar på spelare");

        } catch (IOException e) {
            System.out.println("Player missing");
            throw new RuntimeException(e);
        }
    }

    public void setOpponent(QuizServerPlayer opponent) {
        this.opponent = opponent;
    }

    public void getOpponent(QuizServerPlayer opponent) {
        this.opponent = opponent;
    }







    public void run() {


        try {
            ImportQuestions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SortQuestions();


        output.println("MESSAGE all players connected");
        String[] kategorival;

        if (tag == 'X') {
            // game.printCategoryMessage();
            printCategoryMessage();
            confirmCategory();


            //output.println("CATEGORY" + cat1 + cat2 + cat2);

            try {
                while (true) {
                    String inputtext = input.readLine();
                    if (Objects.equals(inputtext, " Egypten")) {           //detta ska vara variabler istället för hårdkodning
                        output.println("REMOVE_BUTTONS");
                        output.println("MESSAGE Du hade RÄTT");
                        opponent.output.println("MESSAGE bra jobbat!!!!");
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            opponent.output.println("KNAPP3 HAHA vi klarade det!!!!");
            try {
                char inputChar = (char) opponent.input.read();
                if (inputChar == 'm') {
                    opponent.output.println("MESSAGE DU tryckte på knappen");
                    opponent.output.println("TABORTKNAPP");
                    Thread.sleep(5000);
                    opponent.output.println("MESSAGE nu får du vänta igen:)");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            output.println("KNAPP2 HEJ HEJ");


        }

        if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");
/*

            while (true) {
                if (hello.equalsIgnoreCase("hej")) {
                    break;
                }
            }

            output.println("MESSAGE NU HÄNDE DET");
        }
*/
            // ...
        }
    }

    public void printCategoryMessage() {

        Collections.shuffle(quizCategoryList, new Random());

            output.println("CATEGORY " + quizCategoryList.get(0).getCategoryName()+ ", " + quizCategoryList.get(1).getCategoryName() + ", " + quizCategoryList.get(2).getCategoryName());


            //output.println(kategori);
            //output.println("CATEGORY 2000, Egypten, Hannamontana");
            //output.println(categorylist);
            //opponent.output.println("CATEGORY Istället för denna, text så ska vi, ha variabler eller?");


        }






        public void confirmCategory() {

            try {
                while (true) {
                    String inputtext = input.readLine().trim();                      //viktigt att trimma
                   // System.out.println(quizCategoryList.get(0).getCategoryName());
                   // System.out.println(inputtext);
                    //output.println("REMOVE_BUTTONS");

                    if (Objects.equals(inputtext, quizCategoryList.get(0).getCategoryName())) {        //detta ska vara variabler istället för hårdkodning

                        output.println("REMOVE_BUTTONS");
                        sendQuestions(quizCategoryList.get(0));
                        break;
                    }
                    else if (Objects.equals(inputtext, quizCategoryList.get(1).getCategoryName())){
                        output.println("REMOVE_BUTTONS");
                        sendQuestions(quizCategoryList.get(1));
                        break;

                    } else if (Objects.equals(inputtext, quizCategoryList.get(2).getCategoryName())) {

                        output.println("REMOVE_BUTTONS");
                        sendQuestions(quizCategoryList.get(2));
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }







    public void sendQuestions(Category categorychosen) {

        output.println("MESSAGE " + categorychosen.getCategoryName());
        output.println("ANSWERS, Val1, val2, val3, val4");








    }
    }
