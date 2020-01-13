package spacesimulation;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import spacesimulation.algebra.Point3d;
import spacesimulation.algebra.Vec;

public abstract class MassSimulation extends Simulation {
    protected double frictionFactor;
    protected Vec gravity;
    protected List<Mass> masses = new ArrayList<Mass>();

    public MassSimulation(double frictionFactor, Vec gravity, Simulator simulator) {
        super(simulator);
        this.frictionFactor = frictionFactor;
        this.gravity = gravity;
        masses = new ArrayList<Mass>();
    }

    public void addNewMass(double mass, Point3d pos) {
        masses.add(new Mass(mass, pos));
    }

    public void addNewMass(Point3d pos) {
        masses.add(new Mass(pos));
    }

    @Override
    public void tick() {
        calcForces();
        for (Mass mass : masses) {
            mass.tick();
            mass.accelerate(gravity);
            mass.velocity = mass.velocity.scale(frictionFactor);
        }
        buffer();
    }

    @Override
    public abstract void render(Graphics g);

    public abstract void calcForces();

    public abstract void buffer();

    @Override
    public abstract void reset();

    public class Mass extends Point3d {
        public Vec velocity;
        public Vec acceleration;
        public double mass;

        public Mass(double mass, double x, double y, double z) {
            super(x, y, z);
            velocity = new Vec(0, 0, 0);
            acceleration = new Vec(0, 0, 0);
        }

        public Mass(double mass, Point3d pos) {
            this (mass, pos.x, pos.y, pos.z);
        }

        public Mass(double mass, Vec positionVector) {
            this (mass, positionVector.x, positionVector.y, positionVector.z);
        }

        public Mass(double x, double y, double z) {
            this (1, x, y, z);
        }

        public Mass(Point3d pos) {
            this (1, pos);
        }

        public Mass(Vec positionVector) {
            this (1, positionVector);
        }

        public void tick() {
            accelerate();
            move();
        }

        public void render(Graphics3d drawer, Graphics g) {
            drawer.drawDot(this, 4, Color.white, g);
        }

        private void accelerate() {
            velocity.add(acceleration);
            acceleration.scale(0);
        }

        private void move() {
            add(velocity);
        }

        public void applyForce(Vec force) {
            this.acceleration.add(force.scale(1/mass));
        }

        public void accelerate(Vec acceleration) {
            this.acceleration.add(acceleration);
        }
    }
}