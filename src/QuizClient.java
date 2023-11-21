import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class QuizClient extends JFrame implements Serializable {

    public QuizClient() {

        try(Socket socket = new Socket("127.0.0.1", 5554);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){


            Object jf = in.readObject();
            JFrame frame = new JFrame(String.valueOf(jf));
            //frame.setVisible(true);


        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {

        QuizClient qz = new QuizClient();

    }
}