package spacesimulation;

import java.awt.*;

/**
 * An entity can be drawn and moved
 */
public interface Entity {

    public void tick();

    public void render(Graphics3d drawer, Graphics g);
}