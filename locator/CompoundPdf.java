package locator; 

import java.util.Vector;
import coordinates.area.AreaException;
import coordinates.area.Area;
import java.util.Iterator;
import coordinates.Cartesian;
import config.Config;
import logger.Logger;

import pdf.PDF;
import pdf.Probability;

/**
 * A compound PDF over some PFDs.
 * The formula is: $ \overline{p(x)} = frac{\prod_{i=1}^{n}p_i(x)}{\sum_x\sum_y\sum_z \prod^{n}_{i=1} p_i(x)}$
 * The implementation is not efficient. 
 */
public class CompoundPdf extends PDF {
    /**
     * The constructor.
     * @param pdfs vector of PDF objects
     */
    public CompoundPdf(Vector pdfs)
    {
        super(calcArea(pdfs));
        this.pdfs = pdfs;
        calcNormAndMax();
    }

    /**
     * @return the compound probability for the given point
     */
    public Probability getProbability(Cartesian coordinates) throws AreaException
    {
        checkArea(coordinates);
        Probability p = _getProbability(coordinates);     
        p.scale(norm);
        return p;
    }

    private Probability _getProbability(Cartesian coordinates) throws AreaException
    {
        Iterator i = pdfs.iterator();
        Probability p = new Probability(1.0);
        Probability epsilon = new Probability(Config.pdf.CompoundPdf.getEpsilon());    

        while(i.hasNext()) {
            PDF pdf = (PDF) i.next();
            try {
                p.scale(pdf.getProbability(coordinates));
            } catch (AreaException e) {
                p.scale(epsilon);
            }
        }

        return p;
    }

    /**
     * @return point of maximum probability in Cartesian coordinates
     */
    public Cartesian getPointOfMaxProbability()
    {
        return pointOfMaxProbability;
    }

    /**
     * determines the area of the compound PFDs, which includes the areas of all given PDFs.
     */
    private static Area calcArea(Vector pdfs)
    {
        if (pdfs.size() == 0) {
            return null;
        }

        Area area = ((PDF)pdfs.elementAt(0)).getArea();
        for (int i=1; i < pdfs.size(); i++) {
            area.expand(((PDF)pdfs.elementAt(i)).getArea());
        }

        return area;
    }

    /**
     * calculate the norm factor and the point of the maxium probability
     */
    private void calcNormAndMax()
    {
        Probability prob = new Probability(0.0);
        pointOfMaxProbability = new Cartesian(0,0,0);
        max = new Probability(0.0);
        
        Iterator i = area.iterator();
        while(i.hasNext()) {
            Cartesian coords = (Cartesian) i.next();
            Probability p;
            try {
                p = _getProbability(coords);
            } catch (AreaException e) {
                Logger.fatal("CompoundPdf", "calcNormAndMax", "this should never happen error");
                return;
            }
            
            prob.add(p); 
            if (p.compareTo(max) > 0) {
                max = p;
                pointOfMaxProbability.setX(coords.getX());
                pointOfMaxProbability.setY(coords.getY());
                pointOfMaxProbability.setZ(coords.getZ());
            }
        }
        
        norm = prob.reciprocal();
    }
    
    private Probability max;
    private Probability norm;
    private Vector pdfs;
    private Cartesian pointOfMaxProbability;
}

