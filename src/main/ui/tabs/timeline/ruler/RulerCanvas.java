package ui.tabs.timeline.ruler;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

// The canvas (but is a JPanel) for Graphics to draw on to show Ruler ticks
public class RulerCanvas extends JPanel {

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    RulerCanvas() {
        this.setBorder(null);
        this.setBackground(Color.GRAY);
    }

    
    // MODIFIES: this
    // EFFECTS: Paints the ruler
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawRect(0, 0, 100, 100); // place holder
    }


}
