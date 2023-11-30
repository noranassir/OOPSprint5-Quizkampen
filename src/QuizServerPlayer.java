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
    Game game;
    private int currentround = 0;
    int categorySelected = 0;
    public int selectedCategory = 0;
    private int amountOfRounds = 0;
    private int amountOfQuestions = 0;

    private List<String> scoreRoundX = new ArrayList<>();
    private List<String> scoreRoundY = new ArrayList<>();


    public QuizServerPlayer(Socket socket, char tag, Game game) {
        this.socket = socket;
        this.tag = tag;
        this.game = game;

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


    //här börjar X spelaren, Y spelaren är fast på väntar på din tur (när tråden körs)

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
            game.importSelectedQuestions();

            for (int j = 0; j < amountOfQuestions; j++) {

                game.importAnswers();
                game.randomiseAnswers();


                int userAnswerInt = 0;    //SPARAR SVAR
                String inputtext = "";

                output.println("MESSAGE " + game.quizQuestionRandomiser.get(j).getQuizQuestion());
                output.println("ANSWERS " + game.randomisedAnswers.get(0).getQuizAnswer() + ", " + game.randomisedAnswers.get(1).getQuizAnswer() + ", " +
                        game.randomisedAnswers.get(2).getQuizAnswer() + ", " + game.randomisedAnswers.get(3).getQuizAnswer());

                while (true) {
                    inputtext = input.readLine().trim();
                    if (inputtext.equals(game.randomisedAnswers.get(0).getQuizAnswer())) {
                        userAnswerInt = 1;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(1).getQuizAnswer())) {
                        userAnswerInt = 2;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(2).getQuizAnswer())) {
                        userAnswerInt = 3;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(3).getQuizAnswer())) {
                        userAnswerInt = 4;
                        break;

                    }
                }


                userAnswerInt = userAnswerInt - 1;
                Object tempAnswer = game.randomisedAnswers.get(userAnswerInt);
                for (Answer a : game.quizAnswersList) {
                    if (tempAnswer == a) {
                        if (a.getCorrectAnswer() == true) {
                            correctAnswersPerRoundX++;

                        }
                    }
                }
                sleepy();
            }


            String temp = "" + correctAnswersPerRoundX;
            scoreRoundX.add(temp);
            currentround++;


            while (true) {                                                  //efter rundan är klar, skrivs svaret ut
                output.println("REMOVE_BUTTONS");
                output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRoundX);
                output.println("CATEGORY Bra jobbat!");
                input.readLine();
                break;

            }

            totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRoundX;
            game.quizAnswersAfterRand.clear();
            game.categoryListRandom.remove(categorySelected);
            //bryter sig ur första


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


        Collections.shuffle(game.categoryListRandom, new Random());
        int categorySelector = 0;

        output.println("MESSAGE Välj en kategori");                                                                       //väljer bland knappar
        output.println("CATEGORY " + game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName());

        while (true) {
            String inputtext = input.readLine().trim();
            if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName())) {

                categorySelector = 1;
                output.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName())) {
                categorySelector = 2;
                output.println("REMOVE_BUTTONS");
                break;

            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName())) {
                categorySelector = 3;
                output.println("REMOVE_BUTTONS");
                break;
            }
        }

        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = game.categoryListRandom.get(categorySelected);

    }


  //här för Y spelaren köra med samma frågor som X hade
    public int opponentturn(int totalcorrectY) throws IOException, InterruptedException {

        opponent.output.println("SHIDE");
        int totalCorrectAnswersY = totalcorrectY;
        int correctAnswersPerRoundY = 0;


        for (int j = 0; j < amountOfQuestions; j++) {

            game.importAnswers();
            game.randomiseAnswers();

            int userAnswerInt = 0;    //SPARAR SVAR
            String inputtext = "";

            opponent.output.println("MESSAGE " + game.quizQuestionRandomiser.get(j).getQuizQuestion());
            opponent.output.println("ANSWERS " + game.randomisedAnswers.get(0).getQuizAnswer() + ", " + game.randomisedAnswers.get(1).getQuizAnswer() + ", " +
                    game.randomisedAnswers.get(2).getQuizAnswer() + ", " + game.randomisedAnswers.get(3).getQuizAnswer());

            while (true) {
                inputtext = opponent.input.readLine().trim();
                if (inputtext.equals(game.randomisedAnswers.get(0).getQuizAnswer())) {
                    userAnswerInt = 1;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(1).getQuizAnswer())) {
                    userAnswerInt = 2;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(2).getQuizAnswer())) {
                    userAnswerInt = 3;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(3).getQuizAnswer())) {
                    userAnswerInt = 4;
                    break;
                }
            }

            userAnswerInt = userAnswerInt - 1;
            Object tempAnswer = game.randomisedAnswers.get(userAnswerInt);
            for (Answer a : game.quizAnswersList) {
                if (tempAnswer == a) {
                    if (a.getCorrectAnswer() == true) {
                        correctAnswersPerRoundY++;
                    }
                }
            }
        }

        String temp = "" + correctAnswersPerRoundY;
        scoreRoundY.add(temp);


        while (true) {
            opponent.output.println("REMOVE_BUTTONS");
            opponent.output.println("SSHOW");
            output.println("SSHOW");

                opponent.output.println("SCORE Runda " + currentround + " poäng är " + scoreRoundY.get(scoreRoundY.size() - 1) +
                        " Motståndaren fick " + scoreRoundX.get(scoreRoundX.size() - 1));
                output.println("SCORE Runda " + currentround + " poäng är " + scoreRoundX.get(scoreRoundX.size() - 1) +
                        " Motståndaren fick " + scoreRoundY.get(scoreRoundY.size() - 1));


            opponent.output.println("CATEGORY Bra jobbat!");
            opponent.input.readLine();

            opponent.output.println("SHIDE");
            opponent.output.println("REMOVE_BUTTONS");

            break;
        }

        totalCorrectAnswersY = totalCorrectAnswersY + correctAnswersPerRoundY;
        game.quizAnswersAfterRand.clear();
        game.categoryListRandom.remove(categorySelected);
        return totalCorrectAnswersY;

    }


