package simulations;

import java.awt.*;
import spacesimulation.*;
import spacesimulation.algebra.*;

public class PlatonSpace extends Simulation {
    private Point3d[] points;
    private Vec[] forces;
    private final int RADIUS = 1000;
    CartesianCoordinateSystem coordSys = new CartesianCoordinateSystem(true, RADIUS * 2, RADIUS / 10, new Color(110, 106, 160));
    Color colorLines = new Color(200, 200, 200);
    Color colorPoints = new Color(163, 153, 239);
    Color colorInput = new Color(200, 200, 200);

    public PlatonSpace(int amountOfPoint3ds, Simulator simulator) {
        super(simulator);
        points = new Point3d[amountOfPoint3ds];
        forces = new Vec[amountOfPoint3ds];
        for (int i = 0; i < points.length; i++) points[i] = new Point3d(2 * RADIUS * Math.random() - RADIUS, 2 * RADIUS * Math.random() - RADIUS, 2 * RADIUS * Math.random() - RADIUS);
        for (int i = 0; i < forces.length; i++) forces[i] = new Vec(0, 0, 0);
    }

    public void tick(double dtInSec) {
        calcResultingForceOnPoint3d();
        movePoints();
        keepPointsInOrb();
        drawer.setWindowHeightAndWidth(simulator.getWidth(), simulator.getHeight());
    }

    public void render(Graphics g) {
        coordSys.render(drawer, g);
        drawPoints(g);
        if (simulator.getKeymanager().f) drawForces(g);
    }

    private void drawForces(Graphics g) {
        for (int i = 0; i < points.length; i++) {
            Vec force = forces[i];
            g.setColor(Color.ORANGE);
            double shorten = 1 / force.getLength();
            drawer.drawLine(points[i].x, points[i].y, points[i].z, points[i].x + force.x * shorten, points[i].y + force.y * shorten, points[i].z + force.z * shorten, g);
        }
    }

    private void keepPointsInOrb() {
        for (int i = 0; i < points.length; i++) {
            if (points[i].getPositionVector().getLength() > RADIUS) {
                Vec positionVector = points[i].getPositionVector();
                points[i].set(positionVector.scale(RADIUS / positionVector.getLength()));
            }
        }
    }

    private void movePoints() {
        for (int i = 0; i < points.length; i++) {
            points[i].add(forces[i]);
        }
    }

    private void calcResultingForceOnPoint3d() {
        for (int i = 0; i < points.length; i++) {
            Point3d first = points[i];
            for (Point3d other : points) {
                if (!first.equals(other)) {
                    double scalar = 10000000 / Math.pow(first.getDistanceTo(other), 2);
                    forces[i].add(other.getDirectionTo(first).scale(scalar));
                }
            }
        }
    }

    private void drawPoints(Graphics g) {
        for (Point3d point : points) {
            drawer.drawDot(point, 4, colorPoints, g);
        }
        double shortestDist = Integer.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            for (int j = i; j < points.length; j++) {
                if (j != i) {
                    double dist = points[i].getDistanceTo(points[j]);
                    if (dist < shortestDist) shortestDist = dist;
                }
            }
        }
        for (int i = 0; i < points.length; i++) {
            for (int j = i; j < points.length; j++) {
                if (j != i) {
                    if (points[i].getDistanceTo(points[j]) < 1.3 * shortestDist) {
                        g.setColor(colorLines);
                        drawer.drawLine(points[i], points[j], g);
                    }
                }
            }
        }
    }

    @Override
    public void reset() {
        points = new Point3d[points.length];
        forces = new Vec[forces.length];
        for (int i = 0; i < points.length; i++) points[i] = new Point3d(2 * RADIUS * Math.random() - RADIUS, 2 * RADIUS * Math.random() - RADIUS, 2 * RADIUS * Math.random() - RADIUS);
        for (int i = 0; i < forces.length; i++) forces[i] = new Vec(0, 0, 0);
    }
}