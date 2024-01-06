package spacesimulation;

import spacesimulation.algebra.Point3d;
import spacesimulation.algebra.Vec;

import java.awt.*;

public class Mass extends Point3d implements Entity {
    private final Vec velocity;
    private final Vec acceleration;
    private final double mass;

    private InteractionStatus status = InteractionStatus.Movable;

    public Mass(double mass, double x, double y, double z) {
        super(x, y, z);
        if (mass == 0) throw new IllegalArgumentException("Mass can't be equal to 0");
        velocity = new Vec(0, 0, 0);
        acceleration = new Vec(0, 0, 0);
        this.mass = mass;
    }

    public Mass(double mass, Point3d pos) {
        this(mass, pos.x, pos.y, pos.z);
    }

    public Mass(double mass, Vec positionVector) {
        this(mass, positionVector.x, positionVector.y, positionVector.z);
    }

    @Override
    public void tick() {
        accelerate();
        move();
    }

    @Override
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
        this.acceleration.add(Vec.scale(force, 1.0 / mass));
    }

    public void accelerate(Vec acceleration) {
        this.acceleration.add(acceleration);
    }

    public void removeAccelerationInDirection(Vec direction) {
        acceleration.add(acceleration.linearProjection(direction).scale(-1));
    }

    public Vec getImpulse() {
        return Vec.scale(velocity, mass);
    }

    public Vec getCurrentForce() {
        return acceleration;
    }

    public Vec getVelocity() { return velocity; }

    public void setVelocity(Vec velocity) {
        this.velocity.x = velocity.x;
        this.velocity.y = velocity.y;
        this.velocity.z = velocity.z;
    }

    public double getMass() { return mass; }

    public Vec getAcceleration() { return acceleration; }

    public InteractionStatus getStatus() {
        return status;
    }

    public void setStatus(InteractionStatus status) {
        this.status = status;
    }

    public enum InteractionStatus {
        Immovable,
        Movable
    }
}
