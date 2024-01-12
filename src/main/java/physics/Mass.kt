package physics

import framework.Camera
import framework.Simulateable
import framework.Graphics3d
import algebra.Point3d
import algebra.Vec
import java.awt.Color
import java.awt.Graphics

open class Mass(mass: Double, x: Double, y: Double, z: Double): Point3d(x, y, z), Simulateable {
    var velocity: Vec
    private val acceleration: Vec
    val mass: Double
    var status = Status.Movable

    init {
        require(mass != 0.0) { "Mass can't be equal to 0" }
        velocity = Vec(0.0, 0.0, 0.0)
        acceleration = Vec(0.0, 0.0, 0.0)
        this.mass = mass
    }

    constructor(mass: Double, pos: Point3d) : this(mass, pos.x, pos.y, pos.z)
    constructor(mass: Double, positionVector: Vec) : this(mass, positionVector.x, positionVector.y, positionVector.z)

    override fun tick(dt: Seconds) {
        accelerate(dt)
        move(dt)
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, 0.25, Color.white, g)
    }

    open fun render(cam: Camera, g: Graphics) {
        val coords = cam.project(this.positionVector)
        g.color = Color.WHITE
        val radius = 0.25
        g.fillOval(
            (coords.first - radius / cam.zoomX).toInt(),
            (coords.second - radius / cam.zoomY).toInt(),
            (2 * radius / cam.zoomX).toInt(),
            (2 * radius / cam.zoomY).toInt())
    }

    private fun accelerate(dt: Seconds) {
        velocity += acceleration * dt
        acceleration.setToZero()
    }

    private fun move(dt: Seconds) {
        add(velocity * dt)
    }

    fun applyForce(force: Vec) {
        acceleration.addInPlace(force / mass)
    }

    fun accelerate(acceleration: Vec) {
        this.acceleration.addInPlace(acceleration)
    }

    fun removeAccelerationInDirection(direction: Vec) {
        acceleration.subInPlace(acceleration.projectOnto(direction))
    }

    val impulse: Vec
        get() = velocity * mass


    enum class Status {
        Immovable,
        Movable
    }
}
