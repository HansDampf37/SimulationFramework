package spacesimulation

import spacesimulation.physics.Seconds
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints

abstract class Simulation(protected var simulator: Simulator) {
    protected var drawer: Graphics3d = Graphics3d()
    private val antiAliasing = true
    lateinit var keyManager: KeyManager

    abstract fun tick(dt: Seconds)
    fun parentTick(dt: Seconds) {
        listenForInput()
        drawer.setWindowHeightAndWidth(simulator.width, simulator.height)
        tick(dt)
    }

    private fun listenForInput() {
        if (keyManager.w) drawer.moveVerticalCamera(1)
        if (keyManager.s) drawer.moveVerticalCamera(-1)
        if (keyManager.d) drawer.moveHorizontalCamera(1)
        if (keyManager.a) drawer.moveHorizontalCamera(-1)
        if (keyManager.y) drawer.zoom(1)
        if (keyManager.out) drawer.zoom(-1)
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