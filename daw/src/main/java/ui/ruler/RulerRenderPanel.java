package ui.ruler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import model.Player;
import model.Timeline;
import model.TimelineController;

/**
 * Base panel responsible for rendering the timeline ruler (measures, beats, divisions).
 */
public abstract class RulerRenderPanel extends JPanel {

    public static final int TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.3);
    public static final int BEAT_TICK_HEIGHT = (int) Math.round(RulerScrollPane.RULER_HEIGHT * 0.6);

    private static final Color DIVISION_TICK_COLOR = Color.decode("#dedede");
    private static final Color BEAT_COLOR = DIVISION_TICK_COLOR;
    private static final Color MEASURE_COLOR = Color.WHITE;

    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font MEASURE_FONT = new Font("Dialog", Font.PLAIN, 14);

    private static final int FONT_PADDING = 4;

    public RulerRenderPanel() {
        super();
        this.setBorder(null);
        this.setBackground(Color.GRAY);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
    }

    protected void addMouseAdapter(RulerMouseAdapter adapter) {
        this.addMouseListener(adapter);
        this.addMouseMotionListener(adapter);
    }

    /**
     * Draws tick marks for measures, beats, and beat divisions across the given width.
     *
     * @param g                  the graphics context
     * @param timelineController the controller providing timing and scaling values
     * @param width              the width in pixels to draw into
     */
    @SuppressWarnings("methodlength")
    public static void drawAllTickMarks(Graphics g, TimelineController timelineController, int width, int beatDivisions, int beatsPerMeasure) {
        Timeline timeline = timelineController.getTimeline();

        g.setFont(MEASURE_FONT);

        long divisionTickInterval = Player.PULSES_PER_QUARTER_NOTE / beatDivisions;
        long measureTickInterval = (long) Player.PULSES_PER_QUARTER_NOTE * beatsPerMeasure;
        long beatTickInterval = Player.PULSES_PER_QUARTER_NOTE;

        for (long tick = 0; tick <= width / timeline.getPixelsPerTick(); tick += divisionTickInterval) {
            int height = TICK_HEIGHT;
            int pixelPosition = timeline.scaleTickToPixel(tick);
            g.setColor(DIVISION_TICK_COLOR);

            if (tick % measureTickInterval == 0) { // One measure
                height = RulerScrollPane.RULER_HEIGHT;
                long measure = (tick / measureTickInterval) + 1;
                g.setColor(TEXT_COLOR);
                g.drawString(String.valueOf(measure).concat(".1"), pixelPosition + FONT_PADDING, height - FONT_PADDING);
                g.setColor(MEASURE_COLOR);
            } else if (tick % beatTickInterval == 0) { // One beat
                height = BEAT_TICK_HEIGHT;
                g.setColor(BEAT_COLOR);
            }

            g.drawLine(pixelPosition, 0, pixelPosition, height);
        }
    }
}
