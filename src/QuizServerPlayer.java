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

    public void run() {

        //output.println("du är spelare" + tag);

        output.println("MESSAGE all players connected");

        if (tag == 'X') {
            output.println("MESSAGE Välj en kategori!");    //här lägger vi in frågan, och fixar med knappar också
            output.println("KNAPP1 Istället för denna");
            output.println("KNAPP2 text så ska vi");
            output.println("KNAPP3 ha variabler eller?");
        }
        if (tag == 'Y') {
            output.println("MESSAGE Väntar på din tur...");
                                                                 //kanske kan pausa med thread wait här när det behövs? osäker
        }


    }
}