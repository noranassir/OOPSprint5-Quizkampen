import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class QuizClient extends JFrame implements Serializable {

    private JFrame frame = new JFrame("Quizkampen");
    private JLabel messageLabel = new JLabel("");
    private JButton kategori1 = new JButton("");
    private JButton kategori2 = new JButton("");
    private JButton kategori3 = new JButton("");
    private JButton alternativ1 = new JButton("");
    private JButton alternativ2 = new JButton("");
    private JButton alternativ3 = new JButton("");
    private JButton alternativ4 = new JButton("");



    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public QuizClient(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 5554);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        {


            messageLabel.setText("Väntar på spelare");
            JPanel board = new JPanel();
            frame.getContentPane().add(messageLabel, "North");


            //adda knappar här under? eller senare

            frame.getContentPane().add(board, "Center");

            kategori1.setVisible(false);            //knappar
            kategori2.setVisible(false);
            kategori3.setVisible(false);

            board.add(kategori1);
            board.add(kategori2);
            board.add(kategori3);


        }
    }





    public void play() throws Exception {
        String response;
        char tag = 'S';
        char opponenttag = 'P';

        try {
            response = in.readLine();
            if (response.startsWith("Välkommen")) {
                tag = response.charAt(9);
                opponenttag = (tag == 'X' ? 'Y' : 'X');
                frame.setTitle("Quizkampen - Spelare " + tag);

            }

            while (true) {
                response = in.readLine();    //den kommer fortsätta läsa vad servern ger oss, olika alternativ händer beroende på hur spelet utvecklas
                if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
                else if (response.startsWith("KNAPP1")) {
                    kategori1.setVisible(true);
                    kategori1.setText(response.substring(7));
                }
                else if (response.startsWith("KNAPP2")) {
                    kategori2.setVisible(true);
                    kategori2.setText(response.substring(7));
                }
                else if (response.startsWith("KNAPP3")) {
                    kategori3.setVisible(true);
                    kategori3.setText((response.substring(7)));
                }






            }










        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }






    public static void main(String[] args) throws Exception {

        while(true) {

            String serverAddress = "127.0.0.1";
            QuizClient qc = new QuizClient(serverAddress);
            qc.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            qc.frame.setSize(600,400);
            qc.frame.setVisible(true);
            qc.frame.setResizable(true);
            qc.play();

        }
    }
}