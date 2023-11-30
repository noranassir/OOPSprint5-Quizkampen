import java.io.*;
import java.net.Socket;
import java.util.*;

public class QuizServerPlayer extends Thread {
    char tag;
    QuizServerPlayer opponent;
    Socket socket;
    BufferedReader input;
    PrintWriter output;
    Game game;
    private int currentround = 0;
    int categorySelected = 0;
    //public int selectedCategory = 0;
    private int amountOfRounds = 0;
    private int amountOfQuestions = 0;

    int totalCorrectAnswersX = 0;
    int totalCorrectAnswersY = 0;

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

    public void QuizGame() throws IOException, InterruptedException {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(".\\src\\Settings.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        amountOfQuestions = Integer.parseInt(properties.getProperty("amountOfQuestions", "3"));
        amountOfRounds = Integer.parseInt(properties.getProperty("amountOfRounds", "3"));

        for (int i = 0; i < amountOfRounds; i++) {
            categorySelection(input, output);

            int correctAnswersPerRound = 0;
            game.importSelectedQuestions();

            for (int j = 0; j < amountOfQuestions; j++) {
                game.importAnswers();
                game.randomiseAnswers();

                int userAnswerInt = getUserAnswer();
                Object tempAnswer = game.randomisedAnswers.get(userAnswerInt);

                for (Answer a : game.quizAnswersList) {
                    if (tempAnswer == a && a.getCorrectAnswer()) {
                        correctAnswersPerRound++;
                    }
                }

                sleepy();
            }

            addRoundScore(correctAnswersPerRound, tag == 'X' ? scoreRoundX : scoreRoundY);

            while (true) {
                output.println("REMOVE_BUTTONS");
                output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRound);
                output.println("CATEGORY Bra jobbat!");
                input.readLine();
                break;
            }

            int totalCorrectOpponent = tag == 'X' ? opponentturnY(totalCorrectAnswersY) : opponentturnX(totalCorrectAnswersX);
            totalCorrectAnswersX += (tag == 'X') ? correctAnswersPerRound : 0;
            totalCorrectAnswersY += (tag == 'Y') ? correctAnswersPerRound : 0;

            game.quizAnswersAfterRand.clear();
            game.categoryListRandom.remove(categorySelected);
        }

        output.println("REMOVE_BUTTONS");
        output.println("MESSAGE Totala poäng" + ", " + totalCorrectAnswersX + " Motståndaren fick: " + totalCorrectAnswersY);
        opponent.output.println("REMOVE_BUTTONS");
        opponent.output.println("SHIDE");
        opponent.output.println("MESSAGE Totala poäng" + ", " + totalCorrectAnswersY + " Motståndaren fick: " + totalCorrectAnswersX);
    }

    private int getUserAnswer() throws IOException {
        int userAnswerInt = 0;
        String inputtext = "";

        output.println("MESSAGE " + game.quizQuestionRandomiser.get(currentround).getQuizQuestion());
        output.println("ANSWERS " + game.randomisedAnswers.get(0).getQuizAnswer() + ", " +
                game.randomisedAnswers.get(1).getQuizAnswer() + ", " +
                game.randomisedAnswers.get(2).getQuizAnswer() + ", " +
                game.randomisedAnswers.get(3).getQuizAnswer());

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
        return userAnswerInt - 1;
    }

    private void addRoundScore(int correctAnswersPerRound, List<String> scoreList) {
        String temp = String.valueOf(correctAnswersPerRound);
        scoreList.add(temp);
        currentround++;
    }

    //Metod som tar spelares input eller output som inparametrar och generererar kategorival
    public void categorySelection(BufferedReader playerInput, PrintWriter playerOutput) throws IOException {
        Collections.shuffle(game.categoryListRandom, new Random());
        int categorySelector = 0;

        playerOutput.println("MESSAGE Välj en kategori"); // Adjust the message as needed
        playerOutput.println("CATEGORY " +
                game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName() + ", " +
                game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName());

        while (true) {
            String inputtext = playerInput.readLine().trim();
            if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(0)).getCategoryName())) {
                categorySelector = 1;
                playerOutput.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(1)).getCategoryName())) {
                categorySelector = 2;
                playerOutput.println("REMOVE_BUTTONS");
                break;
            } else if (inputtext.equals(game.quizCategoryList.get(game.categoryListRandom.get(2)).getCategoryName())) {
                categorySelector = 3;
                playerOutput.println("REMOVE_BUTTONS");
                break;
            }
        }

        categorySelected = categorySelector - 1;
        game.selectedCategory = game.categoryListRandom.get(categorySelected);
    }


  //här för Y spelaren köra med samma frågor som X hade
    public int opponentturnY(int totalcorrectY) throws IOException, InterruptedException {

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
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        } else if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");
        }
    }

}


