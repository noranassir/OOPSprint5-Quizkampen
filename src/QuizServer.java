import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class QuizServer extends JFrame implements Serializable{

    JFrame jf = new JFrame();
    JPanel jp = new JPanel();
    JButton jb = new JButton("Hej");

    public QuizServer() {

        try (ServerSocket sSocket = new ServerSocket(5554);
             Socket socket = sSocket.accept();
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            JFrame jfsend = JFrame();
            out.writeObject(jfsend);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main (String[]args){

        QuizServer qs = new QuizServer();

    }

    public JFrame JFrame () {

        jf.add(jp);
        jp.add(jb);
        jf.setVisible(true);
        jf.setSize(400,300);
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);

        return jf;

    }



}
