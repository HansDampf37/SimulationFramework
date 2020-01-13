package simulations;

import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

import spacesimulation.*;
import spacesimulation.algebra.*;

public class Pend extends MassSimulation {
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(true, 500, 500, Color.black);
    private final int maxRopeSegmentLength = 10000;
    
    public Pend(int amountOfPoints, Simulator sim) {
        super(0.999, new Vec(0, -0.1, 0), sim);
        for (int i = 0; i < amountOfPoints; i++) addNewMass(new Point3d(0, -i * maxRopeSegmentLength * 0.5, 0));
        drawer.setZoom(0.03);
    }

    @Override
    public void buffer() {
        for (int i = 1; i < masses.size(); i++) {
            if (masses.get(i - 1).getConnectingVectorTo(masses.get(i)).getLength() > maxRopeSegmentLength) {
                Vec posVec = masses.get(i - 1).getConnectingVectorTo(masses.get(i));
                double scalar = maxRopeSegmentLength / posVec.getLength();
                posVec.scale(scalar);
                masses.get(i).set(masses.get(i - 1).getPositionVector().add(posVec));
            }
        }
    }

    @Override
    public void calcForces() {
        getInput();
        masses.get(0).accelerate(Vec.scale(gravity, -1));
        for (int i = 1; i < masses.size(); i++) {
            if (masses.get(i).getDistanceTo(masses.get(i - 1)) >= maxRopeSegmentLength) {
                Vec ropeDir = masses.get(i).getDirectionTo(masses.get(i - 1));
                if (i == 1) {
                    Vec velocityInRopeDirection = Vec.linearProjection(masses.get(1).velocity, ropeDir);
                    velocityInRopeDirection.scale(-2);
                    masses.get(1).accelerate(velocityInRopeDirection);
                    masses.get(1).accelerate(Vec.linearProjection(masses.get(0).acceleration, velocityInRopeDirection));
                } else {

                }
            }
        }
    }

    private void getInput() {
        if (keymanager.f) masses.get(0).accelerate(new Vec(0.1, 0, 0));
        if (keymanager.g) masses.get(0).accelerate(new Vec(-0.1, 0, 0));
        if (keymanager.v) masses.get(0).accelerate(new Vec(0, 0, 0.1));
        if (keymanager.b) masses.get(0).accelerate(new Vec(0, 0, -0.1));
    }

    @Override
    public void render(Graphics g) {
        cart.render(drawer, g);
        for (int i = 0; i < masses.size() - 1; i++) {
            masses.get(i).render(drawer, g);
            drawer.drawLine(masses.get(i), masses.get(i + 1), g);
        }
        masses.get(masses.size() - 1).render(drawer, g);
        g.drawString(masses.get(1).toString(), 100, 100);
    }

    @Override
    public void reset() {
        int size = masses.size();
        masses = new ArrayList<Mass>();
        for (int i = 0; i < size; i++) addNewMass(new Point3d(0, -i * maxRopeSegmentLength * 0.5, 0));
    }
}