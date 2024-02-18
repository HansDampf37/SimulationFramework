package simulations

import framework.Simulation
import algebra.Vec3
import framework.WatchBoolean
import framework.WatchDouble
import framework.WatchInt
import framework.physics.Seconds
import toVec
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Fractal : Simulation("Fractal") {
    @WatchInt("Corners", 3, 10)
    private var dim: Int = 3
        set(value) {
            field = value
            reset()
        }
    private val corners: ArrayList<Vec3> = ArrayList()
    private val points: MutableList<Vec3> = ArrayList()
    @WatchDouble("PointsPerSecond", 1.0, 10000.0)
    private var pointsPerSecond: Double = 100.0
    @WatchBoolean("Skip to End")
    private var skip = false
    private val secondsPerPoint: Double
        get() = 1.0 / pointsPerSecond

    private var secondsSinceLastPoint = 0.0

    init {
        camera.x = 0.0
        camera.y = 0.0
        camera.z = 10.0
        camera.focalLength = 1.0
        camera.zoom = 0.022
        camera.phi = 0.0
        camera.theta = PI
        reset()
    }

    override fun tick(dt: Seconds) {
        if (!isInitialized()) return
        if (!skip) {
            secondsSinceLastPoint += dt
            if (secondsSinceLastPoint < secondsPerPoint) return

            secondsSinceLastPoint = 0.0
        }
        val corner = corners.random()
        val last = points.last()
        val newPoint = last + (corner - last) / 2
        synchronized(points) {
            points.add(newPoint)
        }
    }

    private fun isInitialized(): Boolean = corners.isNotEmpty()

    override fun render() {
        val color = Conf.colorScheme.smallObjectColor.toVec()
        synchronized(points) {
            points.indices.forEach { camera.renderPixel(points[it], color, null) }
        }
    }

    override fun reset() {
        val radius = 100.0
        synchronized(points) {
            points.clear()
            corners.clear()
            for (i in 0 until dim) {
                val phi = 2 * Math.PI / dim * i
                val x = radius * cos(phi + Math.PI / 2)
                val y = radius * sin(phi + Math.PI / 2)
                corners.add(Vec3(x, y, 0.0))
            }
            points.add(Vec3(Math.random() * radius * 2 - radius, Math.random() * radius * 2 - radius, 0.0))
        }
    }
}

fun main() {
    Fractal().start()
}