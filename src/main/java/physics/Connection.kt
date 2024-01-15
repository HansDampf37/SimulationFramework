package physics

import framework.Camera
import framework.Simulateable
import framework.Graphics3d
import physics.Collision.Companion.occur
import java.awt.Color
import java.awt.Graphics
import kotlin.math.pow

abstract class Connection(
    protected val m1: Mass,
    protected val m2: Mass,
    protected val maxEnergy: Double,
    protected var broken: Boolean = false) : Simulateable {
    abstract override fun tick(dt: Seconds)

    override fun render(drawer: Graphics3d, g: Graphics) {
        if (!broken) drawer.drawLine(m1, m2, g)
    }

    fun render(camera: Camera, g: Graphics) {
        if (broken) return
        val (p1, d1) = camera.project(m1.positionVector)
        val (p2, d2) = camera.project(m2.positionVector)
        if (d1 > 0 && d2 > 0) g.drawLine(p1.x.toInt(), p1.y.toInt(), p2.x.toInt(), p2.y.toInt())
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