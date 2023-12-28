package spacesimulation.algebra;

/**
 * Represents a Vector in 3d space.
 */
public class Vec {
    /**
     * x value
     */
    public double x;
    /**
     * y value
     */
    public double y;
    /**
     * z value
     */
    public double z;

    /**
     * Constructor
     * @param deltaX x value
     * @param deltaY y value
     * @param deltaZ z value
     */
    public Vec(double deltaX, double deltaY, double deltaZ) {
        x = deltaX;
        y = deltaY;
        z = deltaZ;
    }

    /**
     * adds the given Vector to this Vector and returns this instance with updated values
     * 
     * @param v other vector
     * @return the result o the vectoraddition with this vector and v
     */
    public Vec add(Vec v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    /**
     * subtracts the given Vector from this Vector and returns this instance with updated values
     * 
     * @param v other vector
     * @return the result o the vectorsubtraction with this vector and v
     */
    public Vec sub(Vec v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    /**
     * Returns the length of this vector
     * 
     * @return length
     */
    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Divides this vector through its length. This will keep its direction but make its length equal to 1. Then returns this instance with updated values.
     * 
     * @return A vector with the same direction as this one had before, but length 1.
     */
    public Vec shortenToLengthOne() {
        double length = getLength();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    /**
     * Returns the standard dot product (fundamental matrix = unit matrix)with the given vector.
     */
    public double scalarProduct(Vec v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Returns true if the angle between this vector and the given one is smaller than 90°. Otherwise, returns false.
     */
    public boolean hasSharpAngleTo(Vec v) {
        Vec w = linearProjection(v);
        return w.x * v.x > 0 || w.y * v.y > 0 || w.z * v.z > 0;
    }

    /**
     * Returns the part of this vector that is parallel to the given vector and updates the values of this instance to the parallel one's values.
     * The returned vector is safe to use.
     */
    public Vec linearProjection(Vec ontoThisOne) {
        Vec w = new Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z);
        double factor = scalarProduct(w)/w.scalarProduct(w);
        w.scale(factor);
        x = w.x;
        y = w.y;
        z = w.z;
        return this;
    }

    /**
     * Scales this Vector by a given factor. Then returns this instance with updated values
     * 
     * @param scalar factor
     * @return the result of scalar multiplication: this * scalar
     */
    public Vec scale(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * Returns the result of the vector addition v + w. The returned vector is safe to use.
     * 
     * @param v first vector
     * @param w second vector
     * @return v + w
     */
    public static Vec add(Vec v, Vec w) {
        return new Vec(v.x + w.x, v.y + w.y, v.z + w.z);
    }

     /**
     * Returns the result of the vector subtraction v - w. The returned vector is safe to use.
     * 
     * @param first first vector
     * @param second second vector
     * @return v - w
     */
    public static Vec sub(Vec first, Vec second) {
        return new Vec(first.x - second.x, first.y - second.y, first.z - second.z);
    }

    /**
     * Returns a vector with the same direction as the given one, but length 1. The returned vector is safe to use.
     * 
     * @return a vector with the same direction as the given one, but length 1.
     */
    public static Vec shortenToLengthOne(Vec v) {
        double length = v.getLength();
        return new Vec(v.x / length, v.y / length, v.z / length);
    }
    
    /**
     * Returns the result of the scalar multiplication v * scalar without changing v. The returned vector is safe to use.
     * @param v Vector
     * @param scalar factor
     * @return the result of the scalar multiplication v * scalar
     */
    public static Vec scale(Vec v, double scalar) {
        return new Vec(v.x * scalar, v.y * scalar, v.z * scalar);
    }

   /**
     * Returns the part of this vector that is parallel to the given vector.
     * The returned vector is safe to use.
     *
     * @param willBeProjected the returned vector builds this one
     * @param ontoThisOne the returned vector is parallel to this one
     * @return the part of this vector that is parallel to the given vector
     */
    public static Vec linearProjection(Vec willBeProjected, Vec ontoThisOne) {
        if (ontoThisOne.getLength() == 0) return new Vec(0, 0, 0);
        Vec w = new Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z);
        double factor = willBeProjected.scalarProduct(w)/w.scalarProduct(w);
        w.scale(factor);
        return w;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vec) {
            Vec v = (Vec) other;
            return x == v.x && y == v.y && z == v.z;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z +")";
    }
}