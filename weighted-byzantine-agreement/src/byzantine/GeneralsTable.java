package byzantine;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by Connor Lewis on 4/23/2017.
 */
public class GeneralsTable {
    class GeneralEntry {
        public int pid;
        public InetSocketAddress addr;
        public GeneralEntry(int pName, String host, int port){
            pid = pName;
            addr = new InetSocketAddress(host, port);
        }
    }
    ArrayList<GeneralEntry> table = new ArrayList<GeneralEntry>();
    public synchronized InetSocketAddress search(String sPid) {
        int pid = Integer.parseInt(sPid);
        System.out.println("Searching " + pid);
        for (GeneralEntry entry: table)
            if (pid == entry.pid){System.out.println("Found " + pid); return entry.addr;}
        return null;
    }
    // returns 0 if old value replaced, otherwise 1
    public synchronized int insert(int pid, String hostName, int portNumber) {
        System.out.println("Inserting " + pid);
        int retValue = 1;
        for (GeneralEntry entry: table)
            if (pid == entry.pid) {
                table.remove(entry);
                retValue = 0;
            }
        table.add(new GeneralEntry(pid, hostName, portNumber));
        notifyAll();
        return retValue;
    }
    public synchronized InetSocketAddress blockingFind(String pid) {
        System.out.println("blockingFind " + pid);
        InetSocketAddress addr = search(pid);
        while (addr == null) {
            Util.myWait(this);
            addr = search(pid);
        }
        return addr;
    }
    public synchronized void clear() {
        table.clear();
    }
}
