package coordinates;

/**
 * Cartesian coordinates.
 * A cartesian coordinate is nothing but a vector with three dimensions.
 * There is no origin. It must be clear from the context where the vector is used in.
 * The absolut real life distance per grid is a config value.
 */
public class Cartesian {
    
    public Cartesian(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    public int getZ()
    {
        return z;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    /**
     * Add two vectors
     */
    public void move(Cartesian distance)
    {
        this.x += distance.getX();
        this.y += distance.getY();
        this.z += distance.getZ();
    }

    /**
     * Compare two Cartesian vectors.
     * In this comparison a vector is greater than an other if and only if alls three
     * components are greater. If neither vector is greater the vectors are considered 
     * as equal.
     * @param with the vector to compare with
     * @return 1 if this > with, -1 if this < with, 0 else
     */
    public int compareTo(Cartesian with)
    {
        if (x <= with.x && y <= with.y && z <= with.z) {
            return -1;
        }

        if (x >= with.x && y >= with.y && z >= with.z) {
            return 1;
        }

        return 0;
    }

    private int x;
    private int y;
    private int z;
}
