package physics

import algebra.Point3d
import algebra.Vec
import framework.Camera
import framework.Entity
import framework.Graphics3d
import framework.Vertex
import java.awt.Color
import java.awt.Graphics

open class Mass(mass: Double, x: Double, y: Double, z: Double): Point3d(x, y, z), Entity {
    override var outlineRasterization: Boolean = false
    override var color: Vec? = Conf.mass_color

    override var velocity: Vec = Vec(0,0,0)
    private val acceleration: Vec
    val mass: Double
    var status = Status.Movable
    override var position: Vec
        get() = positionVector
        set(value) {
            x = value.x
            y = value.y
            z = value.z
        }

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

    open fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, 0.25, Color.white, g)
    }

    override fun render(camera: Camera) {
        camera.renderSphere(Vertex(positionVector, Vec.ones * 255, Vec.zero), 0.1f, this)
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
