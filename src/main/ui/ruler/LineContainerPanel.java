package ui.ruler;

import java.awt.*;
import java.beans.PropertyChangeEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Player;
import model.TimelineController;
import ui.windows.timeline.midi.TrackRenderPanel;

// A container to draw the line of the position tick indicator line over tracks
public abstract class LineContainerPanel extends JPanel {

    private final TimelineController timelineController;
    private Player player;
    private int lineX;

    // EFFECTS: creates a LineContainerPanel that observes the timeline and has BoxLayout
    public LineContainerPanel(TimelineController timelineController, Player player) {
        super();
        this.timelineController = timelineController;
        this.player = player;
        lineX = 0;

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

    protected void updatePlayer() {
        this.player = timelineController.getTimeline().getPlayer();
        updateLineX();
    }
}