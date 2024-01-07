package spacesimulation;

import java.awt.*;

public abstract class Simulation {
    protected Simulator simulator;
    protected KeyManager keymanager;
    protected Graphics3d drawer;
    private final boolean ANTI_ALIASING = true;

    public Simulation(Simulator sim) {
        simulator = sim;
        simulator.addSimulation(this);
        keymanager = simulator.getKeymanager();
        drawer = new Graphics3d();

    }

    public abstract void tick(double dtInSec);

    public void parentTick(double dtInSec) {
        listenForInput();
        drawer.setWindowHeightAndWidth(simulator.getWidth(), simulator.getHeight());
        tick(dtInSec);
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
        if (ANTI_ALIASING) ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        render(g);
    }

    public abstract void render(Graphics g);

    public abstract void reset();
}