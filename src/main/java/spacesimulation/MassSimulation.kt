package spacesimulation

import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Mass
import spacesimulation.physics.Seconds
import java.awt.Graphics

abstract class MassSimulation<T : Mass>(
    private var frictionFactor: Double = 1.0,
    private var gravity: Vec = Vec(0.0, -9.81, 0.0),
    simulator: Simulator) : Simulation(simulator) {

    protected var masses: MutableList<T> = ArrayList()
    private var affectedByGravity = HashMap<T, Boolean>()

    fun addNewMass(mass: T, affectedByGravity: Boolean) {
        masses.add(mass)
        this.affectedByGravity[mass] = affectedByGravity
    }

    override fun tick(dt: Seconds) {
        calcForces(dt)
        for (mass in masses) {
            mass.tick(dt)
            if (affectedByGravity[mass]!!) mass.accelerate(gravity)
            mass.velocity = mass.velocity.scale(frictionFactor)
        }
        buffer()
    }

    abstract override fun render(g: Graphics)
    abstract fun calcForces(dt: Seconds)
    protected fun buffer() = Unit
    abstract override fun reset()
}
