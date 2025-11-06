package ui.common;

import java.awt.*;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Player;
import model.TimelineController;
import ui.windows.timeline.midi.TrackRenderPanel;

/**
 * Container panel that overlays a moving playhead line above track components.
 * Observes the timeline/player and repaints when the position changes.
 */
public abstract class LineContainerPanel extends JPanel {

    private final TimelineController timelineController;
    private Player player;
    private int lineX;

    /**
     * Creates a LineContainerPanel that observes the timeline and arranges children vertically.
     *
     * @param timelineController the controller providing timeline state
     * @param player             the player supplying the current tick position
     */
    public LineContainerPanel(TimelineController timelineController, Player player) {
        super();
        this.timelineController = timelineController;
        this.player = player;
        lineX = 0;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    /**
     * Updates the x-position of the playhead line from the current tick and repaints.
     */
    public void updateLineX() {
        this.lineX = timelineController.getTimeline().scaleTickToPixel(player.getTickPosition());
        repaint();
    }

    /**
     * Paints the playhead line at the current x position.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (lineX >= 0) {
            g.setColor(Color.RED);
            g.drawLine(lineX, 0, lineX, getHeight());
        }
    }

    /**
     * Computes preferred width based on children, adding padding for track rendering.
     */
    @Override
    public Dimension getPreferredSize() {
        int width = 0;
        for (Component comp : getComponents()) {
            width = Math.max(width, comp.getPreferredSize().width);
        }
        return new Dimension(width + TrackRenderPanel.WIDTH_PADDING, super.getPreferredSize().height);
    }

    /**
     * Rebinds to the current player (e.g., on timeline replacement) and updates the playhead.
     */
    protected void updatePlayer() {
        this.player = timelineController.getTimeline().getPlayer();
        updateLineX();
    }
}