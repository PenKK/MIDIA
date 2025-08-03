package ui.ruler;

import model.Player;
import model.Timeline;
import model.TimelineController;
import model.TimelinePlayer;
import ui.windows.timeline.midi.TrackLabelPanel;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class RulerMouseAdapter extends MouseInputAdapter implements PropertyChangeListener {

    protected boolean resume = false;

    private final TimelineController timelineController;
    private Player player;

    public RulerMouseAdapter(TimelineController timelineController, Player player) {
        this.timelineController = timelineController;
        this.player = player;

        timelineController.addObserver(this);
    }

    protected void updateX(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();

        int tick = (int) Math.max(0, (timeline.scalePixelToTick(e.getX())));

        tick = Math.max(tick, 0);
        player.setPositionTick(tick);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (player.isPlaying()) {
            player.pause();
            resume = true;
        }

        if (!player.isDraggingRuler()) {
            player.startRulerDrag();
        }
        updateX(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateX(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        player.stopRulerDrag();
        if (resume) {
            try {
                player.play();
            } catch (InvalidMidiDataException ex) {
                throw new RuntimeException("Failed to play player on mouse release", ex);
            }
            resume = false;
        }
    }

    private void updatePlayer() {
        if (player instanceof TimelinePlayer) {
            player = timelineController.getTimeline().getPlayer();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timelineReplaced")) {
            updatePlayer();
        }
    }
}
