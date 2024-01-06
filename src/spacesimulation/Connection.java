package spacesimulation;

import spacesimulation.algebra.Vec;

import java.awt.*;

public class Connection implements Entity {

    private final Mass m1;

    private final Mass m2;

    private final double maxDistance;

    public Connection(Mass m1, Mass m2, double maxDistance) {
        this.m1 = m1;
        this.m2 = m2;
        this.maxDistance = maxDistance;
    }

    public void tick() {
        if (m1.getDistanceTo(m2) >= maxDistance) {
            Vec ropeDir = m1.getDirectionTo(m2);
            //mass 1 applies a ratio of its impulse in rope direction to mass 2
            double ratioOfForceNotLostInRope = 0.8;
            if (!m1.getImpulse().hasSharpAngleTo(ropeDir)) {
                Vec impulseInRopeDirection = Vec.linearProjection(m1.getImpulse(), ropeDir);
                if (m2.getStatus() == Mass.InteractionStatus.Movable) {
                    m2.applyForce(Vec.scale(impulseInRopeDirection, ratioOfForceNotLostInRope));
                }
                if (m1.getStatus() == Mass.InteractionStatus.Movable) {
                    m1.applyForce(impulseInRopeDirection.scale(-1));
                }
            }
            //mass 2 applies a ratio of its force in rope direction to mass 1
            if (m2.getImpulse().hasSharpAngleTo(ropeDir)) {
                Vec impulseInRopeDirection = Vec.linearProjection(m2.getImpulse(), ropeDir);
                if (m1.getStatus() == Mass.InteractionStatus.Movable) {
                    m1.applyForce(Vec.scale(impulseInRopeDirection, ratioOfForceNotLostInRope));
                }
                if (m2.getStatus() == Mass.InteractionStatus.Movable) {
                    m2.applyForce(impulseInRopeDirection.scale(-1));
                }
            }
        }
    }

    @Override
    public void render(Graphics3d drawer, Graphics g) {
        drawer.drawLine(m1, m2, g);
    }
}
