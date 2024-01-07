package spacesimulation.physics

import spacesimulation.Entity
import spacesimulation.Graphics3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Collision.Companion.occur
import java.awt.Graphics

class Connection(private val m1: Mass, private val m2: Mass, private val maxDistance: Double) : Entity {
    override fun tick(dtInSec: Double) {
        if (m1.getDistanceTo(m2) >= maxDistance) {
            val ropeDir = m1.getDirectionTo(m2)
            val dif = Vec.sub(m1.velocity, m2.velocity)
            if (!dif.hasSharpAngleTo(ropeDir)) occur(m1, m2, 1.0)
        }
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawLine(m1, m2, g)
    }
}
