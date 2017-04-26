package byzantine;

public class WeightedQueen{
final static int defaultValue = 0;
General general;
int f ; // maximum number of f a u l t s
int majorityValue ;
int myId;
boolean isStarted = false;


WeightedQueen(General general) {
    f = 2;
    this.general = general;
    myId = general.pid;
}

 public int decide ( ) {
     for (int k = 0; k <= f; k++) { // f+1 rounds
         System.out.println("Round " + k);
         if(isStarted) {
             general.s0 = 0;
             general.s1 = 0;
             general.queenValue = -1;
         }
         isStarted = true;
         broadcastMsg("phase1");
         Util.mySleep((Symbols.roundTime * 2) / (general.pid + 5));
         //Util.mySleep(Symbols.roundTime);
         //Util.mySleep(Symbols.roundTime);
         //Util.mySleep(Symbols.roundTime);
         //Util.mySleep(Symbols.roundTime);

         synchronized (this) {
             majorityValue = getMajority();
             if (k == myId) {
                 broadcastMsg("queenValue");
             }
             Util.mySleep(Symbols.roundTime);
             //Util.mySleep(Symbols.roundTime);
             //Util.mySleep(Symbols.roundTime);
             //Util.mySleep(Symbols.roundTime);
             //Util.mySleep(Symbols.roundTime);
             synchronized (this) {
                 if(general.queenValue == -1){
                     general.myValue = majorityValue;
                     //System.out.println("yo: " + general.myValue);
                 }
                 else if (general.myWeight > 0.75) {
                     general.myValue = majorityValue;
                     //System.out.println("myValue: " + general.myValue + " myWeight " + general.myWeight);
                 }else {
                     general.myValue = general.queenValue;
                     //System.out.println("sup: " + general.myValue);
                 }
             }
         }
     }
     return general.myValue;
 }

 void broadcastMsg(String tag) {
        for(int i = 0; i < general.numberOfProcesses; i++){
            if (i == general.pid) {
                if(tag.equals("queenValue")){
                    general.adjustWeights(general.myValue, general.initWeight, tag);
                }
                continue;
            }
            System.out.println("Sending initial weight of " + general.initWeight);
            general.sendMsg(i, general.pid, tag, general.myValue, general.initWeight);
        }
 }

    int getMajority () {
        if(general.myValue == 0){
            general.s0 += general.initWeight;
        }
        else{
            general.s1 += general.initWeight;
        }
        //System.out.println("S1 weight = " + general.s1 + " S0 weight = " + general.s0);
        if(general.s1 + general.s0 != 1) {
            double diff = 1 - (general.s1 + general.s0);
            general.s0 += diff;
            //System.out.println("new S0 weight = " + general.s0);
        }
        if (general.s1 > 0.5) {
            general.myWeight = general.s1;
            System.out.println("Setting value to 1 weight is " + general.myWeight);
            return 1;
        }
        else{
            general.myWeight = general.s0;
            System.out.println("Setting value to 0 weight is " + general.myWeight);
            return 0;
        }
    }
}