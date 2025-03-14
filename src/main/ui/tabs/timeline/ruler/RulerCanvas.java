package ui.tabs.timeline.ruler;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import model.Timeline;
import ui.tabs.timeline.midi.MidiTrackLabelPanel;
import ui.tabs.timeline.midi.MidiTrackRenderPanel;

// The canvas (but is a JPanel) for Graphics to draw on to show Ruler ticks
public class RulerCanvas extends JPanel {

    public static final int TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.3);
    public static final int BEAT_TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.6);
    private int pixelWidthTick;
    private int divisions;
    private int beatsPerMeasure;

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    RulerCanvas() {
        this.setBorder(null);
        this.setBackground(Color.GRAY);

        divisions = 8;
        beatsPerMeasure = 4;
        pixelWidthTick = MidiTrackRenderPanel.scalePixelsRender(Timeline.PULSES_PER_QUARTER_NOTE / divisions);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(MidiTrackLabelPanel.LABEL_BOX_WIDTH, 0, 
                   MidiTrackLabelPanel.LABEL_BOX_WIDTH, RulerScrollPane.RULER_HEIGHT);
        drawAllTickMarks(g);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ticks marks of measures, beats, and divisions
    private void drawAllTickMarks(Graphics g) {
        for (int i = MidiTrackLabelPanel.LABEL_BOX_WIDTH; i < getWidth(); i += pixelWidthTick) {
            int height = TICK_HEIGHT;
            int x = (i - MidiTrackLabelPanel.LABEL_BOX_WIDTH);

            if (x % (pixelWidthTick * beatsPerMeasure * divisions) == 0) {
                height = RulerScrollPane.RULER_HEIGHT; // One measure
            } else if (x % (pixelWidthTick * divisions) == 0) {
                height = BEAT_TICK_HEIGHT; // One beat
            }

            g.drawLine(i, 0, i, height);
        }
    }

}
