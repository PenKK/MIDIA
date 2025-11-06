package ui.ruler;

import model.Player;
import model.Timeline;
import model.TimelineController;
import model.TimelinePlayer;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Mouse handler for setting the playback position via the ruler.
 * Pauses/resumes playback during drag and tracks timeline replacement events.
 */
public class RulerMouseAdapter extends MouseInputAdapter implements PropertyChangeListener {

    protected boolean resume = false;

    private final TimelineController timelineController;
    private Player player;

    /**
     * Creates an adapter bound to a controller and player, and subscribes to timeline events.
     *
     * @param timelineController the controller sourcing timeline state
     * @param player             the player to control during dragging
     */
    public RulerMouseAdapter(TimelineController timelineController, Player player) {
        this.timelineController = timelineController;
        this.player = player;

        timelineController.addObserver(this);
    }

    protected void updateX(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();

        int tick = (int) Math.max(0, (timeline.scalePixelToTick(e.getX())));

        tick = Math.max(tick, 0);
        player.setTickPosition(tick);
    }

    /**
     * Starts ruler dragging, pausing playback and capturing the new position.
     */
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

    /**
     * Updates the playback position while dragging across the ruler.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        updateX(e);
    }

    /**
     * Completes ruler dragging and resumes playback if it was playing before.
     */
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

    /**
     * Rebinds to the current timeline's player when the timeline is replaced.
     */
    private void updatePlayer() {
        if (player instanceof TimelinePlayer) {
            player = timelineController.getTimeline().getPlayer();
        }
    }

    /**
     * Listens for timeline replacement to refresh the player reference.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timelineReplaced")) {
            updatePlayer();
        }
    }
}
