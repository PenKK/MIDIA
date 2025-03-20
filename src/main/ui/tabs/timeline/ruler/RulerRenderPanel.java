package ui.tabs.timeline.ruler;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import model.Timeline;
import ui.tabs.timeline.midi.MidiTrackLabelPanel;
import ui.tabs.timeline.midi.MidiTrackRenderPanel;

// The canvas (but is a JPanel) for Graphics to draw on to show Ruler ticks
public class RulerRenderPanel extends JPanel implements PropertyChangeListener {

    public static final int TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.3);
    public static final int BEAT_TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.6);
    private int tickPixelWidth;
    private int beatDivisions;
    private int beatsPerMeasure;

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    RulerRenderPanel() {
        this.setBorder(null);
        this.setBackground(Color.GRAY);
        Timeline.addObserver(this);
        updateMeasurements();
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g);
    }

    // MODIFIES: this
    // EFFECTS: updates measurement values to the current timeline instance
    private void updateMeasurements() {
        Timeline timeline = Timeline.getInstance();
        beatDivisions = timeline.getBeatDivision();
        beatsPerMeasure = timeline.getBeatsPerMeasure();
        tickPixelWidth = MidiTrackRenderPanel.scalePixelsRender(Timeline.PULSES_PER_QUARTER_NOTE / beatDivisions);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ticks marks of measures, beats, and divisions, according to timeline instance
    private void drawAllTickMarks(Graphics g) {
        updateMeasurements();
        for (int i = MidiTrackLabelPanel.LABEL_BOX_WIDTH; i < getWidth(); i += tickPixelWidth) {
            int height = TICK_HEIGHT;
            int beatPixelWidth = tickPixelWidth * beatDivisions;
            int x = i - MidiTrackLabelPanel.LABEL_BOX_WIDTH;

            if (x % (beatPixelWidth * beatsPerMeasure) == 0) {
                height = RulerScrollPane.RULER_HEIGHT; // One measure
            } else if (x % beatPixelWidth == 0) {
                height = BEAT_TICK_HEIGHT; // One beat
            }

            g.drawLine(i, 0, i, height);
        }
    }

    // EFFECTS: listens for property change events and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "beatDivsion":
            case "beatsPerMeasure":
            case "timeline":
                drawAllTickMarks(getGraphics());
                break;
            default:
                break;
        }
    }

}
