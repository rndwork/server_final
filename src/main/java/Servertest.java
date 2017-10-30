import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by rnd on 30.10.2017.
 */
public class Servertest {

    ArrayList clientOutputStreams;
    ArrayList<String> m_history;

    //Для отслеживания GIT
    //Еще одно изменение
    //Последнее изменение на сегодня

    public static void main (String[] args) {
        new Servertest().go();
    }

    public void go() {
        clientOutputStreams = new ArrayList();
        m_history = new ArrayList<String>();
        try {
            ServerSocket serverSock = new ServerSocket(10001);

            while(true) {
                Socket clientSocket = serverSock.accept();

                Charset charset = StandardCharsets.UTF_8;
                OutputStreamWriter osw = new OutputStreamWriter( clientSocket.getOutputStream(), charset );
                PrintWriter writer = new PrintWriter( new BufferedWriter( osw ) );

//                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

                writer.println("Welcome to the chat 7 kids.... Семеро Козлят");
                writer.flush();

                clientOutputStreams.add(writer);
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start() ;
                System.out.println("got a connection");
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    } // Закрываем go


    public class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket sock;

        public ClientHandler(Socket clientSocket) {

            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream(), StandardCharsets.UTF_8);
                reader = new BufferedReader(isReader);
            } catch(Exception ex) {ex.printStackTrace();}

        } // Закрываем конструктор

        public void run() {
            String message;

            tellHistory(m_history);

            try {

                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    m_history.add(message);

                    tellEveryone(message);
                } // Закрываем while
            } catch(Exception ex) {ex.printStackTrace();}
        } // Закрываем run
    } // Закрываем вложенный класс




    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while(it.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        } // Конец цикла while
    } // Закрываем tellEveryone

    public void tellHistory(ArrayList<String> history){


        try {
            PrintWriter writer1 = (PrintWriter) clientOutputStreams.get(clientOutputStreams.size() - 1);

            for (int i = 0; i < history.size(); i++) {
                writer1.println(history.get(i));
            }
            //Идея в том, что бы вызывать историю только для последнего PrintWriter
            //может быть getsize поставить вместо i - writer1.println(history.get(history.size()));

            writer1.flush();
        } catch(Exception ex) {
            ex.printStackTrace();
        }


    }

}
