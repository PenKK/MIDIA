package ui.ruler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import model.Player;
import model.Timeline;
import model.TimelineController;
import ui.tabs.timeline.midi.TrackLabelPanel;

// The panel for Graphics to draw on to show Ruler tick marks
public class RulerRenderPanel extends JPanel implements PropertyChangeListener {

    public static final int TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.3);
    public static final int BEAT_TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.6);
    private static final Color TICK_COLOR = new Color(200, 200, 200);
    private static final int FONT_PADDING = 4;
    private static final Font MEASURE_FONT = new Font("Dialog", Font.PLAIN, 14);

    private TimelineController timelineController;

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    RulerRenderPanel(TimelineController timelineController) {
        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController);
        this.timelineController = timelineController;
        this.setBorder(null);
        this.setBackground(Color.GRAY);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        timelineController.addObserver(this);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ticks marks of measures, beats, and divisions, according to timeline instance
    private void drawAllTickMarks(Graphics g) {
        Timeline timeline = timelineController.getTimeline();

        int beatDivisions = timeline.getBeatDivision();
        int beatsPerMeasure = timeline.getBeatsPerMeasure();

        g.setColor(TICK_COLOR);
        g.setFont(MEASURE_FONT);

        long divisionTickInterval = Player.PULSES_PER_QUARTER_NOTE / beatDivisions;
        long measureTickInterval = (long) Player.PULSES_PER_QUARTER_NOTE * beatsPerMeasure;
        long beatTickInterval = Player.PULSES_PER_QUARTER_NOTE;

        int startOffset = TrackLabelPanel.LABEL_BOX_WIDTH;
        long tick = (startOffset / divisionTickInterval) * divisionTickInterval;

        while (tick <= getWidth() / timeline.getPixelsPerTick()) {
            int height = TICK_HEIGHT;

            int pixelPosition = (int) (timeline.scaleTickToPixel(tick) + startOffset);

            if (tick % measureTickInterval == 0) { // One measure
                height = RulerScrollPane.RULER_HEIGHT;
                long measure = (tick / measureTickInterval) + 1;
                g.drawString(String.valueOf(measure).concat(".1"), pixelPosition + FONT_PADDING, height - FONT_PADDING);
            } else if (tick % beatTickInterval == 0) { // One beat
                height = BEAT_TICK_HEIGHT;
            }

            g.drawLine(pixelPosition, 0, pixelPosition, height);
            tick += divisionTickInterval;
        }
    }

    // EFFECTS: listens for property change events and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "beatDivsion":
            case "beatsPerMeasure":
            case "timelineReplaced":
            case "horizontalScaleFactor":
                repaint();
                break;
        }
    }

}
