package framework

import physics.Seconds
import java.awt.Graphics

/**
 * An entity can be drawn and moved
 */
interface Simulateable {
    fun tick(dt: Seconds)
    fun render(drawer: Graphics3d, g: Graphics)
}
