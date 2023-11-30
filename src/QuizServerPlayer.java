import java.io.*;
import java.net.Socket;
import java.util.*;

/* Här händer spelarlogik, där startar också threadsen som kör för båda våra spelare!
 */
public class QuizServerPlayer extends Thread {


    char tag;

    //int roundScore;
    QuizServerPlayer opponent;



    Socket socket;
    BufferedReader input;
    PrintWriter output;

    QuizServer game;


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


    //gjorde en getter for categorylist
    public ArrayList<Category> getQuizCategoryList() {
        return quizCategoryList;
    }


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




                        }
                        else output.println("RÖD");

                    }

                }
                sleepy();


            }

            //roundScore = correctAnswersPerRoundX;                 //sätter X score för denna runda till... denna rundas score
            String temp = "" + correctAnswersPerRoundX;
            scoreRoundX.add(temp);
            currentround++;



            while (true) {                                                  //efter rundan är klar, skrivs svaret ut
                output.println("REMOVE_BUTTONS");
                output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRoundX);
                output.println("CATEGORY Bra jobbat!");
                input.readLine();
                break;

               // output.println("MESSAGE Poäng för runda" + index + " är lika med " + scoreRoundX);

            }

            //JOptionPane.showMessageDialog(null, "Antal rätt: " +correctAnswersPerRound);
            totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRoundX;
            quizAnswersAfterRand.clear();
            categoryListRandom.remove(categorySelected);
            //bryter sig ur första


            // JOptionPane.showMessageDialog(null, "Totalt antal rätt: " + totalCorrectAnswers);

            //opponent.output.println("CATEGORY " + quizCategoryList.get(categoryListRandom.get(0)).getCategoryName());

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

   //test merge


    public void CategorySelection() throws IOException {       //X spelaren som sagt väljer kategori


        Collections.shuffle(categoryListRandom, new Random());
        int categorySelector = 0;
        //     quizCategoryList.get(categoryListRandom.get(0)).getCategoryName() +
        //      quizCategoryList.get(categoryListRandom.get(1)).getCategoryName() +
        //     quizCategoryList.get(categoryListRandom.get(2)).getCategoryName();

        output.println("MESSAGE välj en kategori!");                                                                       //väljer bland knappar
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

        //try{
        //   categorySelected = Integer.parseInt(categorySelector);
        // }
        //catch (NumberFormatException e){
        //   e.printStackTrace();

        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = categoryListRandom.get(categorySelected);

    }





  //här för Y spelaren köra med samma frågor som X hade
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




                    }else opponent.output.println("RÖD2");



                }

            }
            sleepy();


        }
        //opponent.roundScore = correctAnswersPerRoundY;                 //sätter Y score för denna runda till... denna rundas score
        String temp = "" + correctAnswersPerRoundY;
        scoreRoundY.add(temp);


        while (true) {
            opponent.output.println("REMOVE_BUTTONS");
            opponent.output.println("SSHOW");
            output.println("SSHOW");

                opponent.output.println("SCORE  runda " + currentround + "poäng är" + scoreRoundY.get(scoreRoundY.size() - 1) +
                        " motståndaren har " + scoreRoundX.get(scoreRoundX.size() - 1));
                output.println("SCORE runda " + currentround + "poäng är" + scoreRoundX.get(scoreRoundX.size() - 1) +
                        " motståndaren har " + scoreRoundY.get(scoreRoundY.size() - 1));


            opponent.output.println("CATEGORY Bra jobbat!");
            opponent.input.readLine();
            //output.println("SHIDE");
            opponent.output.println("SHIDE");
            opponent.output.println("REMOVE_BUTTONS");
           // opponent.roundScore = 0;
            break;
        }

        //JOptionPane.showMessageDialog(null, "Antal rätt: " +correctAnswersPerRound);
        totalCorrectAnswersY = totalCorrectAnswersY + correctAnswersPerRoundY;
        quizAnswersAfterRand.clear();
        categoryListRandom.remove(categorySelected);
        return totalCorrectAnswersY;

    }


















