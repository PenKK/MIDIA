package ui.windows.timeline.ruler;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.ruler.RulerRenderPanel;

/**
 * Ruler render panel for the timeline view.
 * Draws measure, beat, and division tick marks and responds to timeline changes.
 */
public class TimelineRulerRenderPanel extends RulerRenderPanel implements PropertyChangeListener {

    private final TimelineController timelineController;

    /**
     * Creates the timeline ruler render panel and wires mouse and property listeners.
     */
    TimelineRulerRenderPanel(TimelineController timelineController) {
        super();
        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController,
                timelineController.getTimeline().getPlayer());
        this.timelineController = timelineController;
        addMouseAdapter(mouseAdapter);
        timelineController.addObserver(this);
    }

    /**
     * Paints the ruler tick marks according to the current timeline settings.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g, timelineController, getWidth());
    }

    /**
     * Repaints the ruler when timing or scale-related properties change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "beatDivision":
            case "beatsPerMeasure":
            case "timelineReplaced":
            case "horizontalScaleFactor":
                repaint();
                break;
        }
    }

}
