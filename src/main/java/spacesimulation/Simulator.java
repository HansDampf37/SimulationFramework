package spacesimulation;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.security.Key;
import java.util.ArrayList;

public class Simulator implements Runnable {
    private Thread thread;
    private boolean running;
    private final Display display;
    private BufferStrategy bs;
    private Graphics g;
    private final KeyManager keymanager = new KeyManager();
    private final ArrayList<Simulation> simulations;

    public Simulator() {
        display = new Display();
        display.getJFrame().addKeyListener(keymanager);
        simulations = new ArrayList<>();
    }

    private void tick(double dt) {
        for (Simulation simulation : simulations) simulation.parentTick(dt);
        keymanager.tick();
    }

    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, display.getCanvas().getWidth(), display.getCanvas().getHeight());
        for (Simulation simulation : simulations) simulation.parentRender(g);
        bs.show();
        g.dispose();
    }

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
        simulation.keyManager = keymanager;
    }

    @Override
    public void run() {
        init();
        long lastTime = System.currentTimeMillis();
        final double amountOfTicks = 160;
        double msPerTick = 1000.0 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.currentTimeMillis();
            delta += (now - lastTime) / msPerTick;
            if (delta >= 1) {
                tick((now - lastTime) / 1000.0);
                render();
                delta--;
                lastTime = now;
            }
        }
        stop();
    }

    private void init() {
        
    }

    public synchronized void start() {
        if(running) return;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if(!running) return;
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCenterX() {
        return display.getCanvas().getWidth() / 2;
    }

    public int getCenterY() {
        return display.getCanvas().getHeight() / 2;
    }

    public int getHeight() {
        return display.getCanvas().getHeight();
    }

    public int getWidth() {
        return display.getCanvas().getWidth();
    }

    public KeyManager getKeymanager() {
        return keymanager;
    }
}