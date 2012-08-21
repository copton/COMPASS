package pdf;

import coordinates.area.Area;
import coordinates.area.AreaException;
import coordinates.Cartesian;

/**
 * a conrete implementation of a PDF, the uniform distritbution.
 */
class UniformDistribution extends PDF {
    public UniformDistribution(Area area)
    {
        super(area);
        probability = new Probability(1.0 / area.getVolume());
    }
    
    public Probability getProbability(Cartesian coordinates) throws AreaException
    {
        checkArea(coordinates);
        
        return probability; 
    }

    private Probability probability;
}
