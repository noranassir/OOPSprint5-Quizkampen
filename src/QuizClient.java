import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class QuizClient extends JFrame implements Serializable {

    public QuizClient() {
        try (Socket socket = new Socket("127.0.0.1", 5554);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send data to the server
            //out.println("Hello, server!");

            // Receive data from the server
            while(true) {
                String response = in.readLine();
                System.out.println("Received from server: " + response);
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }









    public static void main(String[] args) {

        QuizClient qz = new QuizClient();

    }
}