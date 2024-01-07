package simulations;

import spacesimulation.Simulator;

public class Launcher {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        // new PlatonSpace(300, simulator);
        // new Net(simulator);
        // new PlatonSpace(5, simulator);
        new Pend(40, simulator);
        // new Pend(4, simulator);
        // new Fractal(5, simulator);
        simulator.start();
    }
}