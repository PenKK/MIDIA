package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.Sequencer;

// A controller class responsible for managing a single Timeline instance
public class TimelineController implements MetaEventListener {

    private Timeline timeline;
    private PropertyChangeSupport pcs;

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

        if (oldTimeline != null) {
            Sequencer seqr = oldTimeline.getPlayer().getSequencer();
            seqr.removeMetaEventListener(this);
            seqr.close();
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

    // EFFECTS: forces a timeline update (for rendering purposes one day better fix hopefuly)
    public void refresh() {
        pcs.firePropertyChange("timelineReplaced", null, this);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == 47) {
            pcs.firePropertyChange("playbackEnded", null, null);
        }
    }
}
