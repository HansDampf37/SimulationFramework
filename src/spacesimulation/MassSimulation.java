package spacesimulation;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import spacesimulation.algebra.Point3d;
import spacesimulation.algebra.Vec;

public abstract class MassSimulation extends Simulation {
    protected double frictionFactor;
    protected Vec gravity;
    protected List<Mass> masses = new ArrayList<Mass>();
    protected HashMap<Mass, Boolean> affectedByGravity = new HashMap<Mass, Boolean>();

    public MassSimulation(double frictionFactor, Vec gravity, Simulator simulator) {
        super(simulator);
        this.frictionFactor = frictionFactor;
        this.gravity = gravity;
        masses = new ArrayList<Mass>();
    }

    public void addNewMass(double mass, Point3d pos, boolean affectedByGravity) {
        Mass newMass = new Mass(mass, pos);
        masses.add(newMass);
        this.affectedByGravity.put(newMass, affectedByGravity);

    }

    public void addNewMass(Point3d pos, boolean affectedByGravity) {
        addNewMass(1, pos, affectedByGravity);
    }

    @Override
    public void tick() {
        calcForces();
        for (Mass mass : masses) {
            mass.tick();
            if (affectedByGravity.get(mass)) mass.accelerate(gravity);
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
        private Vec velocity;
        private Vec acceleration;
        private double mass;

        public Mass(double mass, double x, double y, double z) {
            super(x, y, z);
            if (mass == 0) throw new IllegalArgumentException("Mass can't be equal to 0");
            velocity = new Vec(0, 0, 0);
            acceleration = new Vec(0, 0, 0);
            this.mass = mass;
        }

        public Mass(double mass, Point3d pos) {
            this (mass, pos.x, pos.y, pos.z);
        }

        public Mass(double mass, Vec positionVector) {
            this (mass, positionVector.x, positionVector.y, positionVector.z);
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
            this.acceleration.add(Vec.scale(force, 1.0/mass));
        }

        public void accelerate(Vec acceleration) {
            this.acceleration.add(acceleration);
        }

        public void removeAccelerationInDireciont(Vec direction) {
            acceleration.add(acceleration.linearProjection(direction).scale(-1));
        }

        public Vec getImpulse() {
            return Vec.scale(velocity, mass);
        }

        public Vec getCurrentForce() {
            return acceleration;
        }
    }
}