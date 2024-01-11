package spacesimulation.physics

import spacesimulation.Entity
import spacesimulation.Graphics3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Collision.Companion.occur
import java.awt.Color
import java.awt.Graphics
import kotlin.math.pow

abstract class Connection(
    protected val m1: Mass,
    protected val m2: Mass,
    protected val maxEnergy: Double,
    protected var broken: Boolean = false) : Entity {
    abstract override fun tick(dt: Seconds)

    override fun render(drawer: Graphics3d, g: Graphics) {
        if (!broken) drawer.drawLine(m1, m2, g)
    }
}

class ImpulseConnection(m1: Mass, m2: Mass, private val maxDistance: Double, maxEnergy: Double, private val springConstant: Double = 300.0) : Connection(m1, m2, maxEnergy) {
    override fun tick(dt: Seconds) {
        if (broken) return
        val dist = m1.getDistanceTo(m2)
        if (dist >= maxDistance) {
            val ropeDir = m1.getDirectionTo(m2)
            val delta = dist - maxDistance

            val difVelocity = m1.velocity - m2.velocity
            var energy: Double = if (!difVelocity.hasSharpAngleTo(ropeDir)) occur(m1, m2, 0.95) else 0.0
            val force = ropeDir * (delta).pow(3) * springConstant / 3
            energy += springConstant * delta.pow(4) / 4
            if (energy > maxEnergy) {
                broken = true
                return
            }
            if (m1.status == Mass.Status.Movable && m2.status == Mass.Status.Movable) {
                m1.applyForce(force)
                m2.applyForce(-force)
                //m1.set(m1 + ropeDir * delta)
                //m2.set(m2 - ropeDir * delta)
            } else if (m2.status == Mass.Status.Movable) {
                m2.applyForce(-force)
                //m2.set(m1 + ropeDir * maxDistance)
            }
            else if (m1.status == Mass.Status.Movable) {
                m1.applyForce(force)
                //m1.set(m2 - ropeDir * maxDistance)
            }
        }
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        if (broken) return
        val oldColor = g.color
        g.color = if (m1.getDistanceTo(m2) < maxDistance) Color.green else Color.red
        drawer.drawLine(m1, m2, g)
        g.color = oldColor
    }
}

class SpringConnection(m1: Mass, m2: Mass, private val maxDistance: Double, private val springConstant: Double = 1000.0, maxEnergy: Double) : Connection(m1, m2, maxEnergy) {
    override fun tick(dt: Seconds) {
        val ropeDir = m1.getDirectionTo(m2)
        val force = springConstant * (m1.getDistanceTo(m2) - maxDistance).pow(2)
        if (m1.status == Mass.Status.Movable && m2.status == Mass.Status.Movable) {
            m1.applyForce(Vec.scale(ropeDir, force / 2))
            m2.applyForce(Vec.scale(ropeDir, -force / 2))
        } else if (m1.status == Mass.Status.Movable && m2.status == Mass.Status.Immovable) {
            m1.applyForce(Vec.scale(ropeDir, force))
        } else if (m1.status == Mass.Status.Immovable && m2.status == Mass.Status.Movable) {
            m2.applyForce(Vec.scale(ropeDir, -force))
        }
    }
}

class FlexibleConnection(m1: Mass, m2: Mass, private val maxDistance: Double, private val springConstant: Double = 100000.0, maxEnergy: Double) : Connection(m1, m2, maxEnergy) {
    override fun tick(dt: Seconds) {
        if (broken) return
        if (m1.getDistanceTo(m2) >= maxDistance) {
            val ropeDir = m1.getDirectionTo(m2)
            val distanceFromEquilibrium = m1.getDistanceTo(m2) - maxDistance

            val force = springConstant * (distanceFromEquilibrium)
            if (m1.status == Mass.Status.Movable && m2.status == Mass.Status.Movable) {
                m1.applyForce(Vec.scale(ropeDir, force / 2))
                m2.applyForce(Vec.scale(ropeDir, -force / 2))
            } else if (m1.status == Mass.Status.Movable && m2.status == Mass.Status.Immovable) {
                m1.applyForce(Vec.scale(ropeDir, force))
            } else if (m1.status == Mass.Status.Immovable && m2.status == Mass.Status.Movable) {
                m2.applyForce(Vec.scale(ropeDir, -force))
            }
            val energy = springConstant * distanceFromEquilibrium.pow(2) / 2
            if (energy > maxEnergy) broken = true
        }
    }

}
