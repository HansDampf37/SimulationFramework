package simulations

import algebra.Vec3
import framework.Vertex
import framework.WatchDouble
import framework.WatchInt
import framework.physics.PhysicsSimulation
import framework.physics.Sphere
import times
import java.awt.Color
import java.awt.Graphics
import kotlin.concurrent.withLock
import kotlin.math.PI
import kotlin.math.pow

/**
 * Simulation that simulated points that are repelling each other yet can not leave the surface of a sphere.
 */
@Suppress("unused")
class PlatonSpace(amountOfPoint3ds: Int) : PhysicsSimulation("Platon") {
    @WatchInt("Amount of points", 2, 100)
    private var amountOfPoint3ds: Int = amountOfPoint3ds
        set(value) {
            field = value
            while (points.size > value) unregister(tickLock.withLock { points.removeLast() })
            while (points.size < value) {
                val pos = Vec3.random.normalize() * radius
                val sphere = Sphere(pos.x, pos.y, pos.z, sphereSize, 1.0)
                tickLock.withLock { register(sphere) }
                points.add(sphere)
            }
        }

    private val points: ArrayList<Sphere> = ArrayList()
    private val radius = 4.0
    private var colorLines = Vec3(200, 200, 200)
    private var colorPoints = Vec3(163, 153, 239)

    @WatchDouble("Sphere size", 0.01, 3.0)
    private var sphereSize = 1.0
        set(value) {
            field = value
            points.forEach { it.radius = value }
        }

    init {
        g = 0.0;
        frictionPerSecond = 0.0
        reset()
        camera.zoom = 0.001
        camera.x = 0.42
        camera.y = 0.0
        camera.z = 25.0
        camera.phi = 0.0
        camera.theta = PI
    }

    private fun drawAcceleration(g: Graphics) {
        for (i in points.indices) {
            val force = points[i].acceleration
            g.color = Color.ORANGE
            val shorten = 1 / force.length
            camera.renderLine(
                Vertex(points[i].positionVector, colorLines, Vec3.zero),
                Vertex(points[i].positionVector + force * shorten, colorLines, Vec3.zero)
            )
        }
    }

    override fun correctState() {
        points.forEach { it.position = it.position.normalize() * radius }
    }

    override fun calcForces() {
        for (i in points.indices) {
            val first = points[i]
            for (j in i + 1 until points.size) {
                val other = points[j]
                val dif = first.position - other.position
                val dirFirst = (dif - dif.projectOnto(first.position)).normalize()
                val dirSecond = (-dif + dif.projectOnto(other.position)).normalize()
                val scale = 1 / first.getDistanceTo(other).pow(2.0) * 100
                first.applyForce(dirFirst * scale)
                other.applyForce(dirSecond * scale)
            }
        }
    }

    override fun render() {
        super.render()
        val shortestDist = getShortestDist()
        for (i in 0 until points.size) {
            for (j in i until points.size) {
                if (j != i) {
                    try {
                        if (points[i].getDistanceTo(points[j]) < 1.3 * shortestDist) {
                            camera.renderLine(
                                Vertex(points[i].positionVector, colorLines, Vec3.zero),
                                Vertex(points[j].positionVector, colorLines, Vec3.zero)
                            )
                        }
                    } catch (_: IndexOutOfBoundsException) {
                    }
                }
            }
        }
    }

    private fun getShortestDist(): Double {
        var shortestDist = Int.MAX_VALUE.toDouble()
        for (i in 0 until points.size) {
            for (j in i until points.size) {
                if (j != i) {
                    try {
                        val dist = points[i].getDistanceTo(points[j])
                        if (dist < shortestDist) shortestDist = dist
                    } catch (_: IndexOutOfBoundsException) {
                    }
                }
            }
        }
        return shortestDist
    }

    override fun reset() {
        super.reset()
        synchronized(points) {
            points.clear()
            repeat(amountOfPoint3ds) {
                val pos = Vec3.random.normalize() * radius
                val sphere = Sphere(pos.x, pos.y, pos.z, sphereSize, 1.0)
                register(sphere)
                points.add(sphere)
            }
        }
    }
}

fun main() {
    PlatonSpace(4).start()
}