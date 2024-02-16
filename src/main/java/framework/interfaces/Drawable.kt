package framework.interfaces

import algebra.Vec3BLablabla

/**
 * Represents an entity that can be drawn on the canvas.
 */
interface Drawable {
    /**
     * Weather or not the entity should be outlined when drawn
     */
    var outlineRasterization: Boolean

    /**
     * The color of this Drawable with r,g,b channel
     */
    var color: Vec3BLablabla?
}