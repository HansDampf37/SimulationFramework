package framework.interfaces

import physics.PhysicsSimulation

/**
 * An Entity can be simulated, [rendered][Renderable], and [moved][Moveable].
 * Implementing classes can be added to a [PhysicsSimulation].
 */
interface Entity: Moveable, Renderable