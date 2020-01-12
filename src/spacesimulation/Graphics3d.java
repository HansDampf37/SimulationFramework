package spacesimulation;

import java.awt.Color;
import java.awt.Graphics;
import spacesimulation.algebra.*;

public class Graphics3d {

    private double cameraAngleHorizontal = Math.PI / 4;
    private double cameraAngleVertical = Math.PI / 4;
    private double zoom;
    private double originX;
    private double originY;
    private int height;

    public Graphics3d(double cameraAngleHorizontal, double cameraAngleVertical, double zoom) {
        this.cameraAngleHorizontal = cameraAngleHorizontal;
        this.cameraAngleVertical = cameraAngleVertical;
        this.zoom = zoom;
    }

    public Graphics3d() {
        this(Math.PI / 4, Math.PI / 4, 0.3);
    }

    public void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, Graphics g) {
        g.drawLine(calcX(x1, y1, z1), calcY(x1, y1, z1), calcX(x2, y2, z2), calcY(x2, y2, z2));
    }

    public void drawLine(Point3d a, Point3d b, Graphics g) {
        drawLine(a.x, a.y, a.z, b.x, b.y, b.z, g);
    }

    public void drawDot(double x, double y, double z, int radius, Color color, Graphics g) {
        g.setColor(color);
        g.fillOval(calcX(x, y, z) - radius, calcY(x, y, z) - radius, 2 * radius, 2 * radius);
    }

    public void drawDot(Point3d a, int radius, Color color, Graphics g) {
        drawDot(a.x, a.y, a.z, radius, color, g);
    }

    private int calcX(double x, double y, double z) {
        return (int) ((x * Math.cos(cameraAngleHorizontal) + z * Math.cos(Math.PI / 2 + cameraAngleHorizontal)) * zoom
                + originX);
    }

    private int calcY(double x, double y, double z) {
        return height - (int) ((y * Math.cos(cameraAngleVertical)
                + z * Math.cos(Math.PI / 2 + cameraAngleVertical) * Math.cos(cameraAngleHorizontal)
                + x * Math.cos(-Math.PI / 2 + cameraAngleVertical) * Math.cos(cameraAngleHorizontal + Math.PI / 2))
                * zoom + originY);
    }

    // private int calcX(Point3d a) {
    //     return calcX(a.x, a.y, a.z);
    // }

    // private int calcY(Point3d a) {
    //     return calcY(a.x, a.y, a.z);
    // }
    
    public void fillTriangle(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, Color color, Graphics g) {
        g.setColor(Color.black);
        drawLine(x1, y1, z1, x2, y2, z2, g);
        drawLine(x2, y2, z2, x3, y3, z3, g);
        drawLine(x3, y3, z3, x1, y1, z1, g);
        g.setColor(color);
        int xValues[] = {calcX(x1, y1, z1), calcX(x2, y2, z2), calcX(x3, y3, z3)};
        int yValues[] = {calcY(x1, y1, z1), calcY(x2, y2, z2), calcY(x3, y3, z3)};
        g.fillPolygon(xValues, yValues, 3);
    }

    public void fillTriangle(Point3d a, Point3d b, Point3d c, Color color, Graphics g) {
        fillTriangle(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z, color, g);
    }

    public void fillParallelogram(Point3d a, Vec delta1, Vec delta2, Color color, Graphics g) {
        fillParallelogram(a.x, a.y, a.z, a.x + delta1.x, a.y + delta1.y, a.z + delta1.z, a.x + delta1.x + delta2.x, a.y + delta1.y + delta2.y, a.z + delta1.z + delta2.z, a.x + delta2.x, a.y + delta2.y, a.z + delta2.z, color, g);
    }

    public void fillParallelogram(Point3d a, Point3d b, Point3d c, Point3d d, Color color, Graphics g) {
        fillParallelogram(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z, d.x, d.y, d.z, color, g);
    }

    public void fillParallelogram(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, Color color, Graphics g) {
        g.setColor(Color.black);
        drawLine(x1, y1, z1, x2, y2, z2, g);
        drawLine(x2, y2, z2, x3, y3, z3, g);
        drawLine(x3, y3, z3, x4, y4, z4, g);
        drawLine(x4, y4, z4, x1, y1, z1, g);
        g.setColor(color);
        int xValues[] = {calcX(x1, y1, z1), calcX(x2, y2, z2), calcX(x3, y3, z3), calcX(x4, y4, z4)};
        int yValues[] = {calcY(x1, y1, z1), calcY(x2, y2, z2), calcY(x3, y3, z3), calcY(x4, y4, z4)};
        g.fillPolygon(xValues, yValues, 4);
    }

    public void moveHorizontalCamera(int dir) {
        if (dir > 0)
            cameraAngleHorizontal += 0.003;
        else if (dir < 0)
            cameraAngleHorizontal -= 0.003;
    }

    public void moveVerticalCamera(int dir) {
        if (dir > 0)
            cameraAngleVertical += 0.003;
        else if (dir < 0)
            cameraAngleVertical -= 0.003;
        if (cameraAngleVertical > Math.PI / 2) cameraAngleVertical = Math.PI / 2;
        if (cameraAngleVertical < -Math.PI / 2) cameraAngleVertical = -Math.PI / 2;
    }

    public void zoom(int dir) {
        try {
            zoom *= (dir > 0) ? 1.005 : 0.995;
        } catch (ArithmeticException e) {
            //Too small or large
            System.out.println("Zoom limit reached");
        }
    }

    public void setWindowHeightAndWidth(int width, int height) {
        // this.width = width;
        this.height = height;
        originX = width / 2;
        originY = height / 2;
    }

    public void setCameraAngleHorizontal(double angle) {
        cameraAngleHorizontal = angle;
    }
    public void setCameraAngleVertical(double angle) {
        cameraAngleVertical = angle;
    }
    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public String cameraSettingsToString() {
        return "Horizontal: " + (int)(cameraAngleHorizontal / Math.PI * 180) + " deg (" + round(cameraAngleHorizontal / Math.PI) + " * PI) \n" +
        "Vertical: " + (int)(cameraAngleVertical / Math.PI * 180) + "deg (" + round(cameraAngleVertical / Math.PI) + " * PI) \n " + 
        "Zoom: " + (int)(zoom * 100) + "%";
    }

    private double round(double in) {
        return (double)((int)(in * 100)) / 100.0;
    }
}