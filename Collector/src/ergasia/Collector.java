package ergasia;

import java.util.Calendar;
import java.io.IOException;
import javax.microedition.io.Datagram;
import javax.microedition.io.Connector;
import javax.microedition.midlet.MIDlet;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.Utils;
import java.util.Vector;
import javax.microedition.midlet.MIDletStateChangeException;

public class Collector extends MIDlet {

    RadiogramConnection rCon1;
    Datagram dg1;
    RadiogramConnection rCon2;
    Datagram dg2;
    int N = 3;
    Vector[] myValues = new Vector[3];
    int port1 = 68;
    int port2 = 69;
    int myperiod = 10000;// in milliseconds

    public void Collector() {
        new Thread() {
            public void run() {
                try {
                    //Connection to receive from Sensors
                    rCon1 = (RadiogramConnection) Connector.open("radiogram://:" + port1);
                    dg1 = rCon1.newDatagram(rCon1.getMaximumLength());
                }
                catch (Exception e) {
                    System.err.println("Caught " + e.getMessage());
                }
                 for (; ;) {
                    try {                
                        for (int i = 0; i < N; i++) {
                            // Read sensor sample 
                            rCon1.receive(dg1);
                            int val = dg1.readInt();         // read the sensor value
                            myValues[i].addElement(new Integer(val)); 
                            synchronized  (myValues[i]){
                                myValues[i].notify();
                            }
                        }           
                    }
                    catch (Exception e) {
                        System.err.println("Exception " + e +  " while reading Sensors.");
                    }
                } 
            }            
        }.start();
    }
    
    synchronized public void Forwardor() {
        new Thread() {
            public void run() {
                try {
                    //Connection to send to Sink
                    rCon2 = (RadiogramConnection) Connector.open("radiogram://broadcast:" + port2);
                    dg2 = rCon2.newDatagram(250);
                    }
                catch (Exception e) {
                    System.err.println("Caught " + e + " in Connection");
                    notifyDestroyed();
                }   
                for(; ;){
                    try{
                        int array[] =new int[N] ;
                        for (int i=0; i<N;i++) {
                            synchronized (myValues[i]){
                                myValues[i].wait();
                            }                            
                            array[i]= ((Integer) myValues[i].firstElement()).intValue();
                            System.out.print(" ~ "+ array[i] + " ~ "); //Values about to send
                            myValues[i].removeElementAt(0);
                        }                        
                        System.out.println();
                        dg2.reset();
                        dg2.writeBoolean(majority(array));
                        rCon2.send(dg2);
                    }
                    catch (InterruptedException e) {
                        System.err.println("Caught Interruption " + e );
                    } catch (IOException e) {
                        System.err.println("Caught IOException " + e );
                    }                     
                    Utils.sleep(myperiod);     
                }
            }               
        }.start();
    }
    
    protected void startApp() throws MIDletStateChangeException {   
        
        for (int i=0; i<N; i++){
            myValues[i]=new Vector();
        }   
        new com.sun.spot.service.BootloaderListenerService().getInstance().start();
        Collector();
        Forwardor();   
    }
    
    boolean majority(int[] array){
        //majority voting mechanism
        return array[0] + array[1] + array[2] > 1;
    }
    
    protected void pauseApp() {

    }

    protected void destroyApp(boolean bln) throws MIDletStateChangeException {

    }
}