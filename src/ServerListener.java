import java.io.IOException;
import java.net.ServerSocket;

public class ServerListener {

    public  ServerListener() {

        try (ServerSocket serverS = new ServerSocket(5554);) {

            while(true){
                QuizServer game = new QuizServer();

                QuizServerPlayer playerX = new QuizServerPlayer(serverS.accept(), 'X', game);
                QuizServerPlayer playerY = new QuizServerPlayer(serverS.accept(), 'Y', game);

                playerX.setOpponent(playerY);
                playerY.setOpponent(playerX);

                playerX.start();
                playerY.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        QuizServer game = new QuizServer();
        String yo = "hej";
        yo = game.testingMethod(yo);

        System.out.println(yo);
        ServerListener sl = new ServerListener();
    }
}