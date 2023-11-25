import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {

    public ServerListener() {

        try (ServerSocket serverS = new ServerSocket(5554);) {

            while(true){

                Game game = new Game();

                QuizServerPlayer playerX = new QuizServerPlayer(serverS.accept(), 'X', game);
                QuizServerPlayer playerY = new QuizServerPlayer(serverS.accept(), 'Y', game);

                playerX.setOpponent(playerY);
                playerY.setOpponent(playerX);
                //game.currentPlayer = playerX;      //currentPlayer metod, är första spelaren
                playerX.start();
                playerY.start();


            }


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }   //test






    public static void main(String[] args) {

        QuizServer game = new QuizServer();
        String yo = "hej";
        yo = game.testingMethod(yo);


        System.out.println(yo);
        ServerListener sl = new ServerListener();


    }
}