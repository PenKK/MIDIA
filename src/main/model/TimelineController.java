package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;

// A controller class responsible for managing a single Timeline instance
public class TimelineController implements MetaEventListener {

    public static final int PLAYER_END_META_TYPE = 47;

    private Timeline timeline;
    private final PropertyChangeSupport pcs;

    public TimelineController() {
       
        pcs = new PropertyChangeSupport(this);
        timeline = new Timeline("New Project", pcs);
        updateSequencerListener();
    }

    public Timeline getTimeline() {
        return timeline;
    }

    private void updateSequencerListener() {
        timeline.getPlayer().getSequencer().addMetaEventListener(this);
    }

    public void playTimeline() {
        try {
            timeline.play();
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid midi data found when starting playback", e);
        }
    }

    public boolean isPlaying() {
        return timeline.getPlayer().getSequencer().isRunning();
    }

    public void pauseTimeline() {
        timeline.pause();
    }

    public void setInstance(Timeline newTimeline) {
        Timeline oldTimeline = this.timeline;

        pcs.firePropertyChange("timelineAboutToBeReplaced", oldTimeline, newTimeline);

        if (isPlaying()) {
            pauseTimeline();
        }

        if (oldTimeline != null) {
            Player player = oldTimeline.getPlayer();
            player.getSequencer().removeMetaEventListener(this);
            player.close();
        }

        this.timeline = newTimeline;
        updateSequencerListener();
        pcs.firePropertyChange("timelineReplaced", oldTimeline, newTimeline);
    }

    // MODIFIES: this
    // EFFECTS: adds the specified observer as a listener of property changes
    public void addObserver(PropertyChangeListener observer) {
        pcs.addPropertyChangeListener(observer);
    }

    // MODIFIES: this
    // EFFECTS: removes the specified observer as a listener of property changes
    public void removeObserver(PropertyChangeListener observer) {
        pcs.removePropertyChangeListener(observer);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == PLAYER_END_META_TYPE) {
            pcs.firePropertyChange("playbackEnded", null, null);
        }
    }
}
