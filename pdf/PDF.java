package pdf;

import coordinates.area.*;
import coordinates.Cartesian;
import java.util.Iterator;
import logger.Logger;

/**
 * Abstract PDF class.
 */
public abstract class PDF {
    
    /**** public ****/
    
    public PDF(Area area)
    {
        this.area = area;
    }
    
    public Area getArea()
    {
        return area;
    }

    public abstract Probability getProbability(Cartesian coordinates) throws AreaException;


    /**** protected ****/

    protected void checkArea(Cartesian coordinates) throws AreaException
    {
        if (! area.contains(coordinates)) {
            throw new AreaException();
        }
    }

    protected Area area;

    /**
     * print values along the x, y or z axes apprioriate for visualisation by gnuplot.
     * printing is done directly and not by the logger.
     * @param what is either "x", "y" or "z"
     */
    public void print(String what)
    {
        int sel;

        if (what.equals("x")) {
            sel = 0;
        } else if (what.equals("y")) {
            sel = 1;
        } else {
            sel = 2;
        }

        try {
            Iterator i = area.iterator();
            while(i.hasNext()) {
                Cartesian coord = (Cartesian) i.next();
                if (sel == 0) {
                    if (coord.getY() == 0 && coord.getZ() == 0) {
                        System.out.println(coord.getX() + " " + getProbability(coord).getValue());
                    }
                }
                else if (sel == 1) {
                    if (coord.getX() == 0 && coord.getZ() == 0) {
                        System.out.println(coord.getY() + " " + getProbability(coord).getValue());
                    }
                }
                else if (sel == 2) {
                    if (coord.getX() == 0 && coord.getY() == 0) {
                        System.out.println(coord.getZ() + " " + getProbability(coord).getValue());
                    }
                }
            }
        } catch (AreaException e) {
            Logger.fatal("PDF", "print", "This should never happen");
        }
    }
}

