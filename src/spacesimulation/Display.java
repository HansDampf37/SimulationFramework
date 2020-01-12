package spacesimulation;

import javax.swing.JFrame;
import java.awt.*;
import javax.swing.*;

public class Display {

    private JFrame frame;
    private Canvas canvas;
    private int height, width;

    public Display() {
        frame = new JFrame("Title");
        height = 1000;
        width = 1600;
        frame.setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setBackground(new Color(42, 55, 71));
        frame.add(canvas);
        frame.pack();
    }

    public int getHeight() {
        if (frame == null) return 1080; 
        return frame.getHeight();
    }
    
    public int getWidth() {
        if (frame == null) return 1920; 
        return frame.getWidth();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getJFrame() {
        return frame;
    }
}