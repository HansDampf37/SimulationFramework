package framework

import physics.Seconds

/**
 * An entity in the 3-dimensional [Simulation] that is mapped via a [Camera] onto the canvas and is drawn there
 * with the [Rasterizer].
 */
interface Entity {
    /**
     * Change the inner state of the Entity depending on the time passed since the last invocation of this method.
     */
    fun tick(dt: Seconds)

    /**
     * Renders the object with the methods provided by the [Camera] object
     * @see Camera.renderStrip
     * @see Camera.renderTriangle
     * @see Camera.renderSphere
     * @see Camera.renderLine
     */
    fun render(camera: Camera)

    /**
     * Weather or not the entity should be outlined when drawn
     */
    var outlineRasterization: Boolean
}
