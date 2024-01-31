package framework

import algebra.Vec
import framework.interfaces.Status
import physics.Mass
import physics.Seconds
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class MassSimulation<T : Mass>(
    private val title: String,
    private var frictionPerSecond: Double = .2,
    private var gravity: Vec = Vec(0.0, 0.0, -9.81)) : Simulation(title) {

    protected var masses: MutableList<T> = ArrayList()
    private var affectedByGravity = HashMap<T, Boolean>()

    fun addNewMass(mass: T) {
        synchronized(masses) { masses.add(mass) }
    }

    override fun tick(dt: Seconds) {
        calcForces(dt)
        synchronized(masses) {
            for (mass in masses) {
                mass.tick(dt)
                if (mass.status == Status.Movable) mass.accelerate(gravity)
                mass.velocity = mass.velocity.scaleInPlace((1 - frictionPerSecond * dt))
            }
        }
        correctState()
    }

    /**
     * This method is invoked after the repositioning of the masses and can be used to correct poorly calculated
     * positions.
     */
    open fun correctState() = Unit

    abstract fun calcForces(dt: Seconds)
}
