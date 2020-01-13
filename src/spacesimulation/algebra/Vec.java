package spacesimulation.algebra;

public class Vec {
    public double x;
    public double y;
    public double z;

    public Vec(double deltaX, double deltaY, double deltaZ) {
        x = deltaX;
        y = deltaY;
        z = deltaZ;
    }

    public Vec add(Vec v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vec sub(Vec v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public double getLength() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec shortenToLenghtOne() {
        double length = getLength();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    public double scalarProduct(Vec v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vec linearProjection(Vec ontoThisOne) {
        Vec w = new Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z);
        double factor = scalarProduct(w)/w.scalarProduct(w);
        w.scale(factor);
        x = w.x;
        y = w.y;
        z = w.z;
        return this;
    }

    public Vec scale(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    public static Vec add(Vec v, Vec w) {
        return new Vec(v.x + w.x, v.y + w.y, v.z + w.z);
    }

    public static Vec sub(Vec first, Vec second) {
        return new Vec(first.x - second.x, first.y - second.y, first.z - second.z);
    }

    public static Vec shortenToLenghtOne(Vec v) {
        double length = v.getLength();
        return new Vec(v.x / length, v.y / length, v.z / length);
    }
    
    public static Vec scale(Vec v, double scalar) {
        return new Vec(v.x * scalar, v.y * scalar, v.z * scalar);
    }

    public static Vec linearProjection(Vec willBeProjected, Vec ontoThisOne) {
        if (ontoThisOne.getLength() == 0) return new Vec(0, 0, 0);
        Vec w = new Vec(ontoThisOne.x, ontoThisOne.y, ontoThisOne.z);
        double factor = willBeProjected.scalarProduct(w)/w.scalarProduct(w);
        w.scale(factor);
        return w;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z +")";
    }
}