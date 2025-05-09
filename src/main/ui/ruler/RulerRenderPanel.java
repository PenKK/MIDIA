package ui.ruler;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private static final Color TICK_COLOR = new Color(200,200,200);
    private static final int FONT_PADDING = 4;
    private static final Font MEASURE_FONT = new Font("Dialog", Font.PLAIN, 14);

    private TimelineController timelineController;
    private int tickPixelWidth;
    private int beatDivisions;
    private int beatsPerMeasure;

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    RulerRenderPanel(TimelineController timelineController) {
        this.timelineController = timelineController;
        this.setBorder(null);
        this.setBackground(Color.GRAY);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        this.addMouseListener(mouseAdapter());
        timelineController.addObserver(this);
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
        Timeline timeline = timelineController.getTimeline();
        beatDivisions = timeline.getBeatDivision();
        beatsPerMeasure = timeline.getBeatsPerMeasure();
        tickPixelWidth = timeline.scalePixelsRender(Player.PULSES_PER_QUARTER_NOTE / beatDivisions);

        repaint();
    }

    // MODIFIES: this
    // EFFECTS: Draws the ticks marks of measures, beats, and divisions, according to timeline instance
    private void drawAllTickMarks(Graphics g) {
        updateMeasurements();
        g.setColor(TICK_COLOR);
        g.setFont(MEASURE_FONT);

        for (int i = TrackLabelPanel.LABEL_BOX_WIDTH; i < getWidth(); i += tickPixelWidth) {
            int height = TICK_HEIGHT;
            int beatPixelWidth = tickPixelWidth * beatDivisions;
            int x = i - TrackLabelPanel.LABEL_BOX_WIDTH;
            
            if (x % (beatPixelWidth * beatsPerMeasure) == 0) { // One measure
                height = RulerScrollPane.RULER_HEIGHT; 
                String str = String.valueOf(x / (beatPixelWidth * beatsPerMeasure) + 1).concat(".1");
                g.drawString(str, i + FONT_PADDING, height - FONT_PADDING);
            } else if (x % beatPixelWidth == 0) { // One beat
                height = BEAT_TICK_HEIGHT; 
            }

            g.drawLine(i, 0, i, height);
        }
    }

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            
            @Override
            public void mouseClicked(MouseEvent e) {
               
            }
        
            @Override
            public void mousePressed(MouseEvent e) {
                Timeline timeline = timelineController.getTimeline();

                boolean resume = false;
                int tick = (int) ((e.getX() - TrackLabelPanel.LABEL_BOX_WIDTH) 
                                 / timelineController.getTimeline().getHorizontalScale());

                if (timelineController.isRunning()) {
                    timelineController.pauseTimeline();
                    resume = true;
                }

                timeline.getPlayer().setPositionTick(tick);
                System.out.println(timeline.durationRemainingMS());

                if (resume) {
                    timelineController.playTimeline();
                }
            }
        
            @Override
            public void mouseReleased(MouseEvent e) {
               
            }
        };
    }

    // EFFECTS: listens for property change events and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "beatDivsion":
            case "beatsPerMeasure":
            case "timelineReplaced":
            case "horizontalScale":
                updateMeasurements();
                break;
            default:
                break;
        }
    }

}
