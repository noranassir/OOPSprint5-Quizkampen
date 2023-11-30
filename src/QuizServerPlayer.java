import java.io.*;
import java.net.Socket;
import java.util.*;

/* Här händer spelarlogik, där startar också threadsen som kör för båda våra spelare!
 */
public class QuizServerPlayer extends Thread {


    char tag;


    QuizServerPlayer opponent;


    Socket socket;
    BufferedReader input;
    PrintWriter output;


    private ArrayList<String> importList = new ArrayList<String>();
    private ArrayList<Category> quizCategoryList = new ArrayList<>();
    private ArrayList<Question> quizQuestionsList = new ArrayList<Question>();
    private ArrayList<Answer> quizAnswersList = new ArrayList<Answer>();
    private int amountOfRounds = 0;
    private int amountOfQuestions = 0;


    //Temporära listor för att modifieras

    private ArrayList<Integer> categoryListRandom = new ArrayList<>();
    private ArrayList<Question> quizQuestionRandomiser = new ArrayList<>();
    private ArrayList<Answer> quizAnswersAfterRand = new ArrayList<>();
    private ArrayList<Answer> randomisedAnswers = new ArrayList<>();

    private List<String> scoreRoundX = new ArrayList<>();

    private List<String> scoreRoundY = new ArrayList<>();

    private int currentround = 0;


    //Diverse variabler för multipla metoder
    int amountOfCategories = 0;
    int categorySelected = 0;
    public int selectedCategory = 0;

