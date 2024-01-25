package framework

import framework.display.Display
import framework.display.KeyManager
import physics.Seconds
import java.awt.*

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
    @WatchDouble("Speed",0.5, 2.0)
    private var speed = 1.0
    protected var drawer: Graphics3d = Graphics3d()
    private var running = false
    protected val display: Display = Display(title)
    protected var camera = Camera(
        0.0, 2.0, 0.0,
        1.0, 1.0, 1.0,
        display.getWidth(), display.getHeight()
    )

    init {
        display.window.addKeyListener(KeyManager)
    }

    /**
     * triggers [Simulation.render] on every simulation at a given frequency.
     * triggers [Simulation.tick] as often as possible
     */
    private val tickAndRender = Runnable {
        val watchedFields = collectWatchedFields(listOf(this, camera))
        display.setWatchedFields(watchedFields)
        var lastTime = System.currentTimeMillis()
        val msPerTick = 1000.0 / renderingFrequency
        var delta = 0.0
        while (running) {
            val now = System.currentTimeMillis()

            // always tick
            val dt: Seconds = (now - lastTime) / 1000.0
            KeyManager.tick()
            listenForInput(dt)
            drawer.setWindowHeightAndWidth(width, height)
            camera.screenWidth = width
            camera.screenHeight = height
            tick(dt * speed)
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
        if (KeyManager.w) drawer.moveVerticalCamera(dt)
        if (KeyManager.s) drawer.moveVerticalCamera(-dt)
        if (KeyManager.d) drawer.moveHorizontalCamera(dt)
        if (KeyManager.a) drawer.moveHorizontalCamera(-dt)
        if (KeyManager.y) drawer.zoom(1 + dt)
        if (KeyManager.out) drawer.zoom(1 - dt)
        if (KeyManager.n) reset()

        if (KeyManager.w) camera.add(camera.lookingDirection * dt * 5.0)
        if (KeyManager.s) camera.add(-camera.lookingDirection * dt * 5.0)
        if (KeyManager.d) camera.add(-camera.left * dt * 5.0)
        if (KeyManager.a) camera.add(camera.left * dt * 5.0)
        if (KeyManager.shift) camera.add(-camera.up * dt * 5.0)
        if (KeyManager.space) camera.add(camera.up * dt * 5.0)
        if (KeyManager.y) camera.zoom *= 1 + dt
        if (KeyManager.out) camera.zoom *= 1 - dt
        if (KeyManager.n) reset()
        if (KeyManager.up) camera.theta += dt
        if (KeyManager.down) camera.theta -= dt
        if (KeyManager.left) camera.phi -= dt
        if (KeyManager.right) camera.phi += dt
        if (KeyManager.f) camera.focalLength *= 1 + dt
        if (KeyManager.g) camera.focalLength *= 1 - dt
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

    private val height: Int
        get() = display.canvas.height
    private val width: Int
        get() = display.canvas.width

}