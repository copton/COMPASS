package coordinates;

import java.lang.Math;
import config.Config;

/**
 * WGS84 coordinates.
 * This is the standard used for GPS.
 * This coordinates are used to express absolut origins to which cartesian coordinates can refer to.
 */
public class WGS84 {

    public WGS84(double longitude, double latitude, double altitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    /**
     * Calculate the difference between to absolut points.
     * @param that the second point.
     * @return the difference vector between the two points.
     */
    public Cartesian diff(WGS84 that)
    {
        Cartesian point1 = this.convertToCartesian();
        Cartesian point2 = that.convertToCartesian();

        int res = Config.coordinates.Cartesian.getResolution();

        int x = (point1.getX() - point2.getX()) * 100 / res;
        int y = (point1.getY() - point2.getY()) * 100 / res;
        int z = (point1.getZ() - point2.getZ()) * 100 / res;

        return new Cartesian(x,y,z);
    }

    /**
     * This function is needed by the diff function.
     * This is voodoo. The formula is taken from the paper:
     * "Software Representation for Heterogeneous Location Data Sources Within A Probabilistic Framework" 
     * by Michael Angermann, Jens Kammann, Patrick Robertson, Alexander Staingaﬂ, Thomas Strang 
     * of the Institute for Communications and Navigation, German Aerospace Center (DLR).
     * @return Cartesian coordinates in meter with the center of the earth as origin
     */
    private Cartesian convertToCartesian()
    {
        // this is magic ;-)
        
        final long a = 6378137;
        final double f = 1.0 / 198.257223536;
        double sinlat = Math.sin(latitude);
        double coslat = Math.cos(latitude);
        double coslon = Math.cos(longitude);
        double N = a / Math.sqrt(1 - f * (2 - f) * sinlat * sinlat);

        double x = (N + altitude) * coslat * coslon;
        double y = (N + altitude) * coslat * coslon;
        double z = ((1 - Math.exp(2)) * N + altitude) * sinlat;

        return new Cartesian((int) x, (int) y, (int) z);
    }

    private double longitude, latitude, altitude;
}

