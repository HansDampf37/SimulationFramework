package spacesimulation;

import java.awt.*;
import spacesimulation.Display;
import java.awt.image.BufferStrategy;

public class Simulator implements Runnable {
    private Thread thread;
    private boolean running;
    private Display display;
    private BufferStrategy bs;
    private Graphics g;
    private KeyManager keymanager;
    private Simulation simulation;

    public Simulator() {
        display = new Display();
        keymanager = new KeyManager();
        display.getJFrame().addKeyListener(keymanager);
    }

    private void tick() {
        if (simulation != null) {
            simulation.parentTick();
            keymanager.tick();
        }
    }

    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null) {
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, display.getCanvas().getWidth(), display.getCanvas().getHeight());
        if (simulation != null) simulation.parentRender(g);
        bs.show();
        g.dispose();
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public void run() {
        init();
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60;
        double nsPerTick = 1000000000.0 / amountOfTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            if (delta >= 1) {
                tick();
                render();
                delta--;
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