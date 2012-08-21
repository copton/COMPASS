package coordinates.area;

import coordinates.WGS84;
import coordinates.Cartesian;

/**
 * The abstract class for an area in the three dimensional room.
 * Provides a factory method to create new area objects.
 */
abstract public class Area implements Cloneable {

    /**
     * factory for new area objects.
     * @param origin the origin of the area
     * @param params a Vector of params. They are interpreted by the constructor of the concrete area class.
     * Usually this are the corners given in Cartesian coordinates.
     */
    public static Area newArea(WGS84 origin, java.util.Vector params)
    {
        return new Cuboid(origin, params);
    }

    /**
     * @return the origin of the area in WGS84 coordinates
     */
    public WGS84 getOrigin()
    {
        return origin;
    }

    /**
     * The clone function.
     * Java sucks. I will never understand why ones needs this if one only wants to have a bitwise copy. 
     * Especially the try clause is pain in the a..
     */
    public Object clone()
    {
        Object obj = null;
        try {
            obj = super.clone();
        } catch (java.lang.CloneNotSupportedException e) {
            ;
        }

        return obj;
    }
    
    /**
     * return an iterator over the area.
     * The iterator returns cartesian coordinates.
     * For performance reasons always the same object is returned whith new values. So do not memory any
     * references on it
     */
    public abstract java.util.Iterator iterator();

    /**
     * move the origin of the area.
     * The absolut covered area stays the same, so basically the corners are recalculated
     */
    public abstract void move(WGS84 newOrigin);

    /**
     * expand the area to additionally cover the given room.
     * The area should stay as small as possible but there may exist a room afterwards which didn't belong to
     * either area before.
     */
    public abstract void expand(Area area);

    /**
     * intersect two areas.
     * The returned area has the same origin as this object.
     * @return the intersection area
     */
    public abstract Area intersect(Area with);

    /**
     * get the volume of the area.
     * @return the volume of the area in grid units ^ 3
     */
    public abstract long getVolume();

    /**
     * check if the area does not cover any room.
     * @return true if the area is empty, false otherwise.
     */
    public abstract boolean isEmpty();

    /**
     * check if this area is a subset of the given one.
     * @return true if it is, false otherwise
     */
    public abstract boolean isSubsetOf(Area area);

    /**
     * check if this area is a superset of the given one.
     * @return true if it is, false otherwise
     */
    public abstract boolean isSupersetOf(Area area);

    /**
     * check if this area contains the given point.
     * @return true if it does, false otherwise
     */
    public abstract boolean contains(WGS84 point);

    /**
     * check if the given vector lies within this area.
     * @return true if it does, flase otherwise
     */
    public abstract boolean contains(Cartesian point);

    protected WGS84 origin;
}
