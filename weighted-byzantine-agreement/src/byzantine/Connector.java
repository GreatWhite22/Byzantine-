package byzantine; /**
 * Created by Connor Lewis on 4/23/2017.
 */
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector {
    ServerSocket listener; Socket[] link;
    public ObjectInputStream[] dataIn;
    public ObjectOutputStream[] dataOut;
    General generalClient;
    public void Connect(int myId, double weight, int proposedValue, int numberOfNeighbors, int portNum)
            throws Exception {
        generalClient = new General(myId, weight, proposedValue, numberOfNeighbors);
        int numNeigh = numberOfNeighbors;
        link = new Socket[numNeigh];
        dataIn = new ObjectInputStream[numNeigh];
        dataOut = new ObjectOutputStream[numNeigh];
       // int localport = getLocalPort(myId);
        listener = new ServerSocket(portNum);

		/* register my name in the name server */
        generalClient.insertName(myId, (InetAddress.getLocalHost())
                .getHostName(), portNum);

		/* accept connections from all the smaller processes */
		int pid = 0;
        while (pid < numNeigh) {
            if (pid  < myId) {
                Socket s = listener.accept();
                InputStream is = s.getInputStream();
                ObjectInputStream din = new ObjectInputStream(is);
                Integer hisId = (Integer) din.readObject();
                //int i = neighbors.indexOf(hisId);
                String tag = (String) din.readObject();
                if (tag.equals("hello")) {
                    link[hisId] = s;
                    dataIn[hisId] = din;
                    dataOut[hisId] = new ObjectOutputStream(
                            s.getOutputStream()); }
                pid++;
            }
            break;
        }
		/* contact all the bigger processes */
        while (pid < numNeigh) {
            if (pid > myId) {
                InetSocketAddress addr = generalClient.searchName(
                        pid, true);
                //int i = neighbors.indexOf(pid);
                link[pid] = new Socket(addr.getHostName(), addr.getPort());
                dataOut[pid] = new
                        ObjectOutputStream(link[pid].getOutputStream());
				/* send a hello message to P_i */
                dataOut[pid].writeObject(new Integer(myId));
                dataOut[pid].writeObject(new String("hello"));
                dataOut[pid].flush();
                dataIn[pid] = new ObjectInputStream(link[pid].getInputStream()); }
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

