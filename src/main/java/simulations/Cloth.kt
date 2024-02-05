package simulations

import algebra.Vec
import physics.PhysicsSimulation
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import physics.*
import toVec
import kotlin.math.PI

/**
 * Simulates a grid of [masses][PointMass] connected by [connections][ImpulseConnection] that transmit impulses.
 * The cloth collides with a [Sphere]. Use W,A,S,D to control the camera position and up,down,left,right to control
 * the camera orientation
 * @param size the amount masses in one dimension of the cloth
 */
@SuppressWarnings("unused")
class Cloth(size: Int) : PhysicsSimulation("Cloth") {
    private val points: MutableList<Sphere> = ArrayList()
    private val links: MutableList<ImpulseConnection> = ArrayList()

    @WatchInt("Size", 1, 20)
    private var size: Int = size
        set(value) {
            field = value
            reset()
        }

    @WatchDouble("Sphere Size", 2.0, 10.0)
    private var sphereRadius: Double = 5.0
        set(value) {
            sphere.radius = value
            field = value
        }
    private var sphere = Sphere(0.0, 0.0, 10.0, sphereRadius, 20.0)

    init {
        reset()
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -25.0
        camera.z = 12.0
        camera.theta = 1 * PI / 3
        camera.phi = PI
        camera.focalLength = 10.0
        camera.zoom = 0.01
    }

    override fun reset() {
        super.reset()
        // clothPoints
        synchronized(points) {
            points.clear()
            for (x in 0 until size) {
                for (y in 0 until size) {
                    val isOnEdge = (x == 0) || (y == 0) || (x == size - 1) || (y == size - 1)
                    val mass = Sphere(x.toDouble() - size / 2.0 + 0.5, y.toDouble() - size / 2.0 + 0.5, 0.0, .25, 1.0)
                    mass.status = if (isOnEdge) Status.Immovable else Status.Movable
                    mass.color = Conf.colorScheme.smallObjectColor.toVec() + (Vec.random * 20) - 10
                    register(mass)
                    points.add(mass)
                }
            }
        }

        // cloth connections
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (x + 1 < size) {
                    val link = ImpulseConnection(points[x * size + y], points[(x + 1) * size + y], 1.1, 1000.0)
                    link.color = Conf.colorScheme.linkColor.toVec()
                    links.add(link)
                    register(link)
                }
                if (y + 1 < size) {
                    val link = ImpulseConnection(points[x * size + y], points[x * size + y + 1], 1.6, 1000.0)
                    link.color = Conf.colorScheme.linkColor.toVec()
                    links.add(link)
                    register(link)
                }
            }
        }
        sphere = Sphere(0.0, 0.0, 10.0, sphereRadius, 20.0)
        sphere.color = Conf.colorScheme.bigObjectColor.toVec()
        register(sphere)
    }

    override fun calcForces() {
        synchronized(points) {
            for (clothPoint in points) clothPoint.acceleration = Vec.zero
            sphere.acceleration = Vec.zero
            applyGravity(points + sphere)
        }
    }

    override fun correctState() {
        synchronized(links) {
            val brokenLinks = links.filter { it.broken }
            links.removeAll(brokenLinks.toSet())
            brokenLinks.forEach { unregister(it) }
        }
    }
}