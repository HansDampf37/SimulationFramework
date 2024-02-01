package simulations

import algebra.Vec
import framework.MassSimulation
import framework.WatchDouble
import framework.WatchInt
import framework.interfaces.Status
import physics.*
import kotlin.math.PI

/**
 * Simulates a grid of [masses][Mass] connected by [connections][ImpulseConnection] that transmit impulses.
 * The cloth collides with a [Sphere]. Use W,A,S,D to control the camera position and up,down,left,right to control
 * the camera orientation
 * @param size the amount masses in one dimension of the cloth
 */
@SuppressWarnings("unused")
class Cloth(size: Int): MassSimulation<Sphere>("Cloth") {
    @WatchInt("Size", 1, 20)
    private var size: Int = size
        set(value) {
            field = value
            reset()
        }
    private val connections: MutableList<Connection> = ArrayList()
    @Suppress("SameParameterValue")
    @WatchDouble("Sphere Size", 2.0, 10.0)
    private var sphereRadius: Double = 5.0
        set(value) {
            sphere.radius = value
            field = value
        }
    private var sphere = Sphere(0.0, 0.0, 10.0, sphereRadius, 20.0)
    init {
        reset()
        drawer.setZoom(30.0)
        drawer.setCameraAngleHorizontal(0.2)
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -25.0
        camera.z = 12.0
        camera.theta = 1 * PI / 3
        camera.phi = PI
        camera.focalLength = 10.0
        camera.zoom = 0.01
    }
    override fun render() {
        synchronized(masses) { masses.forEach{it.render(camera)} }
        synchronized(connections) { connections.filter { !it.broken }.forEach{ it.render(camera) } }
    }

    override fun calcForces(dt: Seconds) {
        input()
        sphere.tick(dt)
        synchronized(connections) {
            connections.shuffle()
            connections.forEach { it.tick(dt) }
        }
        synchronized(masses)  {
            masses.shuffle()
            masses.forEach {
                if (it != sphere) {
                    if (sphere.testForCollision(it)) {
                        Collision.occur(sphere, it, 1.0)
                        val targetDistance = sphere.radius + it.radius
                        val overlap = targetDistance - sphere.getDistanceTo(it)
                        val massMovable = it.status == Status.Movable
                        val overlap1 = if (massMovable) sphere.mass / (it.mass + sphere.mass) * overlap else 0.0
                        val overlap2 = if (massMovable) it.mass / (it.mass + sphere.mass) * overlap else overlap
                        if ((it.positionVector - sphere.positionVector).length != 0.0) {
                            sphere.set(sphere + it.getDirectionTo(sphere) * overlap2)
                            it.set(it + sphere.getDirectionTo(it) * overlap1)
                        }
                    }
                }
            }
        }
    }

    private fun input() {
        /*if (keyManager.up) sphere.accelerate(Vec(40.0, 0.0, 0.0))
        if (keyManager.down) sphere.accelerate(Vec(-40.0, 0.0, 0.0))
        if (keyManager.left) sphere.accelerate(Vec(0.0, 0.0, 40.0))
        if (keyManager.right) sphere.accelerate(Vec(0.0, 0.0, -40.0))
        if (keyManager.shift) sphere.accelerate(Vec(0.0, 40.0, 0.0))
        if (keyManager.space) sphere.accelerate(Vec(0.0, -40.0, 0.0))*/
    }

    override fun reset() {
        synchronized(masses) {
            masses.clear()
            for (x in 0 until size) {
                for (z in 0 until size) {
                    val isOnEdge = (x == 0) or (z == 0) or (x == size - 1) or (z == size - 1)
                    val mass = Sphere(x.toDouble() - size / 2.0 + 0.5, z.toDouble() - size / 2.0 + 0.5, 0.0, .25, 1.0)
                    mass.status = if (isOnEdge) Status.Immovable else Status.Movable
                    mass.color = Conf.mass_color + (Vec.random * 20) - 10
                    addNewMass(mass)
                    masses.last().status = if (isOnEdge) Status.Immovable else Status.Movable
                }
            }
        }
        synchronized(connections) {
            synchronized(masses) {
                connections.clear()
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        if (x + 1 < size) connections.add(
                            ImpulseConnection(
                                masses[x * size + y],
                                masses[(x + 1) * size + y],
                                1.1,
                                1000.0
                            )
                        )
                        if (y + 1 < size) connections.add(
                            ImpulseConnection(
                                masses[x * size + y],
                                masses[x * size + y + 1],
                                1.1,
                                1000.0
                            )
                        )
                    }
                }
            }
        }
        sphere = Sphere(0.0, 0.0, 10.0, sphereRadius, 20.0)
        addNewMass(sphere)
    }
}