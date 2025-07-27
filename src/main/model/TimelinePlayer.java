package model;

import java.awt.event.ActionEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import model.event.Event;
import model.event.EventLog;

public class TimelinePlayer extends Player {
    
    private final Timeline timeline;

    public TimelinePlayer(Timeline timeline) {
        this.timeline = timeline;
    }

    // MODIFIES: this
    // EFFECTS: updates the sequence with the current midiTracks, converting each one 
    //          to a Java Track. Throws InvalidMidiDataException if invalid midi data
    //          is found when setting the sequence to the sequencer
    @Override
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        for (MidiTrack currentMidiTrack : timeline.getMidiTracks()) {
            if (currentMidiTrack.isMuted() || currentMidiTrack.getVolume() == 0) {
                continue;
            }

            Track track = sequence.createTrack();
            currentMidiTrack.applyToTrack(track);
        }

        sequencer.setSequence(sequence);

        Event e = new Event(String.format("Playback sequence was updated in Timeline %s", timeline.getProjectName()));
        EventLog.getInstance().logEvent(e);
    }

    @Override
    public void resetTracks() {
        super.resetTracks();
        Event e = new Event(String.format("Playback sequence cleared in Timeline %s", timeline.getProjectName()));
        EventLog.getInstance().logEvent(e);
    }

    @Override
    public void pause() {
        super.pause();
        Event e = new Event(String.format("Playback paused in Timeline %s at tick: %d",
                timeline.getProjectName(), sequencer.getTickPosition()));
        EventLog.getInstance().logEvent(e);
    }
    
    @Override
    public long setPositionTick(long newPositionTick) {
        long oldPositionTick = super.setPositionTick(newPositionTick);
        timeline.getPropertyChangeSupport().firePropertyChange("positionTick", oldPositionTick, newPositionTick);
        return oldPositionTick;
    }

    @Override
    public float setBPM(float bpm) {
        float oldBpm = super.setBPM(bpm);
        timeline.getPropertyChangeSupport().firePropertyChange("bpm", oldBpm, bpm);
        return oldBpm;
    }

    // EFFECTS: returns the calculation of the sequence length in beats
    @Override
    public double getLengthBeats() {
        return ticksToBeats(timeline.getLengthTicks());
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    @Override
    public double getLengthMs() {
        return ticksToMs(timeline.getLengthTicks());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(playbackUpdateTimer)) {
            if (!isDraggingRuler) {
                updatePositionTick();
            }

            timeline.getPropertyChangeSupport().firePropertyChange("positionTick", null, null);
        }
    }

    @Override
    public void startRulerDrag() {
        super.startRulerDrag();
        timeline.getPropertyChangeSupport().firePropertyChange("rulerDragStarted", null, null);
    }
    
    @Override
    public void stopRulerDrag() {
        super.stopRulerDrag();
        timeline.getPropertyChangeSupport().firePropertyChange("rulerDragStopped", null, null);
    }

    
}
