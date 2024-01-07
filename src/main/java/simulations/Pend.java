package simulations;

import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;
import java.util.List;

import spacesimulation.*;
import spacesimulation.algebra.*;
import spacesimulation.physics.Connection;
import spacesimulation.physics.Mass;

public class Pend extends MassSimulation {
    CartesianCoordinateSystem cart = new CartesianCoordinateSystem(true, 500000, 500000, Color.black);
    private final int maxRopeSegmentLength;
    private final int amountOfPoints;

    private final List<Connection> connections = new ArrayList<>();
    
    public Pend(int amountOfPoints, Simulator sim) {
        super(1, new Vec(0, -9.81, -0), sim);
        this.amountOfPoints = amountOfPoints;
        maxRopeSegmentLength = 100/amountOfPoints;
        reset();
        drawer.setZoom(3);
        drawer.setCameraAngleHorizontal(0.2);
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
        for (Connection c : connections) {
            c.tick();
        }
    }

    private void getInput() {
        if (keymanager.f) masses.get(0).accelerate(new Vec(0.01, 0, 0));
        if (keymanager.g) masses.get(0).accelerate(new Vec(-0.01, 0, 0));
        if (keymanager.v) masses.get(0).accelerate(new Vec(0, 0, 5));
        if (keymanager.b) masses.get(0).accelerate(new Vec(0, 0, -5));
        if (keymanager.up) masses.get(1).applyForce(new Vec(10, 0, 0));
    }

    @Override
    public void render(Graphics g) {
        cart.render(drawer, g);
        for (Mass m : masses) m.render(drawer, g);
        for (Connection c : connections) c.render(drawer, g);

        for (int i = 0; i < masses.size(); i++) {
             g.drawString(masses.get(i).toString(), 10, 100 + i * 20);
        }
    }

    @Override
    public void reset() {
        masses.clear();
        for (int i = 0; i < amountOfPoints; i++) {
            addNewMass(new Point3d(0, -i * maxRopeSegmentLength * 0.7, -i * maxRopeSegmentLength * 0.7), i != 0);
        }
        masses.get(0).setStatus(Mass.Status.Immovable);

        connections.clear();
        for (int i = 0; i < amountOfPoints - 1; i++) {
            connections.add(new Connection(masses.get(i), masses.get(i + 1), maxRopeSegmentLength));
        }
    }
}