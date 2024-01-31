package framework.interfaces

import physics.Seconds

/**
 * A member of this class updates its inner state regularly when [tick] is called.
 */
interface Tickable {
    /**
     * Change the inner state of the Entity depending on the time passed since the last invocation of this method.
     * @param dt Seconds passed since the last invocation of this method
     */
    fun tick(dt: Seconds)
}