package simulations;

import java.awt.*;

import spacesimulation.Simulation;
import spacesimulation.Simulator;
import spacesimulation.algebra.CartesianCoordinateSystem;
import spacesimulation.algebra.Point3d;
import spacesimulation.algebra.Vec;

public class Net extends Simulation {
    private static final int size = 30;
    private static final int distBetweenPoints = 300;
    private double airResist = 0.999;
    private Vec gravity = new Vec(0, -100, 0);
    private Point3d[][] points;
    private Vec[][] forces;

    private Color[][] colors;
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(false, 1000, 1000, Color.black);

    public Net(Simulator sim) {
        super(sim);
        points = new Point3d[size][size];
        forces = new Vec[size][size];
        colors = new Color[size][size];
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[x].length; y++) {
                points[x][y] = new Point3d(distBetweenPoints * (x + Math.random() - 0.5) - distBetweenPoints * size / 2, distBetweenPoints * (Math.random() - 0.5) - 50, distBetweenPoints * (y + Math.random() - 0.5) - distBetweenPoints * size / 2);
                forces[x][y] = new Vec(0, 0, 0);
                colors[x][y] = new Color((int) (163 + 40 * Math.random() - 20),
                        (int) (153 + 40 * Math.random() - 20),
                        (int) (239 + 20 * Math.random() - 10));
            }
        }
        drawer.setCameraAngleHorizontal(Math.PI / 4);
        drawer.setCameraAngleVertical(Math.PI / 4);
        drawer.setZoom(0.1);
    }

    @Override
    public void tick(double dtInSec) {
        getInput();
        calcNetForces();
        airResist();
        movePoints();
    }

    private void movePoints() {
        for (int x = 1; x < points.length - 1; x++) {
            for (int y = 1; y < points[x].length - 1; y++) {
                points[x][y].add(forces[x][y]);
                points[x][y].add(gravity);
            }
        }
    }

    private void getInput() {
        if (keymanager.f) {
            moveEdge(new Vec(0, 100, 0));
        }
        if (keymanager.g) {
            moveEdge(new Vec(0, -100, 0));
        }
        if (keymanager.v) {
            moveEdge(new Vec(100, 0, 0));
        }
        if (keymanager.b) {
            moveEdge(new Vec(-100, 0, 0));
        }
    }

    private void airResist() {
        for (int x = 1; x < points.length - 1; x++) {
            for (int y = 1; y < points[x].length - 1; y++) {
                forces[x][y].scale(airResist);
            }
        }
    }

    private void calcNetForces() {
        for (int x = 1; x < points.length - 1; x++) {
            for (int y = 1; y < points[x].length - 1; y++) {
                forces[x][y].add(points[x][y].getConnectingVectorTo(points[x][y + 1]).getLength() > 60 ? points[x][y].getConnectingVectorTo(points[x][y + 1]).scale(0.1) : new Vec(0, 0, 0));
                forces[x][y].add(points[x][y].getConnectingVectorTo(points[x + 1][y]).getLength() > 60 ? points[x][y].getConnectingVectorTo(points[x + 1][y]).scale(0.1) : new Vec(0, 0, 0));
                forces[x][y].add(points[x][y].getConnectingVectorTo(points[x][y - 1]).getLength() > 60 ? points[x][y].getConnectingVectorTo(points[x][y - 1]).scale(0.1) : new Vec(0, 0, 0));
                forces[x][y].add(points[x][y].getConnectingVectorTo(points[x - 1][y]).getLength() > 60 ? points[x][y].getConnectingVectorTo(points[x - 1][y]).scale(0.1) : new Vec(0, 0, 0));
                forces[x][y].scale(0.99);
            }
        }
    }

    private void moveEdge(Vec delta) {
        for (int i = 0; i < points.length; i++) {
            points[0][i].add(delta);
            points[points.length - 1][i].add(delta);
        }
        for (int i = 1; i < points.length - 1; i++) {
            points[i][0].add(delta);
            points[i][points.length - 1].add(delta);
        }
    }

    @Override
    public void render(Graphics g) {
        cart.render(drawer, g);
        // ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[x].length; y++) {
                drawer.drawDot(points[x][y], 4, colors[x][y], g);
            }
        }
        for (int x = 0; x < points.length - 1; x++) {
            for (int y = 0; y < points[x].length - 1; y++) {
                drawer.drawLine(points[x][y], points[x + 1][y], g);
                drawer.drawLine(points[x][y], points[x][y + 1], g);
            }
        }

        for (int i = 0; i < points.length - 1; i++) {
            drawer.drawLine(points[i][points[i].length - 1], points[i + 1][points[i].length - 1], g);
            drawer.drawLine(points[points.length - 1][i], points[points.length - 1][i + 1], g);
        }
    }

    @Override
    public void reset() {
        points = new Point3d[size][size];
        forces = new Vec[size][size];
        for (int x = 0; x < points.length; x++) {
            for (int y = 0; y < points[x].length; y++) {
                points[x][y] = new Point3d(distBetweenPoints * (x + Math.random() - 0.5) - distBetweenPoints * size / 2, distBetweenPoints * (Math.random() - 0.5) - 50, distBetweenPoints * (y + Math.random() - 0.5) - distBetweenPoints * size / 2);
                forces[x][y] = new Vec(0, 0, 0);
            }
        }
    }
}