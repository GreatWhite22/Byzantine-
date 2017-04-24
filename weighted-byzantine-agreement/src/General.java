import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Created by Connor Lewis on 4/23/2017.
 */
public class General extends Thread{

    static int pid; //process id
    static int numberOfProcesses; // number of processes
    static int proposedValue;
    // public float rho; // rho must be less than 1/4
    public List<Double> weights;
    public double s0, s1;

    Scanner din;
    PrintStream pout;
    Socket server;

    General(int pid, Double weight, int proposedValue, int numberOfProcesses){
        this.pid = pid;
        weights.add(pid, weight);
        this.proposedValue = proposedValue;
        this.numberOfProcesses = numberOfProcesses;
        s0 = 0.0; s1 = 0.0;
    }
    public void getSocket() throws IOException{
        server = new Socket(Symbols.nameServer, Symbols.ServerPort);
        din = new Scanner(server.getInputStream());
        pout = new PrintStream(server.getOutputStream());
    }

    public int insertName(int pid, String hostName, int portnum)
            throws IOException {
        getSocket();
        pout.println("insert " + pid + " " + hostName + " " + portnum);
        pout.flush();
        int retValue = din.nextInt();
        server.close();
        return retValue;
    }
    public InetSocketAddress searchName(int pid, boolean isBlocking)
            throws IOException {
        getSocket();
        if (isBlocking) pout.println("blockingFind " + pid);
        else pout.println("search " + pid);
        pout.flush();
        String result = din.nextLine();
        System.out.println("NameServer returned" + result);
        Scanner sc = new Scanner(result);
        server.close();
        int portnum = sc.nextInt();
        String hostName = sc.next();
        if (portnum == 0) return null;
        else return new InetSocketAddress(hostName, portnum);
    }
    public void clear() throws IOException {
        getSocket();
        pout.println("clear " );
        pout.flush();
        server.close();
    }

    public static void main(String[] args) throws IOException {
        pid = Integer.parseInt(args[0]);
        proposedValue = Integer.parseInt(args[1]);
        double weight = Double.parseDouble(args[2]);
        numberOfProcesses = Integer.parseInt(args[3]);
        int portNum = Integer.parseInt(args[4]);
        General general = new General(pid, weight, proposedValue, numberOfProcesses);
        general.insertName(pid, Symbols.nameServer, portNum);
        Connector connector = new Connector();
        connector.Connect(pid, );
        System.out.println("General + " + pid + " started");
    }
}
