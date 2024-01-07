package spacesimulation.physics

import spacesimulation.Entity
import spacesimulation.Graphics3d
import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import java.awt.Color
import java.awt.Graphics

class Mass(mass: Double, x: Double, y: Double, z: Double) : Point3d(x, y, z), Entity {
    var velocity: Vec
    private val currentForce: Vec
    val mass: Double
    var status = Status.Movable

    init {
        require(mass != 0.0) { "Mass can't be equal to 0" }
        velocity = Vec(0.0, 0.0, 0.0)
        currentForce = Vec(0.0, 0.0, 0.0)
        this.mass = mass
    }

    constructor(mass: Double, pos: Point3d) : this(mass, pos.x, pos.y, pos.z)
    constructor(mass: Double, positionVector: Vec) : this(mass, positionVector.x, positionVector.y, positionVector.z)

    override fun tick(dtInSec: Double) {
        accelerate(dtInSec)
        move(dtInSec)
    }

    override fun render(drawer: Graphics3d, g: Graphics) {
        drawer.drawDot(this, 4, Color.white, g)
    }

    private fun accelerate(dtInSec: Double) {
        velocity.add(Vec.scale(currentForce, dtInSec))
        currentForce.scale(0.0)
    }

    private fun move(dtInSec: Double) {
        add(Vec.scale(velocity, dtInSec))
    }

    fun applyForce(force: Vec) {
        currentForce.add(Vec.scale(force, 1.0 / mass))
    }

    fun accelerate(acceleration: Vec) {
        currentForce.add(acceleration)
    }

    fun removeAccelerationInDirection(direction: Vec) {
        currentForce.sub(currentForce.linearProjection(direction))
    }

    val impulse: Vec
        get() = Vec.scale(velocity, mass)


    enum class Status {
        Immovable,
        Movable
    }
}
