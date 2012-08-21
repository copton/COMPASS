package pdf;

import coordinates.area.Area;
import coordinates.area.AreaException;
import coordinates.Cartesian;

/**
 * A concrete implementation of a PDF, the gauss distribution
 */
public class GaussDistribution extends PDF {
    public GaussDistribution(Area area, double variance)
    {
        super(area);
        this.variance = variance;
    }

    public Probability getProbability(Cartesian coordinates) throws AreaException 
    {
        checkArea(coordinates);

        double norm = java.lang.Math.sqrt(2 * java.lang.Math.PI * variance);

        double x = coordinates.getX() * coordinates.getX();
        double y = coordinates.getY() * coordinates.getY();
        double z = coordinates.getZ() * coordinates.getZ();

        double probability = java.lang.Math.exp(-1 * (x+y+z) / (2.0 * variance)) / norm;

        return new Probability(probability);
    }
    
    private double variance;
}
