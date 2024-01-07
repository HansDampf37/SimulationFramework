package spacesimulation.physics;

import spacesimulation.Entity;
import spacesimulation.Graphics3d;
import spacesimulation.algebra.Vec;
import spacesimulation.physics.Collision;
import spacesimulation.physics.Mass;

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

    public void tick(double dtInSec) {
        if (m1.getDistanceTo(m2) >= maxDistance) {
            Vec ropeDir = m1.getDirectionTo(m2);
            Vec dif = Vec.sub(m1.getVelocity(), m2.getVelocity());
            if (!dif.hasSharpAngleTo(ropeDir)) Collision.Companion.occur(m1, m2, 1);
        }
    }

    @Override
    public void render(Graphics3d drawer, Graphics g) {
        drawer.drawLine(m1, m2, g);
    }
}
