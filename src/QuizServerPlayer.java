import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

    Game game;

    Game gameklass;

    //boolean playerturn;

    String kategori = "MESSAGE Välj en kategori!";


    public QuizServerPlayer(Socket socket, char tag, Game game) {
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

    private Object lock = new Object();
    private String share = "";

    public void run() {

        try {
            game.ImportQuestions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        game.SortQuestions(); //!!!!!!!!! gör att knappar göms
        // game.AmountOfCategories();
        // game.QuizGame();


        output.println("MESSAGE all players connected");
        String[] kategorival;

        if (tag == 'X') {
            // game.printCategoryMessage();
            printCategoryMessage();


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
        ArrayList<Category> categorylist = game.getQuizCategoryList();
        Collections.shuffle(categorylist, new Random());


        for (int i = 0; i < categorylist.size(); i++) {
            Collections.shuffle(categorylist, new Random());
            output.println("CATEGORY " + categorylist.get(i).getCategoryName() + ", " + categorylist.get(i + 1).getCategoryName());


            //output.println(kategori);
            //output.println("CATEGORY 2000, Egypten, Hannamontana");
            //output.println(categorylist);
            //opponent.output.println("CATEGORY Istället för denna, text så ska vi, ha variabler eller?");


        }

    }
}