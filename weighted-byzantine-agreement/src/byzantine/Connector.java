package byzantine; /**
 * Created by Connor Lewis on 4/23/2017.
 */

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Connector {
    ServerSocket listener; Socket[] link;
    ArrayList<Scanner> dataIn;
    ArrayList<PrintWriter> dataOut;
    General generalClient;
    public void Connect(int myId, int numberOfNeighbors)
            throws Exception {
        generalClient = new General();
        int numNeigh = numberOfNeighbors;
        link = new Socket[numNeigh];
        dataIn = new ArrayList<Scanner>();
        dataOut = new ArrayList<PrintWriter>();
        for(int i = 0; i < numberOfNeighbors; i++){
            dataIn.add(null);
            dataOut.add(null);
        }
        int localport = getLocalPort(myId);
        listener = new ServerSocket(localport);

		/* register my name in the name server */
        generalClient.insertName(myId, (InetAddress.getLocalHost())
                .getHostName(), localport);

		/* accept connections from all the smaller processes */
		int pid = 0;
        while (pid < numNeigh) {
            if (pid  < myId) {
                Socket s = listener.accept();
                Scanner din = new Scanner(s.getInputStream());
                Integer hisId = din.nextInt();
                System.out.println("Connecting to pid: " + hisId);
                String tag = din.next();
                if (tag.equals("hello")) {
                    link[hisId] = s;
                    dataIn.set(hisId, din);
                    dataOut.set(hisId, new PrintWriter(
                            s.getOutputStream()));
                }
                pid++;
            }else {
                break;
            }
        }
		/* contact all the bigger processes */
        while (pid < numNeigh) {
            System.out.println("Waiting for pid: " + pid);
            if (pid > myId) {
                InetSocketAddress addr = generalClient.searchName(
                        pid, true);
                //int i = neighbors.indexOf(pid);
                link[pid] = new Socket(addr.getHostName(), addr.getPort());
                dataOut.set(pid, new
                        PrintWriter(link[pid].getOutputStream()));
				/* send a hello message to P_i */
                dataOut.get(pid).println(myId);
                dataOut.get(pid).println("hello");
                dataOut.get(pid).flush();
                dataIn.set(pid, new Scanner(link[pid].getInputStream())); }
                pid++;
        }
    }
    int getLocalPort(int id) {return Symbols.ServerPort + 20 + id;	}
    public void closeSockets() {
        try {
            listener.close();
            for (Socket s : link) s.close();
            generalClient.clear();
        } catch (Exception e) { System.err.println(e); }
    }
}

