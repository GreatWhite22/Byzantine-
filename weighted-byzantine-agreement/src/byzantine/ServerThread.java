package byzantine;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Connor Lewis on 4/23/2017.
 */
public class ServerThread extends Thread {
    GeneralsTable table;
    Socket theGeneral;
    public ServerThread(GeneralsTable table, Socket s){
        this.table = table;
        theGeneral = s;
    }
    public void run(){
        try {
            Scanner sc = new Scanner(theGeneral.getInputStream());
            PrintWriter pout = new PrintWriter(theGeneral.getOutputStream());
            String command = sc.nextLine();
            System.out.println("received:" + command);
            Scanner st = new Scanner(command);
            String tag = st.next();
            if(tag.equals("search")){
                InetSocketAddress addr = table.search(st.next());
                if(addr == null){
                    pout.println(0 + " " + "nullhost");
                }
                else{
                    pout.println(addr.getPort() + " " + addr.getHostName());
                }
            }else if(tag.equals("insert")){
                int pid = st.nextInt();
                String hostName = st.next();
                int port = st.nextInt();
                int retValue = table.insert(pid, hostName, port);
                pout.println(retValue);
            }else if(tag.equals("blockingFind")){
                InetSocketAddress addr = table.blockingFind(st.next());
                pout.println(addr.getPort() + " " + addr.getHostName());
            }else if(tag.equals("clear")){
                table.clear();
            }
            pout.flush();
            theGeneral.close();
        }catch (IOException e){
            System.out.println("Error in byzantine.ServerThread");
            System.err.println(e);
        }
    }
}
