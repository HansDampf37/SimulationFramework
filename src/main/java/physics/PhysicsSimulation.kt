package physics

import algebra.Vec
import framework.Simulation
import framework.WatchDouble
import framework.interfaces.*
import framework.interfaces.Collidable
import physics.collisions.CollisionManager
import randomOrder
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

/**
 * Physics Simulations are [Simulation]s that enable [registering][register] of [tickables][Tickable], [renderables][Renderable],
 * [moveables][Moveable], and [collidables][Collidable]. If this classes [render]-method is called, [Renderable.render] is called on each
 * registered [Renderable]. If this classes [tick]-method is called, [Tickable.tick] is called on each registered [Tickable]
 * and Collisions are calculated between the registered [Collidable]s.
 * The
 */
@Suppress("KotlinConstantConditions")
abstract class PhysicsSimulation(title: String) : Simulation(title) {
    @WatchDouble("g", 0.0, 15.0)
    private var g: MetersPerSecondPerSecond = 9.81
    private val gravity get() = Vec(0.0, 0.0, -g)
    //private var frictionPerSecond: Double = 0.02
    private val tickables: MutableList<Tickable> = ArrayList()
    private val renderables: MutableList<Renderable> = ArrayList()
    private val moveables: MutableList<Moveable> = ArrayList()
    private var collisionManager: CollisionManager = CollisionManager()

    /**
     * Registers a [Tickable], [Renderable], [Moveable] or [Collidable] at this Simulation.
     * @throws IllegalArgumentException if specified object is neither [Tickable], [Renderable], [Moveable] nor [Collidable]
     */
    fun register(obj: Any) {
        if (obj is Tickable) synchronized(tickables) { tickables.add(obj) }
        if (obj is Renderable) synchronized(renderables) { renderables.add(obj) }
        if (obj is Moveable) synchronized(moveables) { moveables.add(obj) }
        if (obj is Collidable) collisionManager.register(obj)

        if (obj !is Tickable && obj !is Renderable && obj !is Collidable && obj !is Moveable) {
            throw IllegalArgumentException("specified object is neither Tickable, Renderable, Moveable, nor Collidable")
        }
    }

    /**
     * Unregisters a [Tickable], [Renderable], [Moveable] or [Collidable] from this Simulation.
     * @throws IllegalArgumentException if specified object is neither [Tickable], [Renderable], nor [Collidable]
     */
    fun unregister(obj: Any) {
        if (obj is Tickable) synchronized(tickables) { tickables.remove(obj) }
        if (obj is Renderable) synchronized(renderables) { renderables.remove(obj) }
        if (obj is Moveable) synchronized(moveables) { moveables.remove(obj) }
        if (obj is Collidable) collisionManager.unregister(obj)

        if (obj !is Tickable && obj !is Renderable && obj !is Collidable && obj !is Moveable) {
            throw IllegalArgumentException("specified object is neither Tickable, Renderable, Moveable, nor Collidable")
        }
    }

    override fun reset() {
        synchronized(tickables) { tickables.clear() }
        synchronized(renderables) { renderables.clear() }
        synchronized(moveables) { moveables.clear() }
        collisionManager.reset()
    }

    override fun tick(dt: Seconds) {
        synchronized(moveables) { moveables.forEach { it.acceleration = Vec.zero }}
        calcForces()
        collisionManager.calculateCollisions()
        synchronized(tickables) {
            val order = randomOrder(tickables.size)
            for (index in order) tickables[index].tick(dt)
        }
        correctState()
    }

    override fun render() {
        synchronized(renderables) {renderables.forEach { it.render(camera) }}
    }

    /**
     * This method is invoked after the repositioning of the masses and can be used to correct poorly calculated
     * positions.
     */
    open fun correctState() = Unit

    /**
     * Calculate the forces and accelerations on the objects in this simulation
     */
    abstract fun calcForces()

    protected fun applyGravity(masses: Iterable<Mass>) {
        for (mass in masses) {
            mass.acceleration += gravity
        }
    }
}
