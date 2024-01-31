package framework.interfaces

import framework.Camera

/**
 * A member of this class can draw itself using its [render]-method and a [Camera] object.
 */
interface Renderable {
    /**
     * Renders the object with the methods provided by the [Camera] object
     * @see Camera.renderStrip
     * @see Camera.renderTriangle
     * @see Camera.renderSphere
     * @see Camera.renderLine
     */
    fun render(camera: Camera)
}