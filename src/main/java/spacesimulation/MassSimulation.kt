package spacesimulation

import spacesimulation.algebra.Point3d
import spacesimulation.algebra.Vec
import spacesimulation.physics.Mass
import java.awt.Graphics

abstract class MassSimulation(
    private var frictionFactor: Double = 0.95,
    private var gravity: Vec = Vec(0.0, -9.81, 0.0),
    simulator: Simulator) : Simulation(simulator) {

    protected var masses: MutableList<Mass> = ArrayList()
    private var affectedByGravity = HashMap<Mass, Boolean>()

    fun addNewMass(mass: Double, pos: Point3d?, affectedByGravity: Boolean) {
        val newMass = Mass(mass, pos!!)
        masses.add(newMass)
        this.affectedByGravity[newMass] = affectedByGravity
    }

    fun addNewMass(pos: Point3d?, affectedByGravity: Boolean) {
        addNewMass(1.0, pos, affectedByGravity)
    }

    override fun tick(dtInSec: Double) {
        calcForces(dtInSec)
        for (mass in masses) {
            mass.tick(dtInSec)
            if (affectedByGravity[mass]!!) mass.accelerate(gravity)
            mass.velocity = mass.velocity.scale(frictionFactor * dtInSec)
        }
        buffer()
    }

    abstract override fun render(g: Graphics)
    abstract fun calcForces(dtInSec: Double)
    abstract fun buffer()
    abstract override fun reset()
}
