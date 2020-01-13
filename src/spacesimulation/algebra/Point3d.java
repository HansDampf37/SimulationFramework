package spacesimulation.algebra;

public class Point3d {
    public double x;
    public double y;
    public double z;

    public Point3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3d(Vec positionVector) {
        x = positionVector.x;
        y = positionVector.y;
        z = positionVector.z;
    }

    public void add(Vec delta) {
        x += delta.x;
        y += delta.y;
        z += delta.z;
    }

    public void set(Vec positionVector) {
        x = positionVector.x;
        y = positionVector.y;
        z = positionVector.z;
    }

    public Vec getConnectingVectorTo(Point3d other) {
        return new Vec(other.x - x, other.y - y, other.z - z);
    }

    public Vec getDirectionTo(Point3d other) {
        Vec result = getConnectingVectorTo(other);
        result.scale(1/result.getLength());
        return result;
    }

    public double getDistanceTo(Point3d other) {
        return getConnectingVectorTo(other).getLength();
    }

    public Vec getPositionVector() {
        return new Vec(x, y, z);
    }

    @Override
    public String toString() {
        return "[" + (int)x + ", " + (int)y + ", " + (int)z + "]";
    }
}