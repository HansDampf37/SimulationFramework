package spacesimulation.algebra;

import java.awt.*;
import spacesimulation.*;

public class CartesianCoordinateSystem {
    private boolean negative;
    private int axisLength;
    private double scaleSize;
    private Color color;

    public CartesianCoordinateSystem(boolean negative, int axisLength, double scaleSize, Color color) {
        this.negative = negative;
        this.axisLength = axisLength;
        this.scaleSize = scaleSize;
        this.color = color;
    }

    public void render(Graphics3d drawer, Graphics g) {
        g.setColor(color);
        drawer.drawLine(negative ? -axisLength : 0, 0, 0, axisLength, 0, 0, g);
        drawer.drawLine(0, negative ? -axisLength : 0, 0, 0, axisLength, 0, g);
        drawer.drawLine(0, 0, negative ? -axisLength : 0, 0, 0, axisLength, g);
        for (int i = 0; i <= axisLength / scaleSize * (negative ? 2 : 1); i++) {
            drawer.drawLine((negative ? -axisLength : 0) + i * scaleSize, -10, 0, (negative ? -axisLength : 0) + i * scaleSize, 10, 0, g);
            drawer.drawLine(-10, (negative ? -axisLength : 0) + i * scaleSize, 0, 10, (negative ? -axisLength : 0) + i * scaleSize, 0, g);
            drawer.drawLine(0, -10, (negative ? -axisLength : 0) + i * scaleSize, 0, 10, (negative ? -axisLength : 0) + i * scaleSize, g);
        }
        g.setColor(Color.red);
        drawer.drawLine(0, 0, 0, 0, 0, scaleSize, g);
        g.setColor(Color.green);
        drawer.drawLine(0, 0, 0, 0, scaleSize, 0, g);
        g.setColor(Color.blue);
        drawer.drawLine(0, 0, 0, scaleSize, 0, 0, g);

        Point3d a = new Point3d(0, 0, axisLength);
        Point3d b = new Point3d(40, 0, axisLength - 80);
        Point3d c = new Point3d(-40, 0, axisLength - 80);
        drawer.fillTriangle(a, b, c, Color.red, g);
        b = new Point3d(0, 40, axisLength - 80);
        c = new Point3d(0, -40, axisLength - 80);
        drawer.fillTriangle(a, b, c, Color.red, g);
        drawer.drawLine(0, 0, axisLength, 0, 0, axisLength, g);
        
        a = new Point3d(0, axisLength, 0);
        b = new Point3d(0, axisLength - 80, 40);
        c = new Point3d(0, axisLength - 80, -40);
        drawer.fillTriangle(a, b, c, Color.green, g);
        b = new Point3d(40, axisLength - 80, 0);
        c = new Point3d(-40, axisLength - 80, 0);
        drawer.fillTriangle(a, b, c, Color.green, g);
        drawer.drawLine(0, axisLength, 0, 0, axisLength, 0, g);

        a = new Point3d(axisLength, 0, 0);
        b = new Point3d(axisLength - 80, 40, 0);
        c = new Point3d(axisLength - 80, -40, 0);
        drawer.fillTriangle(a, b, c, Color.blue, g);
        b = new Point3d(axisLength - 80, 0, 40);
        c = new Point3d(axisLength - 80, 0, -40);
        drawer.fillTriangle(a, b, c, Color.blue, g);
        drawer.drawLine(axisLength, 0, 0, axisLength, 0, 0, g);
    }
}