package framework

import Conf
import framework.display.Display
import framework.display.KeyManager
import framework.display.MouseManager
import framework.interfaces.ISimulation
import framework.physics.Seconds
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.ConvolveOp
import java.awt.image.Kernel


/**
 * Simulations run by the [start] and [stop] methods. They implement a [tick] method that updates simulated objects and
 * a [render]-method that displays the objects.The [camera] object can be used in the render method
 * to map the three-dimensional space into the drawing plane.
 */
abstract class Simulation(title: String, private val renderingFrequency: Double = 25.0) : ISimulation {
    @WatchDouble("Speed",0.0, 2.0)
    private var speed = 1.0
    @WatchBoolean("Anti-Aliasing")
    private var antiAliasing = true
    private var running = false
    protected val keyManager = KeyManager()
    protected val display: Display = Display(title, keyManager = keyManager)

    protected var camera = Camera(
        x = 0.0, y = 0.0, z = 0.0,
        phi = 0.0, theta = 0.0,
        zoom = 1.0, focalLength = 1.0,
        display.getWidth(), display.getHeight()
    )
    private val mouseManager = MouseManager(camera)

    init {
        display.window.addKeyListener(keyManager)
        display.canvas.addMouseMotionListener(mouseManager)
        display.canvas.addMouseListener(mouseManager)
    }

    /**
     * triggers [Simulation.render] on every simulation at a given frequency.
     * triggers [Simulation.tick] as often as possible
     */
    private val tickJob = Runnable {
        var lastTime = System.currentTimeMillis()
        while (running) {
            val now = System.currentTimeMillis()
            val dt: Seconds = (now - lastTime) / 1000.0
            tick(dt * speed)
            lastTime = now
        }
        stop()
    }

    private val renderJob = Runnable {
        var lastTime = System.currentTimeMillis()
        val msPerTick = 1000.0 / renderingFrequency
        var delta = 0.0
        while (running) {
            val now = System.currentTimeMillis()

            // always tick
            val dt: Seconds = (now - lastTime) / 1000.0
            delta += (now - lastTime) / msPerTick
            // render to reach fps goal
            if (delta >= 1) {
                keyManager.tick()
                mouseManager.tick(dt)
                listenForInput(dt)
                initializeRendering()
                delta--
                lastTime = now
                //println("running")
            }
        }
        stop()
    }

    private var tickingThread: Thread = Thread(tickJob)
    private var renderingThread: Thread = Thread(renderJob)

    /**
     * Updates the internal state of the simulation based on the [KeyManagers][KeyManager] inputs. Can be overwritten
     * for custom control over the entities in a simulation.
     */
    protected open fun listenForInput(dt: Seconds) {
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
        g.color = Color.white
        val canvasWidth = display.canvas.width
        val canvasHeight = display.canvas.height
        if (canvasWidth > 0 && canvasHeight > 0) {
            camera.screenWidth = canvasWidth
            camera.screenHeight = canvasHeight
            camera.newFrame()
            render()
            val image = if (antiAliasing) applyAntiAliasing(camera.image) else camera.image
            g.drawImage(image, 0, 0, camera.screenWidth, camera.screenHeight, null)
            g.drawString(camera.cameraSettingsToString(), 10, 10)
        }
        bs.show()
        g.dispose()
    }

    private fun applyAntiAliasing(image: BufferedImage): BufferedImage {
        val kernelSize = 2
        val kernel = FloatArray(kernelSize * kernelSize) { 1.0f / (kernelSize * kernelSize) }
        val op = ConvolveOp(Kernel(kernelSize, kernelSize, kernel), ConvolveOp.EDGE_NO_OP, null)
        return op.filter(image, null)
    }

    @Synchronized
    override fun start() {
        if (running) return
        running = true
        val watchedFields = collectWatchedFields(listOf(this, camera, Conf))
        display.setWatchedFields(watchedFields)
        tickingThread.start()
        renderingThread.start()
    }

    @Synchronized
    override fun stop() {
        if (!running) return
        running = false
        try {
            tickingThread.join()
            renderingThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}