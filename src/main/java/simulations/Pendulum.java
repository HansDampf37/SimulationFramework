package simulations;

import java.awt.Graphics;
import java.awt.Color;

import spacesimulation.*;
import spacesimulation.algebra.*;

public class Pendulum extends Simulation {
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(true, 500, 500, Color.black);
    private final int maxRopeSegmentLength = 10000;
    private final Vec gravity = new Vec(0, -0.1, 0);
    private Point3d[] points;
    private Vec[] velocities;
    private Vec[] forces;
    private final double airResist = 0.999;
    
    public Pendulum(int amountOfPoints, Simulator sim) {
        super(sim);
        points = new Point3d[amountOfPoints];
        velocities = new Vec[amountOfPoints];
        forces = new Vec[amountOfPoints];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point3d(0, -i * maxRopeSegmentLength * 0.5, 0);
            forces[i] = new Vec(0, 0, 0);
        }
        drawer.setZoom(0.03);
    }

    @Override
    public void tick() {
        getInput();
        calcForcesOnPoints();
        airResist();
        movePoints();
    }

    private void getInput() {
        if (keymanager.up) forces[0] = (new Vec(10, 0, 0));
        if (keymanager.down) forces[0] = (new Vec(-10, 0, 0));
        if (keymanager.left) forces[0] = (new Vec(0, 0, 10));
        if (keymanager.right) forces[0] = (new Vec(0, 0, -10));
    }

    private void airResist() {
        for (int i = 0; i < forces.length; i++) {
            forces[i].scale(airResist);
        }
    }

    private void movePoints() {
        points[0].add(forces[0]);
        for (int i = 1; i < points.length; i++) {
            points[i].add(forces[i]);
            if (points[i - 1].getConnectingVectorTo(points[i]).getLength() > maxRopeSegmentLength) {
                Vec posVec = points[i - 1].getConnectingVectorTo(points[i]);
                double scalar = maxRopeSegmentLength / posVec.getLength();
                posVec.scale(scalar);
                points[i].set(points[i - 1].getPositionVector().add(posVec));
            }
        }
    }

    private void calcForcesOnPoints() {
        for (int i = 1; i < points.length; i++) {
            if (points[i].getDistanceTo(points[i - 1]) >= maxRopeSegmentLength) {
                Vec ropeDir = points[i].getDirectionTo(points[i - 1]);
                if (i == 1) {
                    Vec forceInRopeDir = Vec.linearProjection(forces[1], ropeDir);
                    forceInRopeDir.scale(-2);
                    forces[1].add(forceInRopeDir);
                    forces[1].add(Vec.linearProjection(forces[0], forceInRopeDir));
                } else {

                }
            }
            forces[i].add(gravity);
        }
    }

    @Override
    public void render(Graphics g) {
        cart.render(drawer, g);
        for (int i = 0; i < points.length - 1; i++) {
            drawer.drawDot(points[i], 4, Color.lightGray, g);
            drawer.drawLine(points[i], points[i + 1], g);
        }
        drawer.drawDot(points[points.length - 1], 4, Color.lightGray, g);
    }

    @Override
    public void reset() {
        points = new Point3d[points.length];
        forces = new Vec[points.length];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point3d(0, -i * maxRopeSegmentLength * 0.5, 0);
            forces[i] = new Vec(0, 0, 0);
            velocities[i] = new Vec(0, 0, 0);
        }
    }
}