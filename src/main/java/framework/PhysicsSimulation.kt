package framework

import algebra.Vec
import framework.interfaces.Entity
import framework.interfaces.Status
import physics.ImpulseConnection
import physics.Mass
import physics.MetersPerSecondPerSecond
import physics.Seconds
import physics.collisions.Collidable
import physics.collisions.CollisionManager
import kotlin.collections.ArrayList

abstract class PhysicsSimulation(title: String) : Simulation(title) {
    //private var frictionPerSecond: Double = 0.02
    @WatchDouble("g", 0.0, 15.0)
    private var g: MetersPerSecondPerSecond = 9.81
    private val gravity: Vec get() = Vec(0.0, 0.0, -g)
    protected val entities: MutableList<Entity> = ArrayList()
    protected val collidables: List<Collidable> get() = entities.filterIsInstance(Collidable::class.java)
    protected val masses: List<Mass> get() = entities.filterIsInstance(Mass::class.java)
    private var collisionManager: CollisionManager = CollisionManager()
    protected val connections: MutableList<ImpulseConnection> = ArrayList()

    fun addEntity(entity: Entity) {
        synchronized(entities) { entities.add(entity) }
    }

    override fun tick(dt: Seconds) {
        calcForces(dt)
        synchronized(entities) {
            for (entity in entities) {
                if (entity.status == Status.Movable) {
                    entity.acceleration += gravity * dt
                    //entity.velocity = entity.velocity.scaleInPlace((1 - frictionPerSecond * dt))
                    entity.tick(dt)
                    entity.acceleration.setToZero()
                }
            }
        }
        correctState()
    }

    /**
     * This method is invoked after the repositioning of the masses and can be used to correct poorly calculated
     * positions.
     */
    open fun correctState() = Unit

    /**
     * Calculate the forces and accelerations on the [entities] in this simulation
     */
    abstract fun calcForces(dt: Seconds)

    fun testCollision(c1: Collidable, c2: Collidable): Boolean = collisionManager.testCollision(c1, c2)
}
