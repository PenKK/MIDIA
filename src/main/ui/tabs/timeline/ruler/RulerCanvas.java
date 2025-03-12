package ui.tabs.timeline.ruler;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import ui.tabs.timeline.midi.MidiTrackLabelPanel;
import ui.tabs.timeline.midi.MidiTrackPanel;

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
        g.drawLine(MidiTrackLabelPanel.LABEL_BOX_WIDTH, 0, MidiTrackLabelPanel.LABEL_BOX_WIDTH, MidiTrackPanel.HEIGHT); // place holder
    }


}
