package coordinates.area;

import java.util.Vector;

import coordinates.WGS84;
import coordinates.Cartesian;

/**
 * A concrete implementation of an area, a cuboid.
 * A cuboid consists of an origin and two vectors. The lower, left, front corner and the upper, right, back one.
 * The x access goes from left to right and the coordinates increase when going right.
 * The y access goes from down to up and the coordinates increase when going up.
 * The z access goes from front to back and the coordinates increase when going back.
 */
class Cuboid extends Area {
    /**
     * The iterator class for the cuboid
     */
    public class Iterator implements java.util.Iterator {
        
        public Iterator(Cartesian lowerLeftFront, Cartesian upperRightBack)
        {
            this.lowerLeftFront = lowerLeftFront;
            this.upperRightBack = upperRightBack;

            x = lowerLeftFront.getX();
            y = lowerLeftFront.getY();
            z = lowerLeftFront.getZ();
            
            // To avoid creating a new Cartesian object for every call of next only this object will be filled with
            // corretct values and returned each time.
            point = new Cartesian(x, y , z);
            
            hasNext = true;
        }

        public Iterator()
        {
            hasNext = false;
        }       

        /**
         * @return true as long as there are further points.
         */
        public boolean hasNext()
        {
            return hasNext;
        }
        
        /**
         * @return the next point in Cartesian coordinates
         */
        public Object next()
        {
            point.setX(x);
            point.setY(y);
            point.setZ(z);

            x += 1;
            if (x > upperRightBack.getX()) {
                x = lowerLeftFront.getX();
                y += 1;
                if (y > upperRightBack.getY()) {
                    y = lowerLeftFront.getY();
                    z += 1;
                    if (z > upperRightBack.getZ()) {
                        hasNext = false;
                    }
                }
            }
            
            return point;
        }

        /**
         * Removing is not implemented
         */
        public void remove() {}

        private boolean hasNext;
        private Cartesian point;
        private Cartesian lowerLeftFront;
        private Cartesian upperRightBack;
        private int x, y, z;
    };
    

    /**
     * The constructor.
     * @param origin The origin of the cuboid
     * @param params vector of two Cartesian coordinates, namely the lower, left, front corner and the upper, right, back one
     */
    public Cuboid(WGS84 origin, Vector params) 
    {
        this(origin, (Cartesian) params.elementAt(0), (Cartesian) params.elementAt(1));
    }

    /**
     * The constructor.
     * @param origin The origin of the cuboid
     * @param lowerLeftFront the lower, left, front corner 
     * @param upperRightBack the upper, right, back corner
     */
    public Cuboid(WGS84 origin, Cartesian lowerLeftFront, Cartesian upperRightBack)
    {
        this.origin = origin;
        
        if (! (lowerLeftFront.compareTo(upperRightBack) < 0)) {
            throw new AssertionError();
        }
        
        this.lowerLeftFront = lowerLeftFront;
        this.upperRightBack = upperRightBack;
    }
    
    /**
     * default constructor.
     * creates an empty area
     * @param origin the origin of the area in WGS84 coordinates
     */
    public Cuboid(WGS84 origin)
    {
        this(origin, null, null);
    }
    
    /* ** See the documentation of the Area interface for descriptions of the following functions ** */

    public java.util.Iterator iterator()
    {
        return new Iterator(lowerLeftFront, upperRightBack);
    }
    
    public void move(WGS84 newOrigin)
    {
        Cartesian diff = origin.diff(newOrigin);
        lowerLeftFront.move(diff);
        upperRightBack.move(diff);
        origin = newOrigin;
    }
    
    public void expand(Area area) 
    {
        Cuboid that = (Cuboid) area.clone();
        that.move(origin);

        int x, y, z;

        x = Math.min(this.lowerLeftFront.getX(), that.lowerLeftFront.getX());
        y = Math.min(this.lowerLeftFront.getY(), that.lowerLeftFront.getY());
        z = Math.min(this.lowerLeftFront.getZ(), that.lowerLeftFront.getZ());
        Cartesian newLowerLeftFront = new Cartesian(x,y,z);
        lowerLeftFront = newLowerLeftFront;

        x = Math.max(this.upperRightBack.getX(), that.upperRightBack.getX());
        y = Math.max(this.upperRightBack.getY(), that.upperRightBack.getY());
        z = Math.max(this.upperRightBack.getZ(), that.upperRightBack.getZ());
        Cartesian newUpperRightBack = new Cartesian(x,y,z);
        upperRightBack = newUpperRightBack;
    }
    
    public Area intersect(Area with)
    {
        Cuboid that = (Cuboid) with.clone();

        that.move(origin);
        
        if ((this.lowerLeftFront.compareTo(that.upperRightBack) > 0)
          ||(that.lowerLeftFront.compareTo(this.upperRightBack) > 0))   
        {
            // intersection is emtpy
            return new Cuboid(origin);    
        }
 
        
        int x, y, z;
        
        x = Math.max(this.lowerLeftFront.getX(), that.lowerLeftFront.getX());
        y = Math.max(this.lowerLeftFront.getY(), that.lowerLeftFront.getY());
        z = Math.max(this.lowerLeftFront.getZ(), that.lowerLeftFront.getZ());
        Cartesian newLowerLeftFront = new Cartesian(x,y,z);

        x = Math.min(this.upperRightBack.getX(), that.upperRightBack.getX());
        y = Math.min(this.upperRightBack.getY(), that.upperRightBack.getY());
        z = Math.min(this.upperRightBack.getZ(), that.upperRightBack.getZ());
        Cartesian newUpperRightBack = new Cartesian(x,y,z);

        return new Cuboid(origin, newLowerLeftFront, newUpperRightBack);
    }

    public long getVolume()
    {
        if (isEmpty()) {
            return 0;
        }

        int diffX = upperRightBack.getX() - lowerLeftFront.getX();
        int diffY = upperRightBack.getY() - lowerLeftFront.getY();
        int diffZ = upperRightBack.getZ() - lowerLeftFront.getZ();

        return diffX * diffY * diffZ;
    }

    public boolean isEmpty()
    {
        if (lowerLeftFront == null || upperRightBack == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSubsetOf(Area area)
    {
        Cuboid that = (Cuboid) area;

        if ((that.lowerLeftFront.compareTo(this.lowerLeftFront) < 0) 
          &&(that.upperRightBack.compareTo(this.upperRightBack) > 0))
        {
            return true;
        } else {
            return false;
        }   
        
    }
    
    public boolean isSupersetOf(Area area)
    {
        return area.isSubsetOf((Area) this);
    }

    public boolean contains(Cartesian point)
    {
        if ((lowerLeftFront.compareTo(point) < 0) 
          &&(upperRightBack.compareTo(point) > 0))
        {
            return true;
        } else {
            return false;
        }
    }

    public boolean contains(WGS84 point)
    {
        return contains(origin.diff(point));
    }

    private Cartesian lowerLeftFront;
    private Cartesian upperRightBack;
}
