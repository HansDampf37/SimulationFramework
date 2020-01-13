package spacesimulation;

import java.awt.event.*;

public class KeyManager implements KeyListener {

    private boolean pressed[] = new boolean[1000];
    public boolean w, s, a, d, y, n, out, space, f, g, v, b, up, down, left, right = false;

    public void tick() {
        w = pressed[KeyEvent.VK_W];
        s = pressed[KeyEvent.VK_S];
        a = pressed[KeyEvent.VK_A];
        d = pressed[KeyEvent.VK_D];
        y = pressed[KeyEvent.VK_Y];
        n = pressed[KeyEvent.VK_N];
        out = pressed[KeyEvent.VK_X];
        space = pressed[KeyEvent.VK_SPACE];
        f = pressed[KeyEvent.VK_F];
        g = pressed[KeyEvent.VK_G];
        v = pressed[KeyEvent.VK_V];
        b = pressed[KeyEvent.VK_B];
        up = pressed[KeyEvent.VK_UP];
        down = pressed[KeyEvent.VK_DOWN];
        left = pressed[KeyEvent.VK_LEFT];
        right = pressed[KeyEvent.VK_RIGHT];
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressed[e.getKeyCode()] = true;
        
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        pressed[e.getKeyCode()] = false;
    }

}