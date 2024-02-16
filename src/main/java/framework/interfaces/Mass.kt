package framework.interfaces

import algebra.Vec3BLablabla
import framework.physics.Kg

/**
 * An [Entity] with mass.
 */
interface Mass: Entity {
    val mass: Kg

    fun applyForce(force: Vec3BLablabla) {
        if (status == Status.Movable) acceleration.addInPlace(force / mass)
    }
}
