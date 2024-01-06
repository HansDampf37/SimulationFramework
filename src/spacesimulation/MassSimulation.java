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
    protected List<Mass> masses;
    protected HashMap<Mass, Boolean> affectedByGravity = new HashMap<Mass, Boolean>();

    public MassSimulation(double frictionFactor, Vec gravity, Simulator simulator) {
        super(simulator);
        this.frictionFactor = frictionFactor;
        this.gravity = gravity;
        masses = new ArrayList<>();
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
            mass.setVelocity(mass.getVelocity().scale(frictionFactor));
        }
        buffer();
    }

    @Override
    public abstract void render(Graphics g);

    public abstract void calcForces();

    public abstract void buffer();

    @Override
    public abstract void reset();

}