//denna är identisk till QuizGame, förutom att det är Y som väljer kategori
public int QuizGameY(int totalcorrecty) throws IOException, InterruptedException {

                                                                    //funkar inte oavsätt opponent eller main... konstigt
        int totalCorrectAnswersY = totalcorrecty;                      //måste sparas någonstans

        for (int i = 0; i < amountOfRounds; i++) {

            CategorySelectionY();                      //y väljer kategori

            int correctAnswersPerRoundY = 0;
            ImportSelectedQuestions();

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


                           // opponent.roundScore = correctAnswersPerRoundY;         //sätter roundscore för Y spelare
                        }
                        else opponent.output.println("RÖD2");



                    }

                }
                sleepy();

            }

            //opponent.roundScore = correctAnswersPerRoundY;                 //sätter Y score för denna runda till... denna rundas score
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

            //JOptionPane.showMessageDialog(null, "Antal rätt: " +correctAnswersPerRound);
            totalCorrectAnswersY = totalCorrectAnswersY + correctAnswersPerRoundY;
            quizAnswersAfterRand.clear();
            categoryListRandom.remove(categorySelected);
            break;                                              //bryter sig ur första

        }
        // JOptionPane.showMessageDialog(null, "Totalt antal rätt: " + totalCorrectAnswers);

        //opponent.output.println("CATEGORY " + quizCategoryList.get(categoryListRandom.get(0)).getCategoryName());

        output.println("REMOVE_BUTTONS");
              return totalCorrectAnswersY;
    }





      //som sagt y väljer kategori

    public void CategorySelectionY() throws IOException {


        Collections.shuffle(categoryListRandom, new Random());
        int categorySelector = 0;
        //     quizCategoryList.get(categoryListRandom.get(0)).getCategoryName() +
        //      quizCategoryList.get(categoryListRandom.get(1)).getCategoryName() +
        //     quizCategoryList.get(categoryListRandom.get(2)).getCategoryName();

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

        //try{
        //   categorySelected = Integer.parseInt(categorySelector);
        // }
        //catch (NumberFormatException e){
        //   e.printStackTrace();

        categorySelected = categorySelector;
        categorySelected = categorySelected - 1;
        selectedCategory = categoryListRandom.get(categorySelected);

    }






    //nu får X spela med kategori som valdes av Y

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



                    }
                    else output.println("RÖD");



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

            output.println("SCORE runda " + currentround + "poäng är" + scoreRoundX.get(scoreRoundX.size() - 1) +
                    " motståndaren har " + scoreRoundY.get(scoreRoundY.size() - 1));
            opponent.output.println("SCORE  runda " + currentround + "poäng är" + scoreRoundY.get(scoreRoundY.size() - 1) +
                    " motståndaren har " + scoreRoundX.get(scoreRoundX.size() - 1));


            //output.println("CATEGORY Bra jobbat!");
            //opponent.input.readLine();
            //output.println("SHIDE");
            //opponent.output.println("SHIDE");
            output.println("CATEGORY Bra jobbat!");
            input.readLine();
            output.println("REMOVE_BUTTONS");
            output.println("SHIDE");
           // this.roundScore = 0;
            break;
            /*
            output.println("MESSAGE Antalet rätt för denna runda:  " + correctAnswersPerRound + "motståndaren fick" + opponent.roundScore);
            opponent.output.println("MESSAGE Antalet rätt för denna runda:  " + opponent.roundScore + "motståndaren fick: " + correctAnswersPerRound);
            output.println("CATEGORY Bra jobbat!");
            input.readLine();
            output.println("REMOVE_BUTTONS");
            opponent.roundScore = 0;  //test
            break;  */
        }

        //JOptionPane.showMessageDialog(null, "Antal rätt: " +correctAnswersPerRound);
        totalCorrectAnswersX = totalCorrectAnswersX + correctAnswersPerRound;
        quizAnswersAfterRand.clear();
        categoryListRandom.remove(categorySelected);
        // QuizGame();                                            //här startas kedjan om igen, måste spara poäng och göra loop av detta som ändras av properties

        return totalCorrectAnswersX;
    }










    public void sleepy() throws InterruptedException {
        Thread.sleep(500);


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

        //glöm inte opponent.output ger Y saker

        if (tag == 'X') {

            try {
                ImportQuestions();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SortQuestions();
            AmountOfCategories();



            // try {
            // CategorySelection();
            //} catch (IOException e) {
            //    throw new RuntimeException(e);
            //  }
            try {
                QuizGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // try {
             //   opponent.QuizGame();                //får moståndaren att spela
           // } catch (IOException e) {
             //   throw new RuntimeException(e);
            //}


        }

        if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");



            }
        }





    }