//denna är identisk till QuizGame, förutom att det är Y som väljer kategori

    public int QuizGameY(int totalcorrecty) throws IOException {

                                                                    //funkar inte oavsätt opponent eller main... konstigt
        int totalCorrectAnswersY = totalcorrecty;                      //måste sparas någonstans

        for (int i = 0; i < amountOfRounds; i++) {

            CategorySelectionY();                      //y väljer kategori

            int correctAnswersPerRoundY = 0;
            game.importSelectedQuestions();

            for (int j = 0; j < amountOfQuestions; j++) {

                game.importAnswers();
                game.randomiseAnswers();


                int userAnswerInt = 0;    //SPARAR SVAR
                String inputtext = "";

                opponent.output.println("MESSAGE " + game.quizQuestionRandomiser.get(j).getQuizQuestion());
                opponent.output.println("ANSWERS " + game.randomisedAnswers.get(0).getQuizAnswer() + ", " + game.randomisedAnswers.get(1).getQuizAnswer() + ", " +
                        game.randomisedAnswers.get(2).getQuizAnswer() + ", " + game.randomisedAnswers.get(3).getQuizAnswer());

                while (true) {
                    inputtext = opponent.input.readLine().trim();
                    if (inputtext.equals(game.randomisedAnswers.get(0).getQuizAnswer())) {
                        userAnswerInt = 1;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(1).getQuizAnswer())) {
                        userAnswerInt = 2;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(2).getQuizAnswer())) {
                        userAnswerInt = 3;
                        break;
                    } else if (inputtext.equals(game.randomisedAnswers.get(3).getQuizAnswer())) {
                        userAnswerInt = 4;
                        break;
                    }
                }


                userAnswerInt = userAnswerInt - 1;
                Object tempAnswer = game.randomisedAnswers.get(userAnswerInt);
                for (Answer a : game.quizAnswersList) {
                    if (tempAnswer == a) {
                        if (a.getCorrectAnswer() == true) {
                            correctAnswersPerRoundY++;
                                //sätter roundscore för Y spelare
                        }
                    }
                }
            }

                         //sätter Y score för denna runda till... denna rundas score
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
            game.quizAnswersAfterRand.clear();
            game.categoryListRandom.remove(categorySelected);
            break;                                              //bryter sig ur första

        }

        output.println("REMOVE_BUTTONS");
              return totalCorrectAnswersY;
    }

      //som sagt y väljer kategori

    public void CategorySelectionY() throws IOException {


        Collections.shuffle(game.categoryListRandom, new Random());
        int categorySelector = 0;


        opponent.output.println("MESSAGE välj en kategori!");                                                                       //väljer bland knappar
        opponent.output.println("CATEGORY " + game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName());

        while (true) {
            String inputtext = opponent.input.readLine().trim();
            if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName())) {

                categorySelector = 1;
                opponent.output.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName())) {
                categorySelector = 2;
                opponent.output.println("REMOVE_BUTTONS");
                break;

            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName())) {
                categorySelector = 3;
                opponent.output.println("REMOVE_BUTTONS");
                break;
            }
        }


        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = game.categoryListRandom.get(categorySelected);

    }


    //nu får X spela med kategori som valdes av Y

    public int opponentturnX(int correctanswerX) throws IOException {


        output.println("SHIDE");
        int totalCorrectAnswersX = correctanswerX;
        int correctAnswersPerRound = 0;


        for (int j = 0; j < amountOfQuestions; j++) {


            game.importAnswers();
            game.randomiseAnswers();

            int userAnswerInt = 0;    //SPARAR SVAR
            String inputtext = "";

            output.println("MESSAGE " + game.quizQuestionRandomiser.get(j).getQuizQuestion());
            output.println("ANSWERS " + game.randomisedAnswers.get(0).getQuizAnswer() + ", " + game.randomisedAnswers.get(1).getQuizAnswer() + ", " +
                    game.randomisedAnswers.get(2).getQuizAnswer() + ", " + game.randomisedAnswers.get(3).getQuizAnswer());

            while (true) {
                inputtext = input.readLine().trim();
                if (inputtext.equals(game.randomisedAnswers.get(0).getQuizAnswer())) {
                    userAnswerInt = 1;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(1).getQuizAnswer())) {
                    userAnswerInt = 2;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(2).getQuizAnswer())) {
                    userAnswerInt = 3;
                    break;
                } else if (inputtext.equals(game.randomisedAnswers.get(3).getQuizAnswer())) {
                    userAnswerInt = 4;
                    break;
                }
            }


            userAnswerInt = userAnswerInt - 1;
            Object tempAnswer = game.randomisedAnswers.get(userAnswerInt);
            for (Answer a : game.quizAnswersList) {
                if (tempAnswer == a) {
                    if (a.getCorrectAnswer() == true) {
                        correctAnswersPerRound++;
                    }
                }
            }
        }
        String temp = "" + correctAnswersPerRound;
        scoreRoundX.add(temp);



        while (true) {
            output.println("REMOVE_BUTTONS");
            output.println("SSHOW");

            opponent.output.println("SSHOW");

            output.println("SCORE runda " + currentround + "poäng är" + scoreRoundX.get(scoreRoundX.size() - 1) +
                    " motståndaren har " + scoreRoundY.get(scoreRoundY.size() - 1));
            opponent.output.println("SCORE  runda " + currentround + "poäng är" + scoreRoundY.get(scoreRoundY.size() - 1) +
                    " motståndaren har " + scoreRoundX.get(scoreRoundX.size() - 1));


            output.println("CATEGORY Bra jobbat!");
            input.readLine();
            output.println("REMOVE_BUTTONS");
            output.println("SHIDE");
            break;

        }


        totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRound;
        game.quizAnswersAfterRand.clear();
        game.categoryListRandom.remove(categorySelected);

        return totalCorrectAnswersX;
    }



    public void sleepy() throws InterruptedException {
        Thread.sleep(500);


    }

    public void setOpponent(QuizServerPlayer opponent) {
        this.opponent = opponent;
    }

    public void getOpponent(QuizServerPlayer opponent) {
        this.opponent = opponent;
    }


    public void run() {

        //glöm inte opponent.output ger Y saker

        if (tag == 'X') {

            try {
                game.importQuestions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            game.sortQuestions();
            game.amountOfCategories();

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


