import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {


    public  ServerListener() {

        try (ServerSocket serverS = new ServerSocket(5554);) {

            while(true){


                QuizServerPlayer playerX = new QuizServerPlayer(serverS.accept(), 'X');
                QuizServerPlayer playerY = new QuizServerPlayer(serverS.accept(), 'Y');

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

        ServerListener sl = new ServerListener();


    }
}