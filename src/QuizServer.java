import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer extends Thread implements Serializable{


    JFrame jf = new JFrame();
    JPanel jp = new JPanel();
    JButton jb = new JButton("Hej");

    String hej;


    Socket s;

    public QuizServer(Socket s){
        this.s = s;

    }

    public QuizServer(){}


    public String testingMethod (String heyyy) {

        heyyy = "nope";

        return heyyy;


    }



    //ServerSocket sSocket = new ServerSocket(5554);
    //Socket socket = sSocket.accept();


    public void run() {

        try (
                ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));) {

            String hello = "hey";
            out.writeObject(hello);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}