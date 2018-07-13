package ergasia;

import com.sun.squawk.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.FeedforwardLayer;
public class MySensors {

    private static final double[] my_weights = {0.4, -0.6, -38.0, 0.8, 0.85, -0.5, -50.0, 0.85, -1.0, 0.05, -1.2, 0.2, -27.5, -0.6, -2.3, -27.2, -27.1};

    // Neural Network computes 1 in case of fire and 0 in normal case.
    private final BasicNetwork network;
    
    private Double[] doubleToDoubleArray(double [] weights){
        Double [] d_weights=new Double[17];
        for (int i=0; i < weights.length;i++){
            d_weights[i] = new Double(weights[i]);
        }
        return d_weights;
    }
    
    /*
    private Double[] readWeights() throws IOException{
        InputStream is = getClass().getResourceAsStream("input.txt");
        Double[] weights=new Double[17];
        try {       
            
                System.out.println("AXNE 1113");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        
                System.out.println("AXNE 1114");
            if (is != null) {                            
                String str;               
                for (int i=0; i<17;i++){                    
                System.out.println("AXNE 333");
                    str = reader.readLine(); 
                    if (str==null) System.out.println("17 Values needed, 17 Rows should exist!");
                    else {
                        System.out.println("AXNE 2");
                        weights[i] = Double.valueOf(str);
                        System.out.println(weights[i] +" - "+ i);
                    }
                }                
            }
        } finally {
            try { if(is!=null) is.close(); else System.out.println("AXNE 252");} catch (Throwable ignore) {}
        }
        return weights;
    }*/

    public MySensors(){
        network = new BasicNetwork();
        network.addLayer(new FeedforwardLayer(2));
        network.addLayer(new FeedforwardLayer(4));
        network.addLayer(new FeedforwardLayer(1)); 
        /*try {
            readWeights();
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
        network.loadWeightsThresholds(doubleToDoubleArray(my_weights));
    }

    public boolean fireAlert(double[] vals) {
        boolean hot = vals[0] >= 35;
        boolean dry = vals[1] <= 35;
        if (hot && dry)
            return true;
        else if (hot == dry && vals[1] <= 100)        // (!hot && !dry && humidity <= 100)
            return false;
        else {
            double[] data = {vals[0], vals[1]};
            return network.compute(new BasicNeuralData(data)).getData(0) >= 0.5;
        }
    }
}
