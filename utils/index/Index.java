package utils.index;

import java.util.Vector;
import coordinates.area.Area;

import coordinates.*;

/**
 * An abstract class for indexing objects in an area.
 * A factory is provided to easily exchange the implementation.
 */
abstract public class Index {
    public static Index newIndex(WGS84 origin)
    {
        return new LinearSearch(origin);
    }

    public class Iterator implements java.util.Iterator {
        public Iterator(Vector units)
        {
            iterator = units.iterator();
        }
        
        public boolean hasNext()
        {
            return iterator.hasNext();
        }

        public Object next()
        {
            return iterator.next();
        }

        public void remove()
        {
            iterator.remove();
        }

        private java.util.Iterator iterator;
    }
    
    public Iterator iterator()
    {
        return new Iterator(units);
    }   

   
    /**
     * add an object to the index.
     * @param area the area this object covers. This object is modified, so provide a copy if you need it untouched.
     * @param unit the object itself
     */
    public abstract void add(Area area, Object unit);

    /**
     * get the first object of the index whose area covers the given point.
     */
    public abstract Object get(Cartesian coordinates);
    
    protected Index()
    {

    }

    protected void add(Object unit)
    {
        units.add(unit);
    }

    private Vector units;
}
