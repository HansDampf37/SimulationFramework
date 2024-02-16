package simulations

import algebra.Vec
import framework.*
import framework.physics.Seconds
import framework.physics.Sphere
import kotlin.math.cos
import kotlin.math.sin

@Suppress("unused")
class SpheresInTheSky(numDivisions: Int): Simulation("Spheres in the sky") {
    private val dots = ArrayList<Sphere>()

    @WatchInt("Amount", 4, 100)
    var numDivisions = numDivisions
        set(value) {
            field = value
            reset()
        }
    @WatchDouble("Distance", 1.0, 100.0)
    var distance = 35.0
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
        synchronized(dots) { dots.forEach { it.render(camera) } }
        drawLookingDirection()
    }

    private fun drawLookingDirection() {
        val p1 = camera.positionVector - camera.up + camera.lookingDirection * 0.1
        val p2 = camera.positionVector - camera.up + camera.lookingDirection * 10
        camera.renderLine(Vertex(p1, 255 * Vec.ones, Vec.zero), Vertex(p2, Vec.ones, Vec.zero))
    }

    override fun reset() {
        synchronized(dots) {
            dots.clear()
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
                    dots.add(s)
                }
            }
        }
        camera.x = 0.0
        camera.y = 0.0
        camera.z = 0.0
        camera.focalLength = 43.2
        camera.zoom = 0.05
        camera.theta = 0.0
    }
}