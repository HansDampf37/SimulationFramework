package spacesimulation;

import java.awt.Graphics;
import java.awt.Color;

public abstract class Simulation {
    protected Simulator simulator;
    protected KeyManager keymanager;
    protected Graphics3d drawer;

    public Simulation(Simulator sim) {
        simulator = sim;
        simulator.addSimulation(this);
        keymanager = simulator.getKeymanager();
        drawer = new Graphics3d();
    }

    public abstract void tick();

    public void parentTick() {
        listenForInput();
        drawer.setWindowHeightAndWidth(simulator.getWidth(), simulator.getHeight());
        tick();
    }

    private void listenForInput() {
        if (keymanager.w) drawer.moveVerticalCamera(1);
        if (keymanager.s) drawer.moveVerticalCamera(-1);
        if (keymanager.d) drawer.moveHorizontalCamera(1);
        if (keymanager.a) drawer.moveHorizontalCamera(-1);
        if (keymanager.y) drawer.zoom(1);
        if (keymanager.out) drawer.zoom(-1);
        if (keymanager.n) reset();
    }

    public void parentRender(Graphics g) {
        g.setColor(Color.white);
        g.drawString(drawer.cameraSettingsToString(), 10, 10);
        render(g);
    }

    public abstract void render(Graphics g);

    public abstract void reset();
}