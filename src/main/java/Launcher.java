import simulations.Cloth;
import framework.Simulator;

public class Launcher {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        // new PlatonSpace(300, simulator);
        // new Net(simulator);
        // new PlatonSpace(5, simulator);
        // new Pend(40, simulator, 10);
        simulator.addSimulation(new Cloth(simulator, 20));
        // new Pend(4, simulator);
        // new Fractal(5, simulator);
        simulator.start();
    }
}