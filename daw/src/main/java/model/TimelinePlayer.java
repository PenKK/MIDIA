package model;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import model.event.Event;
import model.event.EventLog;

/**
 * Plays back the entire Timeline.
 * <p>
 * Converts each MidiTrack into a {@code javax.sound.midi.Track} for playback,
 * manages updates to the underlying sequence, and propagates timeline-related
 * property changes (e.g., BPM, ruler drag, tick position) for UI synchronization.
 */
public class TimelinePlayer extends Player {
    
    private final Timeline timeline;

    public TimelinePlayer(Timeline timeline) {
        super();
        this.timeline = timeline;
    }

    /**
     * Updates the playback sequence with the current MidiTracks, converting each to a Java Track.
     *
     * @throws InvalidMidiDataException if invalid MIDI data is encountered when setting the sequence
     */
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

    /**
     * Sets the beat division and fires a property change event.
     *
     * @param newBeatDivision the new beat division
     * @return the previous beat division
     */
    @Override
    public int setBeatDivision(int newBeatDivision) {
        int oldBeatDivision = super.setBeatDivision(newBeatDivision);
        timeline.getPropertyChangeSupport().firePropertyChange("beatDivision", oldBeatDivision, newBeatDivision);
        return oldBeatDivision;
    }

    /**
     * Sets the beats per measure and fires a property change event.
     *
     * @param newBeatsPerMeasure the new beats per measure
     * @return the previous beats per measure value
     */
    @Override
    public int setBeatsPerMeasure(int newBeatsPerMeasure) {
        int oldBeatsPerMeasure = super.setBeatsPerMeasure(newBeatsPerMeasure);
        timeline.getPropertyChangeSupport().firePropertyChange("beatsPerMeasure", oldBeatsPerMeasure, newBeatsPerMeasure);
        return oldBeatsPerMeasure;
    }
    
    @Override
    public long setTickPosition(long newTickPosition) {
        long oldTickPosition = super.setTickPosition(newTickPosition);
        timeline.getPropertyChangeSupport().firePropertyChange("tickPosition", oldTickPosition, newTickPosition);
        return oldTickPosition;
    }

    @Override
    public float setBPM(float bpm) {
        float oldBpm = super.setBPM(bpm);
        timeline.getPropertyChangeSupport().firePropertyChange("bpm", oldBpm, bpm);
        return oldBpm;
    }

    /**
     * Returns the total sequence length in beats.
     *
     * @return length in beats
     */
    @Override
    public double getLengthBeats() {
        return ticksToBeats(timeline.getLengthTicks());
    }

    /**
     * Returns the total sequence length in milliseconds.
     *
     * @return length in milliseconds
     */
    @Override
    public double getLengthMs() {
        return ticksToMs(timeline.getLengthTicks());
    }

    /**
     * Returns the total sequence length in ticks.
     *
     * @return length in ticks
     */
    @Override
    public long getLengthTicks() { return timeline.getLengthTicks(); }

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
