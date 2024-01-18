import simulations.Cloth;

public class Launcher {
    public static void main(String[] args) {
        // new PlatonSpace(300, simulator);
        // new Net(simulator);
        // new PlatonSpace(5, simulator);
        // new Pend(40, simulator, 10);
        new Cloth(20).start();
        // new Pend(4, simulator);
        // new Fractal(5, simulator);
    }
}