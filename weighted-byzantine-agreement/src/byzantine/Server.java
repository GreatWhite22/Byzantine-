package byzantine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor Lewis on 4/23/2017.
 */
public class Server extends Thread {
    GeneralsTable table;
    static Connector connector;
    public Server(){
        table = new GeneralsTable();
    }
    static int numberOfGenerals;
    public static void main(String[] args){
        Server server = new Server();
        numberOfGenerals = Integer.parseInt(args[0]);
        System.out.println("Server started...");
        try{
            ServerSocket listener = new ServerSocket(Symbols.ServerPort);
            Socket s;
            while((s = listener.accept()) != null){
                Thread t = new ServerThread(server.table, s);
                t.start();
            }
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
