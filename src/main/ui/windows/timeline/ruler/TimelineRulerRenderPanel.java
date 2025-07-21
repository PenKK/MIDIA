package ui.windows.timeline.ruler;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.TimelineController;
import ui.ruler.RulerRenderPanel;

// The panel for Graphics to draw on to show Ruler tick marks
public class TimelineRulerRenderPanel extends RulerRenderPanel implements PropertyChangeListener {

    private TimelineController timelineController;

    // EFFECTS: Sets null border for zero padding, borders will be drawn via Graphics
    TimelineRulerRenderPanel(TimelineController timelineController) {
        super();
        TimelineRulerMouseAdapter mouseAdapter = new TimelineRulerMouseAdapter(timelineController);
        this.timelineController = timelineController;
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        timelineController.addObserver(this);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        super.drawAllTickMarks(g, timelineController, getWidth());
    }

    // EFFECTS: listens for property change events and runs methods accordingly
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
