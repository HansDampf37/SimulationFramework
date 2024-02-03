package framework.interfaces

import physics.collisions.Collidable
import physics.PhysicsSimulation
import physics.Mass

/**
 * Implementing classes can be added to a [PhysicsSimulation]. These classes typically implement a subset of the
 * following interfaces: [Renderable], [Tickable], [Moveable], [Collidable] and [Mass]. The method [addToSimulation] is
 * responsible for registering this instance at the simulations data structures [PhysicsSimulation.collidables],
 * [PhysicsSimulation.renderables], [PhysicsSimulation.tickables], [PhysicsSimulation.connections].
 */
interface Entity: Moveable, Renderable {
    /**
     * The method [addToSimulation] is responsible
     * for registering the entity at the simulations data structures (forExample in the case of a [PhysicsSimulation] at
     * [PhysicsSimulation.collidables], [PhysicsSimulation.renderables], [PhysicsSimulation.tickables],
     * [PhysicsSimulation.connections]).
     */
    //fun addToSimulation(sim: ISimulation)

}