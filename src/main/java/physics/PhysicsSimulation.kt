package physics

import algebra.Vec
import framework.Simulation
import framework.WatchDouble
import framework.interfaces.*
import framework.interfaces.Collidable
import physics.collisions.Collision
import physics.collisions.CollisionManager
import randomOrder
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList

/**
 * Physics Simulations are [Simulation]s that enable [registering][register] of [tickables][Tickable], [renderables][Renderable],
 * and [collidables][Collidable]. If this classes [render]-method is called, [Renderable.render] is called on each
 * registered [Renderable]. If this classes [tick]-method is called, [Tickable.tick] is called on each registered [Tickable]
 * and Collisions are calculated between the registered [collidables].
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
    private val collidables: MutableList<Collidable> = ArrayList()

    private var collisionManager: CollisionManager = CollisionManager()

    /**
     * Registers a [Tickable], [Renderable], or [Collidable] at this Simulation.
     * @throws IllegalArgumentException if specified object is neither [Tickable], [Renderable], nor [Collidable]
     */
    fun register(obj: Any) {
        if (obj is Tickable) synchronized(tickables) { tickables.add(obj) }
        if (obj is Renderable) synchronized(renderables) { renderables.add(obj) }
        if (obj is Collidable) synchronized(collidables) { collidables.add(obj) }

        if (obj !is Tickable && obj !is Renderable && obj !is Collidable) {
            throw IllegalArgumentException("specified object is neither Tickable, Renderable, nor Collidable")
        }
    }

    /**
     * Unregisters a [Tickable], [Renderable], or [Collidable] from this Simulation.
     * @throws IllegalArgumentException if specified object is neither [Tickable], [Renderable], nor [Collidable]
     */
    fun unregister(obj: Any) {
        if (obj is Tickable) synchronized(tickables) { tickables.remove(obj) }
        if (obj is Renderable) synchronized(renderables) { renderables.remove(obj) }
        if (obj is Collidable) synchronized(collidables) { collidables.remove(obj) }

        if (obj !is Tickable && obj !is Renderable && obj !is Collidable) {
            throw IllegalArgumentException("specified object is neither Tickable, Renderable, nor Collidable")
        }
    }

    override fun reset() {
        synchronized(tickables) { tickables.clear() }
        synchronized(renderables) { renderables.clear() }
        synchronized(collidables) { collidables.clear() }
    }

    override fun tick(dt: Seconds) {
        calcForces()
        synchronized(collidables)  {
            collidables.forEach { c1 ->
                collidables.forEach { c2 ->
                    if (c1 != c2) {
                        if (testCollision(c1, c2)) {
                            Collision.occur(c1, c2, 1.0)
                            if (c1 is Sphere && c2 is Sphere) {
                                val targetDistance = c1.radius + c2.radius
                                val overlap = targetDistance - c1.getDistanceTo(c2)
                                val massMovable = c2.status == Status.Movable
                                val overlap1 = if (massMovable) c1.mass / (c2.mass + c1.mass) * overlap else 0.0
                                val overlap2 = if (massMovable) c2.mass / (c2.mass + c1.mass) * overlap else overlap
                                if ((c2.positionVector - c1.positionVector).length != 0.0) {
                                    c1.set(c1 + c2.getDirectionTo(c1) * overlap2)
                                    c2.set(c2 + c1.getDirectionTo(c2) * overlap1)
                                }
                            }
                        }
                    }
                }
            }
        }
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

    private fun testCollision(c1: Collidable, c2: Collidable): Boolean = collisionManager.testCollision(c1, c2)
}
