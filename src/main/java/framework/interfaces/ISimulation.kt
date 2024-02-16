package framework.interfaces

import framework.physics.Seconds
import framework.Camera
import framework.Graphics3d

/**
 * Simulations run by the [start] adn [stop] methods. They implement a [tick] method that updates simulated objects and
 * a [render]-method that displays the objects.The [Graphics3d] and [Camera] objects can be used in the render method
 * to map the three-dimensional space into the drawing plane.
 */
interface ISimulation {

    /**
     * Updates internal state of the Simulation.
     * @param dt amount of Seconds since the last call of tick (most of the time 0.00...)
     */
    fun tick(dt: Seconds)

    /**
     * Render all the primitives by calling the [Camera]s rendering methods on all relevant primitives.
     * @see Camera.renderSphere
     * @see Camera.renderTriangle
     * @see Camera.renderLine
     * @see Camera.renderStrip
     */
    fun render()

    /**
     * Resets the Simulation to a starting state
     */
    fun reset()

    /**
     * Starts the simulation
     */
    fun start()

    /**
     * Stops the simulation
     */
    fun stop()
}