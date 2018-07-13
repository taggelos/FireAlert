package ergasia;


import java.util.Random;

public class NormalRandom extends Random {
    private final double derivation;
    private final double mean;

    public NormalRandom(double derivation, double mean) {
        super();
        this.derivation = derivation;
        this.mean = mean;
    }

    public NormalRandom() {
        this(1.0, 0.0);
    }

    public double nextNormal() {  
        return 1.0;
        //return nextGaussian()*derivation+mean;
    }
    
    public static double[] gaussian (){
        Random rng = new Random();
        double r;
        double è;
        double x;
        double y;
        //r = Math.sqrt(-2 * log(rng.nextDouble()));
        r = Math.sqrt(-2 * com.sun.squawk.util.MathUtils.log(rng.nextDouble()));
        è = 2 * Math.PI  * rng.nextDouble();
        x = r * Math.cos(è);
        y = r * Math.sin(è);
        double [] res = {x*30+30,y*20+20};
        return res;
    }
}
