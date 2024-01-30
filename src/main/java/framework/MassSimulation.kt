package framework

import algebra.Vec
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

    fun addNewMass(mass: T, affectedByGravity: Boolean) {
        synchronized(masses) { masses.add(mass) }
        this.affectedByGravity[mass] = affectedByGravity
    }

    override fun tick(dt: Seconds) {
        calcForces(dt)
        synchronized(masses) {
            for (mass in masses) {
                mass.tick(dt)
                if (affectedByGravity[mass]!!) mass.accelerate(gravity)
                mass.velocity = mass.velocity.scaleInPlace((1 - frictionPerSecond * dt))
            }
        }
        correct()
    }

    abstract fun correct()

    abstract fun calcForces(dt: Seconds)
}
