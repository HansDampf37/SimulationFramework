package physics

import algebra.Vec
import framework.*
import physics.Collision.Companion.occur
import java.awt.Color
import java.awt.Graphics
import kotlin.math.pow

abstract class Connection(
    protected val m1: Mass,
    protected val m2: Mass,
    protected val maxEnergy: Double,
    var broken: Boolean = false
) : Simulateable {

    abstract override fun tick(dt: Seconds)

    override fun render(drawer: Graphics3d, g: Graphics) {
        if (!broken) drawer.drawLine(m1, m2, g)
    }

    fun render(camera: Camera) {
        val v1 = Vertex(m1.positionVector, 255 * Vec.ones, Vec.zero)
        val v2 = Vertex(m2.positionVector, 255 * Vec.ones, Vec.zero)
        camera.renderLine(v1, v2)
    }
}

class ImpulseConnection(
    m1: Mass,
    m2: Mass,
    private val maxDistance: Double,
    maxEnergy: Double,
    private val springConstant: Double = 300.0
) : Connection(m1, m2, maxEnergy) {
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
            } else if (m1.status == Mass.Status.Movable) {
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