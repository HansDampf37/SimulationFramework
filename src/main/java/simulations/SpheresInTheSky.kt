package simulations

import algebra.Vec
import framework.Simulation
import framework.Vertex
import framework.WatchDouble
import framework.WatchInt
import physics.Seconds
import physics.Sphere
import kotlin.math.cos
import kotlin.math.sin

class SpheresInTheSky(numDivisions: Int): Simulation("Spheres in the sky") {
    private val spheres = ArrayList<Sphere>()

    @WatchInt("Amount", 4, 100)
    var numDivisions = numDivisions
        set(value) {
            field = value
            reset()
        }
    @WatchDouble("Distance", 1.0, 100.0)
    var distance = 10.0
        set(value) {
            field = value
            reset()
        }
    @WatchDouble("Radius", 0.25, 25.0)
    var radius = 1.0
        set(value) {
            field = value
            reset()
        }


    init {
        reset()
    }

    override fun tick(dt: Seconds) = Unit

    override fun render() {
        spheres.forEach { it.render(camera) }
        drawLookingDirection()
    }

    private fun drawLookingDirection() {
        val p1 = camera.positionVector - camera.up + camera.lookingDirection * 0.1
        val p2 = camera.positionVector - camera.up + camera.lookingDirection * 10
        camera.renderLine(Vertex(p1, Vec.ones, Vec.zero), Vertex(p2, Vec.ones, Vec.zero))
        println("fw" + camera.lookingDirection)
        println("up:" + camera.up)
        println("left:" + camera.left)
    }

    override fun reset() {
        for (lat in 0 until numDivisions) {
            for (lon in 0 until numDivisions * 2) {
                val theta = (lat * Math.PI / numDivisions).toFloat()
                val phi = (lon * 2 * Math.PI / (numDivisions * 2)).toFloat()
                val s = Sphere(
                    distance * sin(theta) * cos(phi),
                    distance * sin(theta) * sin(phi),
                    distance * cos(theta),
                    radius,
                    1.0
                )
                spheres.add(s)
            }
        }
        camera.x = 0.0
        camera.z = 0.0
        camera.z = 0.0
        camera.focalLength = 10.5
    }
}