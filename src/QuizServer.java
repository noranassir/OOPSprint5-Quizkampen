import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class QuizServer {

    int PORT = 55555;
    List<String> list = new ArrayList<String>();


    public QuizServer() {

        list.add("Ett");
        list.add("Två");
        list.add("Tre");
        list.add("Fyra");

        try(ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket = serverSocket.accept();
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {


                for(String no : list){
                    out.println(no);

                }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        QuizServer server = new QuizServer();
    }
}
