package byzantine;

/**
 * Created by Connor Lewis on 4/25/2017.
 */
public class GeneralListener extends Thread{
    int channel;
    General comm = null;
    public GeneralListener(int channel, General comm) {
        this.channel = channel;
        this.comm = comm;
    }
    public void run() {
        while (!comm.finished) {
            //System.out.println("Listening on " + channel);
            comm.receiveMsg(channel);
            //System.out.println("Executing message");
            //comm.executeMsg(m);

        }
    }
}
