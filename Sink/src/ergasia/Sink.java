package ergasia;

import java.util.Date;
import java.util.Calendar;
import com.sun.spot.util.Utils;
import javax.microedition.io.Datagram;
import javax.microedition.io.Connector;
import javax.microedition.midlet.MIDlet;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import javax.microedition.midlet.MIDletStateChangeException;

public class Sink extends MIDlet {
    
    RadiogramConnection rCon;
    Datagram dg;
    
    protected void startApp() throws MIDletStateChangeException {

        int myperiod = 10000;     // in milliseconds
        int port = 69;        
        try {
            //Connection to receive from COllector
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + port);
            dg = rCon.newDatagram(rCon.getMaximumLength());
        }
        catch (Exception e) {
            System.err.println("Caught " + e.getMessage());
        }
        
        for(; ;) {
            try {                
                // Read sensor sample received over the radio
                if (rCon.packetsAvailable()) {
                    rCon.receive(dg);
                    boolean val = dg.readBoolean();
                    if(val){
                        System.out.println("FIRE ALERT : " + val);}
                    else {
                        System.out.println("Nothing to worry about.");
                    }
                }                   
                Utils.sleep(myperiod);
            }
            catch (Exception e) {
                System.err.println("Caught " + e +
                        " while Collecting samples.");
            }
        }
    }

    protected void pauseApp() {
        
    }
    
    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        
    }
}