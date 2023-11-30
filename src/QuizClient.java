import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class QuizClient extends JFrame implements Serializable, ActionListener {

    private JFrame frame = new JFrame("Quizkampen");
    private JPanel messagePanel = new JPanel(new FlowLayout());
    private JTextPane messageArea = new JTextPane();

    private JTextArea textarea = new JTextArea();
    private JButton[] categoryButtons = new JButton[3];
    private JButton[] answerButtons = new JButton[4];

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;



    public QuizClient(String serverAddress){
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


        StyledDocument centerMessage = messageArea.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        centerMessage.setParagraphAttributes(0, centerMessage.getLength(), center, false);

        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setText("Waiting for players...");
        messageArea.setFont(new Font("Serif", Font.PLAIN, 20));
        messageArea.setPreferredSize(new Dimension(550, 100));
        messageArea.setBackground(Color.WHITE);
        messageArea.setOpaque(true);
        messageArea.setEditable(false);


        textarea.setText("");
        textarea.setEditable(false);
        textarea.setPreferredSize(new Dimension(300,400));
        textarea.setVisible(false);

        messageArea.setFocusable(false);
        messagePanel.add(messageArea);
        boardQuestions.add(messagePanel, BorderLayout.CENTER);

        frame.getContentPane().add(boardQuestions, "North");
        frame.getContentPane().add(boardButtons, "Center");

        messagePanel.add(textarea);

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

    public void play() {
        String response;
        char tag = 'S';

        try {
            response = in.readLine();
            if (response.startsWith("Välkommen")) {
                tag = response.charAt(9);
                frame.setTitle("Quizkampen - Spelare " + tag);
            }

            while (true) {
                response = in.readLine();
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

                else if (response.startsWith("SCORE")) {

                    textarea.append(response.substring(6));
                    textarea.append("\n");
                } else if (response.startsWith("SSHOW")) {
                     messageArea.setVisible(false);
                    textarea.setVisible(true);
                } else if (response.startsWith("SHIDE")) {
                    messageArea.setVisible(true);
                    //textarea.setText(null);
                    textarea.setVisible(false);
                }
                else if(response.startsWith("RÖD")) {
                    for (int i = 0; i < answerButtons.length; i++) {
                        answerButtons[i].setVisible(true);
                        answerButtons[i].setBackground(Color.red);
                        answerButtons[i].setPreferredSize(new Dimension(300, 100));
                    }

                }else if(response.startsWith("RÖD2")){
                    for (int i = 0; i < answerButtons.length; i++) {
                        answerButtons[i].setVisible(true);
                        answerButtons[i].setBackground(Color.red);
                        answerButtons[i].setPreferredSize(new Dimension(300, 100));
                    }

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
                answerButtons[i].setPreferredSize(new Dimension(300, 100));
                answerButtons[i].setBackground(Color.WHITE);
                answerButtons[i].addActionListener(e -> {
                    JButton clickedButton = (JButton)e.getSource();
                    clickedButton.setBackground(Color.GREEN);
                });
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


    public static void main(String[] args) {

        while(true) {

            String serverAddress = "127.0.0.1";
            QuizClient qc = new QuizClient(serverAddress);


        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        out.println(clickedButton.getText());
    }
}
