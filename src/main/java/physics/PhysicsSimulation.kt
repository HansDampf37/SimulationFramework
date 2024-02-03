package physics

import algebra.Vec
import framework.Simulation
import framework.WatchDouble
import framework.interfaces.*
import physics.collisions.Collidable
import physics.collisions.CollisionManager
import randomOrder
import kotlin.collections.ArrayList

abstract class PhysicsSimulation(title: String) : Simulation(title) {
    //private var frictionPerSecond: Double = 0.02
    @WatchDouble("g", 0.0, 15.0)
    private var g: MetersPerSecondPerSecond = 9.81
    private val gravity: Vec get() = Vec(0.0, 0.0, -g)
    protected val entities = ArrayList<Entity>()
    protected val moveables: MutableList<Moveable> = ArrayList()
    protected val tickables: MutableList<Tickable> = ArrayList()
    protected val renderables: MutableList<Renderable> = ArrayList()
    protected val collidables: MutableList<Collidable> = ArrayList()
    protected val masses: MutableList<Mass> = ArrayList()
    protected val connections: MutableList<ImpulseConnection> = ArrayList()

    private var collisionManager: CollisionManager = CollisionManager()

    fun add(el: Any) {
        if (el is Entity) synchronized(entities) { entities.add(el) }
        if (el is Moveable) synchronized(moveables) { moveables.add(el) }
        if (el is Tickable) synchronized(tickables) { tickables.add(el) }
        if (el is Renderable) synchronized(renderables) { renderables.add(el) }
        if (el is Collidable) synchronized(collidables) { collidables.add(el) }
        if (el is Mass) synchronized(masses) { masses.add(el) }
    }

    override fun reset() {
        synchronized(entities) { entities.clear() }
        synchronized(moveables) { moveables.clear() }
        synchronized(tickables) { tickables.clear() }
        synchronized(renderables) { renderables.clear() }
        synchronized(collidables) { collidables.clear() }
        synchronized(masses) { masses.clear() }
    }

    override fun tick(dt: Seconds) {
        calcForces(dt)
        synchronized(moveables) {
            for (entity in moveables) {
                if (entity.status == Status.Movable) {
                    entity.acceleration += gravity * dt
                    //entity.velocity = entity.velocity.scaleInPlace((1 - frictionPerSecond * dt))
                    //entity.acceleration.setToZero()
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
     * Calculate the forces and accelerations on the [moveables] in this simulation
     */
    abstract fun calcForces(dt: Seconds)

    fun testCollision(c1: Collidable, c2: Collidable): Boolean = collisionManager.testCollision(c1, c2)
}
