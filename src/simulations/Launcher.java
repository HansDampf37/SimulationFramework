package simulations;

import spacesimulation.Simulator;

public class Launcher {
    public static void main(String[] args) {
        Simulator simulator = new Simulator();
        // new PlatonSpace(500, simulator);
        // new Net(simulator);
        new Fractal(5, simulator);
        // new Pendulum(2, simulator);
        simulator.start();
        // Vec v = new Vec(1, -1, 0);
        // System.out.println(v.hasSharpAngleTo(new Vec(0.01, -0.01, 10)));
    }
}