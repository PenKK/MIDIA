package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class TimelineController {

    private Timeline timeline;
    private PropertyChangeSupport pcs;

    public TimelineController() throws MidiUnavailableException, InvalidMidiDataException {
        pcs = new PropertyChangeSupport(this);
        timeline = new Timeline("New Project", pcs);
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void setInstance(Timeline newTimeline) {
        Timeline oldTimeline = this.timeline;

        if (oldTimeline != null) {
            oldTimeline.getPlayer().getSequencer().close();
        }

        this.timeline = newTimeline;
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
}
