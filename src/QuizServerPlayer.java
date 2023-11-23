import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* Här händer spelarlogik, där startar också threadsen som kör för båda våra spelare!
 */
public class QuizServerPlayer extends Thread{


    char tag;
    QuizServerPlayer opponent;

    Socket socket;
    BufferedReader input;
    PrintWriter output;

    QuizServer game;

    boolean playerturn;



    public QuizServerPlayer(Socket socket, char tag, QuizServer game){
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


        output.println("MESSAGE all players connected");
        String hello = "";

        if (tag == 'X') {
            output.println("MESSAGE Välj en kategori!");
            output.println("KNAPP1 Istället för denna");
            output.println("KNAPP2 text så ska vi");
            output.println("KNAPP3 ha variabler eller?");

            try {
                char inputChar = (char) input.read();
                if (inputChar == 'y') {
                    output.println("TABORTKNAPP");
                    output.println("MESSAGE Du hade RÄTT");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            hello = "hej";




        }

        if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");


            while (true) {
                if (hello.equalsIgnoreCase("hej")) {
                    break;
                }
            }

            output.println("MESSAGE NU HÄNDE DET");
        }

        // ...
    }
}