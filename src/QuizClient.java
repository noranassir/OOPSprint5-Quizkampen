import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class QuizClient extends JFrame implements Serializable, ActionListener {

    private JFrame frame = new JFrame("Quizkampen");
    private JPanel messagePanel = new JPanel(new FlowLayout());
    private JTextPane messageArea = new JTextPane();
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

    private void initializeUI() {
        int r = 51;
        int g = 204;
        int b = 255;
        Color custumColor = new Color(r, g, b);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setResizable(true);
        frame.setBackground(Color.BLACK);
        frame.setForeground(Color.BLACK);

        JPanel boardQuestions = new JPanel(new FlowLayout());
        boardQuestions.setOpaque(true);
        boardQuestions.setBackground(custumColor);
        boardQuestions.setForeground(custumColor);

        JPanel boardButtons = new JPanel(new GridBagLayout());
        boardButtons.setOpaque(true);
        boardButtons.setBackground(custumColor);
        boardButtons.setForeground(custumColor);

        JLabel boardScore = new JLabel("SCORE: ");
        boardScore.setOpaque(true);
        boardScore.setBackground(custumColor);
        boardScore.setForeground(Color.BLACK);

        //Centerar texten
        StyledDocument centerMessage = messageArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        centerMessage.setParagraphAttributes(0, centerMessage.getLength(), center, false);

        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setText("Waiting for players...");
        messageArea.setFont(new Font("Serif", Font.PLAIN, 20));
        //messageArea.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setPreferredSize(new Dimension(550, 100));
        messageArea.setBackground(Color.WHITE);
        messageArea.setOpaque(true);
        messageArea.setEditable(false);
        //messageArea.setLineWrap(true);
        //messageArea.setWrapStyleWord(true);

        messageArea.setFocusable(false);
        messagePanel.add(messageArea);
        boardQuestions.add(messagePanel, BorderLayout.CENTER);

        frame.getContentPane().add(boardQuestions, "North");
        frame.getContentPane().add(boardButtons, "Center");
        frame.getContentPane().add(boardScore, "South");



        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);


        for (int i = 0; i < categoryButtons.length; i++) {
            categoryButtons[i] = new JButton();
            categoryButtons[i].setVisible(false);
            categoryButtons[i].addActionListener(this);
            categoryButtons[i].setPreferredSize(new Dimension(150, 75));
            boardButtons.add(categoryButtons[i], gbc);
            gbc.gridx++;

            if (gbc.gridx > 1) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
        }

        gbc.gridx = 0;
        gbc.gridy++;

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JButton();
            answerButtons[i].setVisible(false);
            answerButtons[i].addActionListener(this);
            answerButtons[i].setPreferredSize(new Dimension(150, 75));
            boardButtons.add(answerButtons[i], gbc);
            gbc.gridx++;

            if (gbc.gridx > 1) {
                gbc.gridx = 0;
                gbc.gridy++;
            }
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
                    messageArea.setText(response.substring(8));
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
                //categoryButtons[i].setText("<html><center><div style='white-space: nowrap;'>" + categoryArray[i] + "</div></center></html>");
                categoryButtons[i].setPreferredSize(new Dimension(200, 100));
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
                //answerButtons[i].setText("<html><center><div style='white-space: nowrap;'>" + answerArray[i] + "</div></center></html>");
                answerButtons[i].setPreferredSize(new Dimension(300, 100));

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