package simulations;

import spacesimulation.Simulator;

public class Launcher {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        // new PlatonSpace(500, simulator);
        // new Net(simulator);
        new Pendulum(2, simulator);
        simulator.start();
    }
}