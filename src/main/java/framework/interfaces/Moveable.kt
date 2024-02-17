package framework.interfaces

import algebra.Vec3
import framework.physics.Seconds

/**
 * An entity in the 3-dimensional [ISimulation] has a [position], a [velocity], and a [acceleration].
 * It can be updated using its [tick] method.
 */
interface Moveable : Tickable {

    /**
     * The entities anchor position
     */
    var position: Vec3

    /**
     * The velocity as difference in position per second
     */
    var velocity: Vec3

    /**
     * The acceleration as difference in velocity per second
     */
    var acceleration: Vec3

    /**
     * Status making it possible to turn of
     */
    var status: Status

    /**
     * Changes the velocity by [acceleration] * [dt]
     * @param dt time since last invocation of this method in Seconds
     */
    fun accelerate(dt: Seconds) {
        if (status == Status.Movable) velocity += acceleration * dt
    }

    /**
     * Changes the position by [velocity] * [dt]
     * @param dt time since last invocation of this method in Seconds
     */
    fun move(dt: Seconds) {
        if (status == Status.Movable) position += velocity * dt
    }

    /**
     * Updates velocity and position
     */
    override fun tick(dt: Seconds) {
        accelerate(dt)
        move(dt)
    }
}

enum class Status {
    Immovable,
    Movable
}
