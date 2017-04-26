package byzantine;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Created by Connor Lewis on 4/23/2017.
 */
public class General extends Thread{

    static Connector connector;
    static int pid; //process id
    static int numberOfProcesses; // number of processes
    static int myValue;
    static int queenValue;
    static double myWeight;
    static double initWeight;
    public double s0, s1;
    static Boolean finished = false;

    Scanner din;
    PrintStream pout;
    Socket server;

    General(){}

    General(int pid, Double weight, int proposedValue, int numberOfProcesses){
        myValue = proposedValue;
        myWeight = weight;
        this.pid = pid;
        this.numberOfProcesses = numberOfProcesses;
        s0 = 0.0; s1 = 0.0;
        for(int process = 0; process < numberOfProcesses; process++){
            if(process != pid) {

                    GeneralListener listener = new GeneralListener(process, this);
                    listener.start();

            }
        }
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
        System.out.println("Server returned " + result);
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

    public void receiveMsg(int fromId){
        try{
            Scanner sc = connector.dataIn.get(fromId);
            String s;
            int count = 0;

            if(sc.hasNextLine()) {
                s = sc.nextLine();
                if(!s.equals("")) {
                    String[] message = s.split(" ");
                    adjustWeights(Integer.parseInt(message[3]), Double.parseDouble(message[4]), message[2]);
                }
            }

        }catch(Exception e){
            System.err.println(e);
            finished = true;
            connector.closeSockets();
        }
    }

    public void sendMsg(int destId, int srcId, String tag, int value, double weight){
        try{
            String msg = destId + " " + pid + " " + tag + " " + value + " " + weight;
            System.out.println("Sending message: " + msg);
            PrintWriter writer = connector.dataOut.get(destId);
            writer.println(msg);
            writer.flush();
        }catch (Exception e){
            System.err.println(e);
            finished = true;
            connector.closeSockets();
        }
    }

    public void adjustWeights(int valueSent, double weightSent, String tag){
        //System.out.println("value " + valueSent + " weight " + weightSent + " tag " + tag);
        if(tag.equals("phase1")) {
            if(valueSent == 1){
                //System.out.println("Value 1 received");
                s1 += weightSent;
            }
            else if(valueSent == 0){
                s0 += weightSent;
            }
        }
        else if(tag.equals("queenValue")) {
            //System.out.println("Queenvalue set as " + valueSent);
            queenValue = valueSent;
        }
    }

    public static void main(String[] args) throws Exception {
        pid = Integer.parseInt(args[0]);
        myValue = Integer.parseInt(args[2]);
        int intWeight = Integer.parseInt(args[1]);
        initWeight = intWeight;
        initWeight /= 100;
        numberOfProcesses = Integer.parseInt(args[3]);
        int portNum = Integer.parseInt(args[4]);
        connector = new Connector();
        connector.Connect(pid, numberOfProcesses);
        General general = new General(pid, myWeight, myValue, numberOfProcesses);
        System.out.println("General " + pid + " started");
        Util.mySleep(Symbols.roundTime);
        WeightedQueen queen = new WeightedQueen(general);
        int consensusValue = queen.decide();
        finished = true;
        System.out.println("Consensus value: " + consensusValue);
    }
}
