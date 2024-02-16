package framework.physics

import algebra.Vec3
import framework.Camera
import framework.Vertex
import framework.interfaces.Renderable
import framework.interfaces.Status
import framework.interfaces.Tickable
import framework.physics.collisions.Collision.Companion.occur
import kotlin.math.pow

/**
 * A connection connects two [masses][PointMass] [m1] and [m2]. The two masses can then interact with each other in any way for
 * example by sending impulses over the connection or by implementing a spring mechanism.
 * If at some point the connection holds more energy than [maxEnergy] it should [break][broken]. Implementations should
 * check that [broken] == false before [Rendering][render] or [ticking][tick].
 * @see ImpulseConnection
 * @see SpringConnection //TODO
 */
abstract class Connection(
    protected val m1: PointMass,
    protected val m2: PointMass,
    var maxEnergy: Joule,
    var broken: Boolean = false
) : Tickable, Renderable {
    override var color: Vec3? = null

    abstract override fun tick(dt: Seconds)

    override fun render(camera: Camera) {
        if (broken) return
        val v1 = Vertex(m1.positionVector, color ?: Vec3.zero, Vec3.zero)
        val v2 = Vertex(m2.positionVector, color ?: Vec3.zero, Vec3.zero)
        camera.renderLine(v1, v2)
    }

    fun isConnectedTo(mass: PointMass) : Boolean = m1 == mass || m2 == mass
}

/**
 * An ImpulseConnection is a [Connection] that sends impulses between the two [masses][PointMass] if
 *
 * 1. the distance between them > maxDistance
 *
 * 2. they are moving away from each other
 */
class ImpulseConnection(
    m1: PointMass,
    m2: PointMass,
    var maxDistance: Double,
    maxEnergy: Double,
    private val springConstant: Double = 300.0
) : Connection(m1, m2, maxEnergy) {
    override var outlineRasterization: Boolean = false

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
            if (m1.status == Status.Movable && m2.status == Status.Movable) {
                m1.applyForce(force)
                m2.applyForce(-force)
                m1.set(m1 + ropeDir * delta)
                m2.set(m2 - ropeDir * delta)
            } else if (m2.status == Status.Movable) {
                m2.applyForce(-force)
                m2.set(m1 + ropeDir * maxDistance)
            } else if (m1.status == Status.Movable) {
                m1.applyForce(force)
                m1.set(m2 - ropeDir * maxDistance)
            }
        }
    }
}