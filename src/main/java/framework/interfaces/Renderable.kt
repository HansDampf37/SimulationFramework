package framework.interfaces

import framework.Camera

/**
 * A member of this class can be projected using its [render]-method and a [Camera] object.
 * Each renderable object is automatically [Drawable].
 */
interface Renderable: Drawable {
    /**
     * Renders the object with the methods provided by the [Camera] object
     * @see Camera.renderStrip
     * @see Camera.renderTriangle
     * @see Camera.renderSphere
     * @see Camera.renderLine
     */
    fun render(camera: Camera)
}