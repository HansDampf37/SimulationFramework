package physics

import algebra.Vec
import framework.Simulation
import framework.WatchDouble
import framework.interfaces.*
import framework.interfaces.Collidable
import physics.collisions.Collision
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