    public void ImportQuestions() throws IOException {


        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(".\\src\\Quiz.txt"))) {

            while (true) {
                String input = bufferedReader.readLine();

                if (input != null) {
                    importList.add(input);       //importlista är där vi får in allt i filen
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

    public void ImportSelectedQuestions() {
        quizQuestionRandomiser.clear();
        for (Question q : quizQuestionsList) {
            int temp1 = q.getQuizCategory() - 1;
            if (temp1 == selectedCategory) {
                quizQuestionRandomiser.add(q);
            }
        }

        Collections.shuffle(quizQuestionRandomiser, new Random());
    }


    public void ImportAnswers() {
        for (Question q : quizQuestionRandomiser) {
            int temp1 = q.getQuizCategory();
            int temp2 = q.getQuestionNumber();
            for (Answer a : quizAnswersList) {
                if (temp1 == a.getQuizCategory() && temp2 == a.getQuestionNumber()) {
                    quizAnswersAfterRand.add(a);
                }
            }

        }
    }

    public void RandomiseAnswers() {

        randomisedAnswers.clear();
        for (int i = 0; i < 4; i++) {
            randomisedAnswers.add(quizAnswersAfterRand.get(i));
        }
        Collections.shuffle(randomisedAnswers, new Random());
        for (int i = 0; i < 4; i++) {
            quizAnswersAfterRand.remove(0);
        }
    }


    public void QuizGame() throws IOException, InterruptedException {


        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(".\\src\\Settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        amountOfQuestions = Integer.parseInt(properties.getProperty("amountOfQuestions", "3"));
        amountOfRounds = Integer.parseInt(properties.getProperty("amountOfRounds", "3"));


        int totalCorrectAnswersX = 0;
        int totalCorrectAnswersY = 0;

        for (int i = 0; i < amountOfRounds; i++) {


            CategorySelection();                             //X spelaren väljer kategori push

            int correctAnswersPerRoundX = 0;
            ImportSelectedQuestions();

            for (int j = 0; j < amountOfQuestions; j++) {

                ImportAnswers();
                RandomiseAnswers();


                int userAnswerInt = 0;    //SPARAR SVAR
                String inputtext = "";

                output.println("MESSAGE " + quizQuestionRandomiser.get(j).getQuizQuestion());
                output.println("ANSWERS " + randomisedAnswers.get(0).getQuizAnswer() + ", " + randomisedAnswers.get(1).getQuizAnswer() + ", " +
                        randomisedAnswers.get(2).getQuizAnswer() + ", " + randomisedAnswers.get(3).getQuizAnswer());

                while (true) {
                    inputtext = input.readLine().trim();
                    if (inputtext.equals(randomisedAnswers.get(0).getQuizAnswer())) {
                        userAnswerInt = 1;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(1).getQuizAnswer())) {
                        userAnswerInt = 2;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(2).getQuizAnswer())) {
                        userAnswerInt = 3;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(3).getQuizAnswer())) {
                        userAnswerInt = 4;
                        break;

                    }
                }


                userAnswerInt = userAnswerInt - 1;
                Object tempAnswer = randomisedAnswers.get(userAnswerInt);
                for (Answer a : quizAnswersList) {
                    if (tempAnswer == a) {
                        if (a.getCorrectAnswer() == true) {
                            correctAnswersPerRoundX++;
                            sleepy();


                        } else output.println("RÖD");

                    }

                }
                sleepy();

            }


            String temp = "" + correctAnswersPerRoundX;
            scoreRoundX.add(temp);
            currentround++;


            while (true) {
                output.println("REMOVE_BUTTONS");
                output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRoundX);
                output.println("CATEGORY Bra jobbat!");
                input.readLine();
                break;

            }

            totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRoundX;
            quizAnswersAfterRand.clear();
            categoryListRandom.remove(categorySelected);


            opponent.output.println("REMOVE_BUTTONS");
            totalCorrectAnswersY = opponentturn(totalCorrectAnswersY);
            totalCorrectAnswersY = QuizGameY(totalCorrectAnswersY);
            totalCorrectAnswersX = opponentturnX(totalCorrectAnswersX);
        }

        output.println("REMOVE_BUTTONS");
        output.println("MESSAGE totala poäng" + ", " + totalCorrectAnswersX + "motståndaren fick: " + totalCorrectAnswersY);
        opponent.output.println("REMOVE_BUTTONS");
        opponent.output.println("SHIDE");
        opponent.output.println("MESSAGE totala poäng" + ", " + totalCorrectAnswersY + "motståndaren fick: " + totalCorrectAnswersX);
    }


    public void CategorySelection() throws IOException {       //X spelaren som sagt väljer kategori


        Collections.shuffle(categoryListRandom, new Random());
        int categorySelector = 0;


        output.println("MESSAGE Välj en kategori:");
        output.println("CATEGORY " + quizCategoryList.get(categoryListRandom.get(0)).getCategoryName() + ", " +
                quizCategoryList.get(categoryListRandom.get(1)).getCategoryName() + ", " +
                quizCategoryList.get(categoryListRandom.get(2)).getCategoryName());

        while (true) {
            String inputtext = input.readLine().trim();
            if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(0)).getCategoryName())) {

                categorySelector = 1;
                output.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(1)).getCategoryName())) {
                categorySelector = 2;
                output.println("REMOVE_BUTTONS");
                break;

            } else if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(2)).getCategoryName())) {
                categorySelector = 3;
                output.println("REMOVE_BUTTONS");
                break;
            }
        }


        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = categoryListRandom.get(categorySelected);

    }


    public int opponentturn(int totalcorrectY) throws IOException, InterruptedException {

        opponent.output.println("SHIDE");
        int totalCorrectAnswersY = totalcorrectY;
        int correctAnswersPerRoundY = 0;


        for (int j = 0; j < amountOfQuestions; j++) {


            ImportAnswers();
            RandomiseAnswers();

            int userAnswerInt = 0;    //SPARAR SVAR
            String inputtext = "";

            opponent.output.println("MESSAGE " + quizQuestionRandomiser.get(j).getQuizQuestion());
            opponent.output.println("ANSWERS " + randomisedAnswers.get(0).getQuizAnswer() + ", " + randomisedAnswers.get(1).getQuizAnswer() + ", " +
                    randomisedAnswers.get(2).getQuizAnswer() + ", " + randomisedAnswers.get(3).getQuizAnswer());

            while (true) {
                inputtext = opponent.input.readLine().trim();
                if (inputtext.equals(randomisedAnswers.get(0).getQuizAnswer())) {
                    userAnswerInt = 1;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(1).getQuizAnswer())) {
                    userAnswerInt = 2;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(2).getQuizAnswer())) {
                    userAnswerInt = 3;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(3).getQuizAnswer())) {
                    userAnswerInt = 4;
                    break;
                }
            }


            userAnswerInt = userAnswerInt - 1;
            Object tempAnswer = randomisedAnswers.get(userAnswerInt);
            for (Answer a : quizAnswersList) {
                if (tempAnswer == a) {
                    if (a.getCorrectAnswer() == true) {
                        correctAnswersPerRoundY++;
                        sleepy();


                    } else opponent.output.println("RÖD2");


                }

            }
            sleepy();


        }

        String temp = "" + correctAnswersPerRoundY;
        scoreRoundY.add(temp);


        while (true) {
            opponent.output.println("REMOVE_BUTTONS");
            opponent.output.println("SSHOW");
            output.println("SSHOW");

            opponent.output.println("SCORE KATEGORI " + currentround);
            opponent.output.println("SCORE Dina poäng: " + scoreRoundY.get(scoreRoundY.size() - 1));
            opponent.output.println("SCORE Motståndares poäng: " + scoreRoundX.get(scoreRoundX.size() - 1));
            output.println("SCORE KATEGORI " + currentround);
            output.println("SCORE Dina poäng: " + scoreRoundY.get(scoreRoundY.size() - 1));
            output.println("SCORE Motståndares poäng: " + scoreRoundX.get(scoreRoundX.size() - 1));


            opponent.output.println("CATEGORY Bra jobbat!");
            opponent.input.readLine();

            opponent.output.println("SHIDE");
            opponent.output.println("REMOVE_BUTTONS");

            break;
        }


        totalCorrectAnswersY = totalCorrectAnswersY + correctAnswersPerRoundY;
        quizAnswersAfterRand.clear();
        categoryListRandom.remove(categorySelected);
        return totalCorrectAnswersY;

    }


    public int QuizGameY(int totalcorrecty) throws IOException, InterruptedException {

        int totalCorrectAnswersY = totalcorrecty;

        for (int i = 0; i < amountOfRounds; i++) {

            CategorySelectionY();

            int correctAnswersPerRoundY = 0;
            ImportSelectedQuestions();

            for (int j = 0; j < amountOfQuestions; j++) {

                ImportAnswers();
                RandomiseAnswers();


                int userAnswerInt = 0;
                String inputtext = "";

                opponent.output.println("MESSAGE " + quizQuestionRandomiser.get(j).getQuizQuestion());
                opponent.output.println("ANSWERS " + randomisedAnswers.get(0).getQuizAnswer() + ", " + randomisedAnswers.get(1).getQuizAnswer() + ", " +
                        randomisedAnswers.get(2).getQuizAnswer() + ", " + randomisedAnswers.get(3).getQuizAnswer());

                while (true) {
                    inputtext = opponent.input.readLine().trim();
                    if (inputtext.equals(randomisedAnswers.get(0).getQuizAnswer())) {
                        userAnswerInt = 1;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(1).getQuizAnswer())) {
                        userAnswerInt = 2;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(2).getQuizAnswer())) {
                        userAnswerInt = 3;
                        break;
                    } else if (inputtext.equals(randomisedAnswers.get(3).getQuizAnswer())) {
                        userAnswerInt = 4;
                        break;
                    }
                }


                userAnswerInt = userAnswerInt - 1;
                Object tempAnswer = randomisedAnswers.get(userAnswerInt);
                for (Answer a : quizAnswersList) {
                    if (tempAnswer == a) {
                        if (a.getCorrectAnswer() == true) {
                            correctAnswersPerRoundY++;
                            sleepy();



                        } else opponent.output.println("RÖD2");


                    }

                }
                sleepy();
            }

            String temp = "" + correctAnswersPerRoundY;
            scoreRoundY.add(temp);
            currentround++;


            while (true) {
                opponent.output.println("REMOVE_BUTTONS");
                opponent.output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRoundY);
                opponent.output.println("CATEGORY Bra jobbat!");
                opponent.input.readLine();
                break;
            }


            totalCorrectAnswersY = totalCorrectAnswersY + correctAnswersPerRoundY;
            quizAnswersAfterRand.clear();
            categoryListRandom.remove(categorySelected);
            break;                                              //bryter sig ur första

        }


        output.println("REMOVE_BUTTONS");
        return totalCorrectAnswersY;
    }




    public void CategorySelectionY() throws IOException {

        Collections.shuffle(categoryListRandom, new Random());
        int categorySelector = 0;


        opponent.output.println("MESSAGE välj en kategori!");                                                                       //väljer bland knappar
        opponent.output.println("CATEGORY " + quizCategoryList.get(categoryListRandom.get(0)).getCategoryName() + ", " +
                quizCategoryList.get(categoryListRandom.get(1)).getCategoryName() + ", " +
                quizCategoryList.get(categoryListRandom.get(2)).getCategoryName());

        while (true) {
            String inputtext = opponent.input.readLine().trim();
            if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(0)).getCategoryName())) {

                categorySelector = 1;
                opponent.output.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(1)).getCategoryName())) {
                categorySelector = 2;
                opponent.output.println("REMOVE_BUTTONS");
                break;

            } else if (inputtext.equals(quizCategoryList.get(categoryListRandom.get(2)).getCategoryName())) {
                categorySelector = 3;
                opponent.output.println("REMOVE_BUTTONS");
                break;
            }
        }


        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = categoryListRandom.get(categorySelected);

    }


    public int opponentturnX(int correctanswerX) throws IOException, InterruptedException {


        output.println("SHIDE");
        int totalCorrectAnswersX = correctanswerX;
        int correctAnswersPerRound = 0;


        for (int j = 0; j < amountOfQuestions; j++) {


            ImportAnswers();
            RandomiseAnswers();

            int userAnswerInt = 0;    //SPARAR SVAR
            String inputtext = "";

            output.println("MESSAGE " + quizQuestionRandomiser.get(j).getQuizQuestion());
            output.println("ANSWERS " + randomisedAnswers.get(0).getQuizAnswer() + ", " + randomisedAnswers.get(1).getQuizAnswer() + ", " +
                    randomisedAnswers.get(2).getQuizAnswer() + ", " + randomisedAnswers.get(3).getQuizAnswer());

            while (true) {
                inputtext = input.readLine().trim();
                if (inputtext.equals(randomisedAnswers.get(0).getQuizAnswer())) {
                    userAnswerInt = 1;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(1).getQuizAnswer())) {
                    userAnswerInt = 2;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(2).getQuizAnswer())) {
                    userAnswerInt = 3;
                    break;
                } else if (inputtext.equals(randomisedAnswers.get(3).getQuizAnswer())) {
                    userAnswerInt = 4;
                    break;
                }
            }
            //

            userAnswerInt = userAnswerInt - 1;
            Object tempAnswer = randomisedAnswers.get(userAnswerInt);
            for (Answer a : quizAnswersList) {
                if (tempAnswer == a) {
                    if (a.getCorrectAnswer() == true) {
                        correctAnswersPerRound++;
                        sleepy();


                    } else output.println("RÖD");


                }
            }
            sleepy();

        }
        String temp = "" + correctAnswersPerRound;
        scoreRoundX.add(temp);


        while (true) {
            output.println("REMOVE_BUTTONS");
            output.println("SSHOW");

            opponent.output.println("SSHOW");

            output.println("SCORE KATEGORI " + currentround);
            output.println("SCORE Dina poäng: " + scoreRoundY.get(scoreRoundY.size() - 1));
            output.println("SCORE Motståndares poäng: " + scoreRoundX.get(scoreRoundX.size() - 1));
            opponent.output.println("SCORE KATEGORI " + currentround);
            opponent.output.println("SCORE Dina poäng: " + scoreRoundY.get(scoreRoundY.size() - 1));
            opponent.output.println("SCORE Motståndares poäng: " + scoreRoundX.get(scoreRoundX.size() - 1));


            output.println("CATEGORY Bra jobbat!");
            input.readLine();
            output.println("REMOVE_BUTTONS");
            output.println("SHIDE");

            break;

        }

        totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRound;
        quizAnswersAfterRand.clear();
        categoryListRandom.remove(categorySelected);


        return totalCorrectAnswersX;
    }


    public void sleepy() throws InterruptedException {
        Thread.sleep(500);


    }

    public QuizServerPlayer(Socket socket, char tag) {
        this.socket = socket;
        this.tag = tag;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println("Välkommen" + tag);
            output.println("MESSAGE Väntar på spelare");

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


        if (tag == 'X') {

            try {
                ImportQuestions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SortQuestions();
            AmountOfCategories();



            try {
                QuizGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
        if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");


        }
    }
}


