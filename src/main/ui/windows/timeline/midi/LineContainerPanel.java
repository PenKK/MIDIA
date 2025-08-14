package ui.windows.timeline.midi;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Player;
import model.TimelineController;

// A container to draw the line of the position tick indicator line over tracks
public class LineContainerPanel extends JPanel implements PropertyChangeListener {

    private final TimelineController timelineController;
    private Player player;
    private int lineX;

    // EFFECTS: creates a LineContainerPanel that observes the timeline and has BoxLayout
    public LineContainerPanel(TimelineController timelineController, Player player) {
        super();
        this.timelineController = timelineController;
        this.player = player;
        lineX = 0;

        timelineController.addObserver(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    // MODIFIES: this
    // EFFECTS: updates lineX value with the timeline positionTick (scaled) and repaints
    public void updateLineX() {
        this.lineX = (int) timelineController.getTimeline().scaleTickToPixel(player.getPositionTick());
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

    @Override
    public Dimension getPreferredSize() {
        int width = 0;
        for (Component comp : getComponents()) {
            width = Math.max(width, comp.getPreferredSize().width);
        }
        return new Dimension(width + TrackRenderPanel.WIDTH_PADDING, super.getPreferredSize().height);
    }

    private void updatePlayer() {
        this.player = timelineController.getTimeline().getPlayer();
        updateLineX();
    }

    // EFFECTS: listens for property changes and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "positionTick":
            case "horizontalScaleFactor":
                updateLineX();
                break;
            case "timelineReplaced":
                updatePlayer();
                break;
            case "blockPasted":
            case "blockCreated":
            case "noteCreated":
                repaint();
                break;
        }
    }
}