package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Timer;

// A controller class responsible for managing a single Timeline instance
public class TimelineController implements ActionListener {

    private Timeline timeline;
    private PropertyChangeSupport pcs;
    private Timer playbackEndTimer;

    public TimelineController() throws MidiUnavailableException, InvalidMidiDataException {
        pcs = new PropertyChangeSupport(this);
        playbackEndTimer = new Timer(0, this);
        playbackEndTimer.setRepeats(false);
        timeline = new Timeline("New Project", pcs);
    }

    public Timeline getTimeline() {
        return timeline;
    }

    public void playTimeline() {
        int delay = (int) (timeline.getLengthMs() - timeline.getPlayer().getPositionMs());

        if (delay < 0) {
            return;
        }
        
        playbackEndTimer.setInitialDelay(delay);
        playbackEndTimer.setDelay(delay);
        playbackEndTimer.start();

        try {
            timeline.play();
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid midi data found when starting playback", e);
        }
    }

    public boolean isRunning() {
        return timeline.getPlayer().getSequencer().isRunning();
    }

    public void pauseTimeline() {
        playbackEndTimer.stop();
        timeline.pause();
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

    public Timer getPlaybackEndTimer() {
        return playbackEndTimer;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(playbackEndTimer)) {
            System.out.println("ENDED");
            pcs.firePropertyChange("playbackEnded", null, null);
        }
    }
}
