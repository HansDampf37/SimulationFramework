package spacesimulation

import java.awt.Graphics

/**
 * An entity can be drawn and moved
 */
interface Entity {
    fun tick(dtInSec: Double)
    fun render(drawer: Graphics3d, g: Graphics)
}
