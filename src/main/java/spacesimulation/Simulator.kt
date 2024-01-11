package spacesimulation

import spacesimulation.physics.Seconds
import java.awt.Graphics
import java.awt.image.BufferStrategy

/**
 * Environment in which a simulation is running.
 */
class Simulator {
    val keyManager = KeyManager()
    private var threadTickingAndRendering: Thread? = null
    private var running = false
    private val display: Display = Display().apply{ jFrame.addKeyListener(keyManager) }
    private var bs: BufferStrategy? = null
    private var g: Graphics? = null
    private val simulations: ArrayList<Simulation> = ArrayList()

    /**
     * triggers [Simulation.render] on every simulation at a given frequency.
     * triggers [Simulation.tick] as often as possible
     */
    private val tickAndRender = Runnable {
        var lastTime = System.currentTimeMillis()
        val amountOfTicks = 160.0
        val msPerTick = 1000.0 / amountOfTicks
        var delta = 0.0
        while (running) {
            val now = System.currentTimeMillis()

            // always tick
            val dt: Seconds = (now - lastTime) / 1000.0
            tick(dt)
            delta += (now - lastTime) / msPerTick

            // render to reach fps goal
            if (delta >= 1) {
                render()
                delta--
                lastTime = now
            }
        }
        stop()
    }


    /**
     * Calls [Simulation.tick] on the simulations run in this simulator
     */
    private fun tick(dt: Double) {
        for (simulation in simulations) simulation.parentTick(dt)
        keyManager.tick()
    }

    /**
     * Calls [Simulation.render] with a new [Graphics] object
     */
    private fun render() {
        bs = display.canvas.bufferStrategy
        if (bs == null) {
            display.canvas.createBufferStrategy(3)
            return
        }
        g = bs!!.drawGraphics
        g!!.clearRect(0, 0, display.canvas.width, display.canvas.height)
        for (simulation in simulations) simulation.parentRender(g!!)
        bs!!.show()
        g!!.dispose()
    }

    /**
     * Registers a new [Simulation] in the Simulator
     * @param simulation the new Simulation
     */
    fun addSimulation(simulation: Simulation) {
        simulations.add(simulation)
        simulation.keyManager = keyManager
    }

    @Synchronized
    fun start() {
        if (running) return
        running = true
        threadTickingAndRendering = Thread(tickAndRender).apply {start()}
    }

    @Synchronized
    fun stop() {
        if (!running) return
        running = false
        try {
            threadTickingAndRendering?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    val height: Int
        get() = display.canvas.height
    val width: Int
        get() = display.canvas.width
}