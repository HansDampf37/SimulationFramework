package framework

import physics.Seconds
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints

/**
 * Simulations run by the [start] and [stop] methods. They implement a [tick] method that updates simulated objects and
 * a [render]-method that displays the objects.The [drawer] and [camera] object can be used in the render method
 * to map the three-dimensional space into the drawing plane.
 */
abstract class Simulation(
    title: String,
    private val renderingFrequency: Double = 25.0,
    private val antiAliasing: Boolean = true
) : ISimulation {

    protected var drawer: Graphics3d = Graphics3d()
    private var running = false
    protected val keyManager: KeyManager = KeyManager()
    protected val display: Display = Display(title).apply { jFrame.addKeyListener(keyManager) }
    protected var camera = Camera(
        0.0, 2.0, 0.0,
        1.0, 1.0, 1.0,
        display.getWidth(), display.getHeight()
    )
    private var zPuffer: Array<Array<Float>> = Array(width) { Array(height) { Float.MAX_VALUE } }
    private var colorPuffer: Array<Array<Color>> = Array(width) { Array(height) { Color.black } }

    /**
     * triggers [Simulation.render] on every simulation at a given frequency.
     * triggers [Simulation.tick] as often as possible
     */
    private val tickAndRender = Runnable {
        var lastTime = System.currentTimeMillis()
        val msPerTick = 1000.0 / renderingFrequency
        var delta = 0.0
        while (running) {
            val now = System.currentTimeMillis()

            // always tick
            val dt: Seconds = (now - lastTime) / 1000.0
            keyManager.tick()
            listenForInput(dt)
            drawer.setWindowHeightAndWidth(width, height)
            camera.screenWidth = width
            camera.screenHeight = height
            tick(dt)
            delta += (now - lastTime) / msPerTick

            // render to reach fps goal
            if (delta >= 1) {
                initializeRendering()
                delta--
                lastTime = now
            }
        }
        stop()
    }

    private var threadTickingAndRendering: Thread = Thread(tickAndRender)

    private fun listenForInput(dt: Seconds) {
        if (keyManager.w) drawer.moveVerticalCamera(dt)
        if (keyManager.s) drawer.moveVerticalCamera(-dt)
        if (keyManager.d) drawer.moveHorizontalCamera(dt)
        if (keyManager.a) drawer.moveHorizontalCamera(-dt)
        if (keyManager.y) drawer.zoom(1 + dt)
        if (keyManager.out) drawer.zoom(1 - dt)
        if (keyManager.n) reset()

        if (keyManager.w) camera.add(camera.lookingDirection * dt * 5.0)
        if (keyManager.s) camera.add(-camera.lookingDirection * dt * 5.0)
        if (keyManager.d) camera.add(-camera.left * dt * 5.0)
        if (keyManager.a) camera.add(camera.left * dt * 5.0)
        if (keyManager.shift) camera.add(-camera.up * dt * 5.0)
        if (keyManager.space) camera.add(camera.up * dt * 5.0)
        if (keyManager.y) camera.zoom *= 1 + dt
        if (keyManager.out) camera.zoom *= 1 - dt
        if (keyManager.n) reset()
        if (keyManager.up) camera.pitch += dt
        if (keyManager.down) camera.pitch -= dt
        if (keyManager.left) camera.yaw -= dt
        if (keyManager.right) camera.yaw += dt
        if (keyManager.f) camera.focalLength *= 1 + dt
        if (keyManager.g) camera.focalLength *= 1 - dt
    }

    /**
     * Calls [Simulation.render] with a new [Graphics] object
     */
    private fun initializeRendering() {
        val bs = display.canvas.bufferStrategy
        if (bs == null) {
            display.canvas.createBufferStrategy(3)
            return
        }
        val g = bs.drawGraphics
        g.clearRect(0, 0, display.canvas.width, display.canvas.height)
        g.color = Color.white
        g.drawString(camera.cameraSettingsToString(), 10, 10)
        if (antiAliasing) (g as Graphics2D).setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        camera.prepareForNewFrame()
        render()
        g.drawImage(camera.image, 0, 0, camera.screenWidth, camera.screenHeight, null)
        bs.show()
        g.dispose()
    }

    @Synchronized
    override fun start() {
        if (running) return
        running = true
        threadTickingAndRendering.start()
    }

    @Synchronized
    override fun stop() {
        if (!running) return
        running = false
        try {
            threadTickingAndRendering.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    protected val height: Int
        get() = display.canvas.height
    protected val width: Int
        get() = display.canvas.width
}