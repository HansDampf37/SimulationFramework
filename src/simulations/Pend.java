package simulations;

import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

import spacesimulation.*;
import spacesimulation.algebra.*;

public class Pend extends MassSimulation {
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(true, 500, 500, Color.black);
    private final int maxRopeSegmentLength = 5000;
    private final double ratioOfForceNotLostInRope = 0.8;
    private final int amountOfPoints;
    
    public Pend(int amountOfPoints, Simulator sim) {
        super(0.999, new Vec(0, -0.5, 0), sim);
        this.amountOfPoints = amountOfPoints;
        reset();
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
        for (int i = 1; i < masses.size(); i++) {
            //rope between mass i and mass i-1 is taut
            if (masses.get(i).getDistanceTo(masses.get(i - 1)) >= maxRopeSegmentLength) {
                Vec ropeDir = masses.get(i).getDirectionTo(masses.get(i - 1));
                if (i == 1) {
                    Vec velocityInRopeDirection = Vec.linearProjection(masses.get(1).getImpulse(), ropeDir);
                    velocityInRopeDirection.scale(-2);
                    masses.get(1).accelerate(velocityInRopeDirection);
                    masses.get(1).applyForce(Vec.linearProjection(masses.get(0).getImpulse(), velocityInRopeDirection));
                } else {
                    //mass i applies a ratio of its force in rope direction to mass i-1
                    Vec velocityInRopeDirection = Vec.linearProjection(masses.get(i).getImpulse(), ropeDir);
                    masses.get(i - 1).applyForce(Vec.scale(velocityInRopeDirection, ratioOfForceNotLostInRope));
                    masses.get(i).applyForce(velocityInRopeDirection.scale(-1));
                    //mass i - 1 applies a ratio of its force in rope direction to mass i
                    velocityInRopeDirection = Vec.linearProjection(masses.get(i- 1).getImpulse(), ropeDir);
                    masses.get(i).applyForce(Vec.scale(velocityInRopeDirection, ratioOfForceNotLostInRope));
                    masses.get(i - 1).applyForce(velocityInRopeDirection.scale(-1));
                }
            }
        }
    }

    private void getInput() {
        if (keymanager.f) masses.get(0).accelerate(new Vec(0.1, 0, 0));
        if (keymanager.g) masses.get(0).accelerate(new Vec(-0.1, 0, 0));
        if (keymanager.v) masses.get(0).accelerate(new Vec(0, 0, 0.1));
        if (keymanager.b) masses.get(0).accelerate(new Vec(0, 0, -0.1));
        if (keymanager.up) masses.get(1).applyForce(new Vec(10, 0, 0));
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
        masses = new ArrayList<Mass>();
        for (int i = 0; i < amountOfPoints; i++) addNewMass(new Point3d(0, -i * maxRopeSegmentLength, 0), i != 0);
    }
}