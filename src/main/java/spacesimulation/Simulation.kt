package spacesimulation

import spacesimulation.physics.Seconds
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints

/**
 * Simulations are run in [Simulator]s. They implement a [tick] method that updates simulated objects and a [render]-method
 * that displays the objects.The [drawer] object can be used in the render method
 * to map the three-dimensional space into the drawing plane.
 */
abstract class Simulation(protected var simulator: Simulator) {
    protected var drawer: Graphics3d = Graphics3d()
    private val antiAliasing = true
    lateinit var keyManager: KeyManager

    abstract fun tick(dt: Seconds)
    fun parentTick(dt: Seconds) {
        listenForInput(dt)
        drawer.setWindowHeightAndWidth(simulator.width, simulator.height)
        tick(dt)
    }

    private fun listenForInput(dt: Seconds) {
        if (keyManager.w) drawer.moveVerticalCamera(dt)
        if (keyManager.s) drawer.moveVerticalCamera(-dt)
        if (keyManager.d) drawer.moveHorizontalCamera(dt)
        if (keyManager.a) drawer.moveHorizontalCamera(-dt)
        if (keyManager.y) drawer.zoom( 1 + dt)
        if (keyManager.out) drawer.zoom(1 - dt)
        if (keyManager.n) reset()
    }

    fun parentRender(g: Graphics) {
        g.color = Color.white
        g.drawString(drawer.cameraSettingsToString(), 10, 10)
        if (antiAliasing) (g as Graphics2D).setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        render(g)
    }

    abstract fun render(g: Graphics)
    abstract fun reset()
}