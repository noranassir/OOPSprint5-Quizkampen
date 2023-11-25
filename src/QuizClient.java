import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class QuizClient extends JFrame implements Serializable, ActionListener {

    private JFrame frame = new JFrame("Quizkampen");
    private JLabel messageLabel = new JLabel("");
    private JButton[] categoryButtons = new JButton[3];
    private JButton[] answerButtons = new JButton[4];

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;


    public QuizClient(String serverAddress) throws Exception {
        try {
            socket = new Socket(serverAddress, 5554);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            initializeUI();
            play();
        }
     catch (IOException e) {
        throw new RuntimeException(e);
    }
}
        /*
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


        } */



    private void initializeUI() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(true);

        JPanel board = new JPanel();
        frame.getContentPane().add(board);

        messageLabel.setText("Waiting for players...");
        frame.getContentPane().add(messageLabel, "North");

        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i] = new JButton();
            categoryButtons[i].setVisible(false);
            categoryButtons[i].addActionListener(this);
            board.add(categoryButtons[i]);
        }

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JButton();
            answerButtons[i].setVisible(false);
            answerButtons[i].addActionListener(this);
            board.add(answerButtons[i]);
        }

        frame.setVisible(true);
    }

    public void play() throws Exception {
        String response;
        char tag = 'S';
        // char opponenttag = 'P';

        try {
            response = in.readLine();
            if (response.startsWith("Välkommen")) {
                tag = response.charAt(9);
               // opponenttag = (tag == 'X' ? 'Y' : 'X');
                frame.setTitle("Quizkampen - Spelare " + tag);
            }


            while (true) {
                response = in.readLine();    //den kommer fortsätta läsa vad servern ger oss, olika alternativ händer beroende på hur spelet utvecklas
                if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
                else if (response.startsWith("CATEGORY")) {
                    updateCategoryButtons(response.substring(8));
                }
                else if (response.startsWith("ANSWERS")) {
                    updateAnswerButtons(response.substring(8));
                } else if (response.startsWith("REMOVE_BUTTONS")) {
                    hideAllButtons();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void updateCategoryButtons(String categories) {
        String[] categoryArray = categories.split(",");
        for (int i = 0; i < categoryButtons.length; i++) {
            if (i < categoryArray.length) {
                categoryButtons[i].setVisible(true);
                categoryButtons[i].setText(categoryArray[i]);
            } else {
                categoryButtons[i].setVisible(false);
            }
        }
    }

    private void updateAnswerButtons(String answers) {
        String[] answerArray = answers.split(",");
        for (int i = 0; i < answerButtons.length; i++) {
            if (i < answerArray.length) {
                answerButtons[i].setVisible(true);
                answerButtons[i].setText(answerArray[i]);
            } else {
                answerButtons[i].setVisible(false);
            }
        }
    }

    private void hideAllButtons() {
        for (JButton button : categoryButtons) {
            button.setVisible(false);
        }
        for (JButton button : answerButtons) {
            button.setVisible(false);
        }
    }







    public static void main(String[] args) throws Exception {

        while(true) {

            String serverAddress = "127.0.0.1";
            QuizClient qc = new QuizClient(serverAddress);
            /*qc.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            qc.frame.setSize(600,400);
            qc.frame.setVisible(true);
            qc.frame.setResizable(true);
            qc.play(); */

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        out.println(clickedButton.getText());             //skickar ut texten på knappen
    }
}









/*

            while (true) {
                response = in.readLine();    //den kommer fortsätta läsa vad servern ger oss, olika alternativ händer beroende på hur spelet utvecklas
                if (response.startsWith("MESSAGE")) {
                    messageLabel.setText(response.substring(8));
                }
                else if (response.startsWith("KNAPP1")) {
                    kategori1.setVisible(true);
                    kategori1.addActionListener(this);                        //test
                    kategori1.setText(response.substring(7));
                }
                else if (response.startsWith("KNAPP2")) {
                    kategori2.setVisible(true);
                    kategori2.addActionListener(this);                    //test
                    kategori2.setText(response.substring(7));
                }
                else if (response.startsWith("KNAPP3")) {
                    kategori3.setVisible(true);
                    kategori3.addActionListener(this);                          //test
                    kategori3.setText((response.substring(7)));
                }
                else if (response.startsWith("TABORTKNAPP")) {
                    kategori1.setVisible(false);
                    kategori2.setVisible(false);
                    kategori3.setVisible(false);
                }
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

 */











