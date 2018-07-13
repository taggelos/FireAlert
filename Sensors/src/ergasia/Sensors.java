package ergasia;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.Utils;
import java.util.Calendar;
import java.util.Date;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class Sensors extends MIDlet {

    //private static final double t_derivation = 10;
    //private static final double t_mean = 50;
    //private static final double h_derivation = 20;
    //private static final double h_mean = 20;    
    RadiogramConnection rCon;
    Datagram dg;

    protected void startApp() throws MIDletStateChangeException {
        MySensors sensor = new MySensors();
        //NormalRandom temperature = new NormalRandom(t_derivation, t_mean);
        //NormalRandom humidity = new NormalRandom(h_derivation, h_mean);            
        Calendar cal = Calendar.getInstance();
        int myperiod = 10000;// in milliseconds
        int port = 68;
        int datagram_size= 250;
        long now;
        boolean reading;
        String ts;
        
        try {
            //Connection to send to Collector
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + port);
            dg = rCon.newDatagram(datagram_size);
        }
        catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            notifyDestroyed();
        }
        
        for (; ; ) {
            try {
                // Get the current time and sensor reading
                now = System.currentTimeMillis();
                double [] values = NormalRandom.gaussian(); 
                //values[0] is temperature
                //values[1] is humidity
                reading = sensor.fireAlert(values);
                cal.setTime(new Date(now));
                ts = cal.get(Calendar.YEAR) + "-" +
                        (1 + cal.get(Calendar.MONTH)) + "-" +
                        cal.get(Calendar.DAY_OF_MONTH) + " " +
                        cal.get(Calendar.HOUR_OF_DAY) + ":" +
                        cal.get(Calendar.MINUTE) + ":" +
                        cal.get(Calendar.SECOND);
                
                System.out.println("Temperature reading at " + ts + " is " + values[0] + " and Humidity is "+ values[1]);
                
                // Send the reading from sensor.
                dg.reset();
                //dg.writeLong(now);
                int send_reading=0;
                if(reading)send_reading=1;
                dg.writeInt(send_reading);
                rCon.send(dg);
                
                Utils.sleep(myperiod);
            }
            catch (Exception e) {
                System.err.println("Caught " + e +
                        " while collecting/sending sensor sample.");
            }
        }
    }

    protected void pauseApp() {
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void destroyApp(boolean bln) throws MIDletStateChangeException {
        //throw new java.lang.UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}