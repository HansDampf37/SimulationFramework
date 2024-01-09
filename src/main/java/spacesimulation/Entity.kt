package spacesimulation

import spacesimulation.physics.Seconds
import java.awt.Graphics

/**
 * An entity can be drawn and moved
 */
interface Entity {
    fun tick(dt: Seconds)
    fun render(drawer: Graphics3d, g: Graphics)
}
