package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Timeline;
import model.TimelineController;

// A container to draw the line of the position tick indicator line over tracks
public class LineContainerPanel extends JPanel implements PropertyChangeListener {

    private TimelineController timelineController;
    private int lineX = TrackLabelPanel.LABEL_BOX_WIDTH;

    // EFFECTS: creates a LineContainerPanel that observs the timeline and has BoxLayout
    public LineContainerPanel(TimelineController timelineController) {
        super();
        this.timelineController = timelineController;
        timelineController.addObserver(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    // MODIFIES: this
    // EFFECTS: updates lineX value with the timeline positionTick (scaled) and repaints
    public void updateLineX() {
        Timeline timeline = timelineController.getTimeline();
        this.lineX = timeline.scalePixelsRender(timeline.getPlayer().getPositionTick()) 
                                                            + TrackLabelPanel.LABEL_BOX_WIDTH;
        repaint(); 
    }

    // EFFECTS: paints the position line at lineX
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (lineX >= 0) {
            g.setColor(Color.RED);
            g.drawLine(lineX, 0, lineX, getHeight());
        }
    }

    // EFFECTS: listens for property changes and runs methods accoringly 
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("positionTick")) {
            updateLineX();
        }
    }
}