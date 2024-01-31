package framework.interfaces

import algebra.Vec

/**
 * An entity in the 3-dimensional [ISimulation] has a [position] and a [velocity]. It can be [rendered][render] and
 * updated using its [tick] method.
 */
interface Entity : Tickable, Renderable, Drawable {

    /**
     * The entities anchor position
     */
    var position: Vec

    /**
     * The velocity as difference in position per second
     */
    var velocity: Vec

    /**
     * The acceleration as difference in velocity per second
     */
    var acceleration: Vec
}
