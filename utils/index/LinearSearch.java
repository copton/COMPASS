package utils.index;

import coordinates.area.Area;
import coordinates.Cartesian;
import coordinates.WGS84;

/**
 * concrete implementation of an index, the linear search.
 */
class LinearSearch extends Index {
    public LinearSearch(WGS84 origin)
    {
        this.origin = origin;
    }

    public void add(Area area, Object unit)
    {
        area.move(origin);
        add(unit);
        units.add(new Unit(area, unit));
    }

    public Object get(Cartesian coordinates) 
    {
        
        for (java.util.Iterator i = units.iterator(); i.hasNext(); ) {
            Unit unit = (Unit) i.next();
            if (unit.area.contains(coordinates)) {
                return unit.object;
            }
        }

        return null;
    }

    private class Unit {
        Unit(Area area, Object object)
        {
            this.area = area;
            this.object = object;
        }

        Area area;
        Object object;
    }

    private java.util.Vector units;
    private WGS84 origin;
}
