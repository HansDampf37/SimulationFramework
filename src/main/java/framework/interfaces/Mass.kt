package framework.interfaces

import algebra.Vec
import framework.physics.Kg

/**
 * An [Entity] with mass.
 */
interface Mass: Entity {
    val mass: Kg

    fun applyForce(force: Vec) {
        if (status == Status.Movable) acceleration.addInPlace(force / mass)
    }
}
