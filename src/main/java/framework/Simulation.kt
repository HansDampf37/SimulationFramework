package framework

import framework.display.Display
import framework.display.KeyManager
import framework.display.MouseManager
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
    @WatchDouble("Speed",0.0, 2.0)
    private var speed = 1.0
    protected var drawer: Graphics3d = Graphics3d()
    private var running = false
    protected val display: Display = Display(title)
    protected var camera = Camera(
        x = 0.0, y = 0.0, z = 0.0,
        phi = 0.0, theta = 0.0,
        zoom = 1.0, focalLength = 1.0,
        display.getWidth(), display.getHeight()
    )

    private val mouseManager = MouseManager(camera)
    private val keyManager = KeyManager

    init {
        display.window.addKeyListener(keyManager)
        display.canvas.addMouseMotionListener(mouseManager)
        display.canvas.addMouseListener(mouseManager)
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
            tick(dt * speed)
            delta += (now - lastTime) / msPerTick

            // render to reach fps goal
            if (delta >= 1) {
                keyManager.tick()
                mouseManager.tick()
                listenForInput(dt)
                initializeRendering()
                delta--
                lastTime = now
                println("running")
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

        if (keyManager.w) camera.moveForward(dt)
        if (keyManager.s) camera.moveBackward(dt)
        if (keyManager.d) camera.moveRight(dt)
        if (keyManager.a) camera.moveLeft(dt)
        if (keyManager.shift) camera.moveDown(dt)
        if (keyManager.space) camera.moveUp(dt)
        if (keyManager.y) camera.zoom *= 1 + dt
        if (keyManager.out) camera.zoom *= 1 - dt
        if (keyManager.n) reset()
        if (keyManager.up) camera.theta += dt
        if (keyManager.down) camera.theta -= dt
        if (keyManager.left) camera.phi -= dt
        if (keyManager.right) camera.phi += dt
        if (keyManager.f) camera.focalLength *= 1 + dt
        if (keyManager.g) camera.focalLength *= 1 - dt
    }

    /**
     * Clears the image and calls [Camera.newFrame] and [Simulation.render].
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
        if (antiAliasing) (g as Graphics2D).setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        )
        val canvasWidth = display.canvas.width
        val canvasHeight = display.canvas.height
        if (canvasWidth > 0 && canvasHeight > 0) {
            drawer.setWindowHeightAndWidth(canvasWidth, canvasHeight)
            camera.screenWidth = canvasWidth
            camera.screenHeight = canvasHeight
            camera.newFrame()
            render()
            g.drawImage(camera.image, 0, 0, camera.screenWidth, camera.screenHeight, null)
            g.drawString(camera.cameraSettingsToString(), 10, 10)
        }
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
}