package simulations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import spacesimulation.*;
import spacesimulation.algebra.*;

public class Fractal extends Simulation {
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(false, 100, 10, Color.white);
    private Point3d[] corners;
    private List<Point3d> points;

    public Fractal(int dim, Simulator sim) {
        super(sim);
        corners = new Point3d[dim];
        reset();
    }

    @Override
    public void tick() {
        int speed = 5;
        for (int i = 0; i < speed; i++) {
            Point3d corner = corners[(int)(Math.random() * corners.length)];
            points.add(new Point3d(points.get(points.size() - 1).getConnectingVectorTo(corner).scale(0.5).add(points.get(points.size() - 1).getPositionVector())));
        }
        drawer.setCameraAngleHorizontal(0);
        drawer.setCameraAngleVertical(0);
    }

    @Override
    public void render(Graphics g) {
        // cart.render(drawer, g);
        for (Point3d point : corners) drawer.drawDot(point, 4, Color.green, g);
        for (Point3d point : points) drawer.drawDot(point, 1, Color.white, g);
    }

    @Override
    public void reset() {
        double scale = 1000;
        points = new ArrayList<Point3d>();
        // for (int i = 0; i < (int)((double)corners.length / 2.0 + 0.6); i++) corners[i] = new Point3d(Math.random() * scale * 2 - scale, Math.random() * scale * 2 - scale, 0);
        // for (int i = (int)((double)corners.length / 2.0 + 0.6); i < corners.length; i++) corners[i] = new Point3d(corners[i - corners.length / 2].getPositionVector().scale(-1));
        corners[0] = new Point3d(0,-100,0);
        corners[1] = new Point3d(-95,-31,0);
        corners[2] = new Point3d(-59,81,0);
        corners[3] = new Point3d(59,81,0);
        corners[4] = new Point3d(95,-31,0);
        points.add(new Point3d(Math.random() * scale * 2 - scale, Math.random() * scale * 2 - scale, 0));
        drawer.setZoom(4.7);
    }
}