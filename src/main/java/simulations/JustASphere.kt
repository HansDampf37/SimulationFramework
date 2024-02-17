package simulations

import framework.Simulation
import framework.physics.Seconds
import framework.physics.Sphere
import kotlin.math.PI

@Suppress("unused")
class JustASphere: Simulation("Sphere") {
    private val dot = Sphere(0.0, 0.0, 0.0, 1.0, 1.0)
    init {
        reset()
    }

    override fun tick(dt: Seconds) = Unit

    override fun render() {
        dot.render(camera)
    }

    override fun reset() {
        dot.x = 0.0
        dot.y = 0.0
        dot.z = 0.0
        camera.focalLength = 10.0
        camera.x = 0.0
        camera.y = -25.0
        camera.z = 0.0
        camera.theta = PI / 2
        camera.phi = PI
        camera.focalLength = 10.0
        camera.zoom = 0.001
    }
}

fun main() {
    JustASphere().start()
}