package model;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.*;
import javax.swing.Timer;

import org.json.JSONArray;
import org.json.JSONObject;

import model.event.Event;
import model.event.EventLog;
import persistance.Writable;

public abstract class Player implements Writable, ActionListener {

    private static final int DEFAULT_BEAT_DIVISION = 4;
    private static final int DEFAULT_BEATS_PER_MEASURE = 4;

    public static final int PULSES_PER_QUARTER_NOTE = 960;
    protected static final float DEFAULT_BPM = 120;
    public static final int UI_UPDATE_DELAY = 10;

    protected Sequencer sequencer;
    protected Sequence sequence;

    protected int beatDivision;
    protected int beatsPerMeasure;

    protected float bpm;
    protected long tickPosition;
    protected ArrayList<Integer> availableChannels;
    protected Timer playbackUpdateTimer;
    protected boolean isDraggingRuler;

    /**
     * Creates a new Player instance. User must run close() to free resources after use.
     */
    public Player() {
        bpm = DEFAULT_BPM;
        tickPosition = 0;
        beatDivision = DEFAULT_BEAT_DIVISION;
        beatsPerMeasure = DEFAULT_BEATS_PER_MEASURE;
        playbackUpdateTimer = new Timer(UI_UPDATE_DELAY, this);
        isDraggingRuler = false;

        try {
            if (GraphicsEnvironment.isHeadless()) {
                // Headless: use software synthesizer, don't attempt connection with default hardware
                sequencer = MidiSystem.getSequencer(false);
                sequencer.open();
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                sequencer.getTransmitter().setReceiver(synth.getReceiver());
                EventLog.getInstance().logEvent(new Event("Headless system, using software synthesizer"));
            } else {
                // else use the default sequencer
                sequencer = MidiSystem.getSequencer();
                sequencer.open();
            }
            sequence = new Sequence(Sequence.PPQ, PULSES_PER_QUARTER_NOTE);
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI device unavailable, unable to initialize player", e);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid MIDI data found during player initialization, PPQ may be invalid", e);
        }
        // Omit 9 from available channels; 9 is reserved for percussion
        availableChannels = new ArrayList<>(List.of(0,1,2,3,4,5,6,7,8,10,11,12,13,14,15));
    }

    public ArrayList<Integer> getAvailableChannels() {
        return availableChannels;
    }

    /**
     * Updates the sequence with the current notes/tracks for playback.
     *
     * @throws InvalidMidiDataException if invalid MIDI data is encountered during the update
     */
    public abstract void updateSequence() throws InvalidMidiDataException;

    /**
     * Deletes all tracks from the sequence, effectively resetting playback.
     */
    public void resetTracks() {
        for (Track track : sequence.getTracks()) {
            sequence.deleteTrack(track);
        }
    }

    /**
     * Begins playback at the current tick position with the current tempo.
     *
     * @throws InvalidMidiDataException if invalid MIDI data is found during sequence update
     */
    public void play() throws InvalidMidiDataException {
        updateSequence();
        sequencer.setTickPosition(tickPosition);
        sequencer.setTempoInBPM(bpm);
        sequencer.start();
        playbackUpdateTimer.start();

        Event e = new Event(String.format("Playback started, Sequence length: %d ticks",
                sequencer.getTickLength()));
        EventLog.getInstance().logEvent(e);
    }

    /**
     * Pauses playback and synchronizes internal tick position with the sequencer.
     */
    public void pause() {
        sequencer.stop();
        playbackUpdateTimer.stop();
        syncToSequencerTickPosition();
    }

    /**
     * Frees all system resources held by the player. <p>
     * Closes {@code sequencer}.
     */
    public void close() {
        sequencer.close();
    }

    /**
     * Updates the internal tick position from the sequencer's current playback tick.
     */
    public void syncToSequencerTickPosition() {
        setTickPosition(sequencer.getTickPosition());
    }

    /**
     * Sets the beat division.
     *
     * @param newBeatDivision the new beat division
     * @return the previous beat division
     */
    public int setBeatDivision(int newBeatDivision) {
        int oldBeatDivision = this.beatDivision;
        this.beatDivision = newBeatDivision;
        return oldBeatDivision;
    }

    /**
     * Sets the beats per measure.
     *
     * @param newBeatsPerMeasure the new beats per measure
     * @return the previous beats per measure
     */
    public int setBeatsPerMeasure(int newBeatsPerMeasure) {
        int oldBeatsPerMeasure = this.beatsPerMeasure;
        this.beatsPerMeasure = newBeatsPerMeasure;
        return oldBeatsPerMeasure;
    }

    /**
     * Sets the timeline position (in ticks).
     * <p>
     * Preconditions: {@code newTickPosition >= 0}
     *
     * @param newTickPosition the new tick position
     * @return the previous tick position
     */
    public long setTickPosition(long newTickPosition) {
        long oldTickPosition = tickPosition;
        this.tickPosition = newTickPosition;
        return oldTickPosition;
    }

    public long snapTickNearest(long rawTick) {
        long divisionTickInterval = PULSES_PER_QUARTER_NOTE / beatDivision;
        return Math.round((double) rawTick / divisionTickInterval) * divisionTickInterval;
    }

    public long snapTickLowerDivision(long rawTick) {
        long divisionTickInterval = PULSES_PER_QUARTER_NOTE / beatDivision;
        return (rawTick / divisionTickInterval) * divisionTickInterval;
    }

    public long snapTickLowerBeatDivision(long rawTick) {
        long divisionTickInterval = Player.PULSES_PER_QUARTER_NOTE / beatDivision;
        return (rawTick / divisionTickInterval) * divisionTickInterval;
    }

    /**
     * Replaces the current list of available channels.
     * Use with caution; channels may become out of sync with tracks.
     *
     * @param newChannels the new available channels
     */
    public void setAvailableChannels(ArrayList<Integer> newChannels) {
        availableChannels = newChannels;
    }

    public boolean isPlaying() {
        return sequencer.isRunning();
    }

    /**
     * Sets the timeline position by milliseconds.
     * <p>
     * Preconditions: {@code newPositionMs >= 0}
     *
     * @param newPositionMs the position in milliseconds
     */
    public void setPositionMs(double newPositionMs) {
        setTickPosition(msToTicks(newPositionMs));
    }

    /**
     * Sets the timeline position by beat number (1-based).
     * <p>
     * Preconditions: {@code newPositionBeat >= 1}
     *
     * @param newPositionBeat the beat number (1-based)
     */
    public void setPositionBeat(double newPositionBeat) {
        setTickPosition(beatsToTicks(newPositionBeat - 1));
    }

    /**
     * Sets the tempo in beats per minute.
     * <p>
     * Preconditions: {@code bpm >= 1}
     *
     * @param bpm the new tempo
     * @return the previous tempo
     */
    public float setBPM(float bpm) {
        float oldBpm = this.bpm;
        this.bpm = bpm;

        if (isPlaying()) {
            sequencer.setTempoInBPM(bpm);
        }

        return oldBpm;
    }

    /**
     * Converts ticks to milliseconds using the current BPM.
     * <p>
     * Preconditions: {@code ticks >= 0}
     *
     * @param ticks the tick count
     * @return the duration in milliseconds
     */
    public double ticksToMs(long ticks) {
        double durationInQuarterNotes = (double) ticks / (double) sequence.getResolution();
        double durationInMinutes = durationInQuarterNotes / bpm;
        return durationInMinutes * 60000;
    }

    /**
     * Converts milliseconds to ticks.
     * <p>
     * Preconditions: {@code ms >= 0}
     *
     * @param ms the duration in milliseconds
     * @return the equivalent ticks
     */
    public long msToTicks(double ms) {
        double durationInMinutes = ms / (double) 60000;
        double durationInQuarterNotes = bpm * durationInMinutes;
        double ticks = durationInQuarterNotes * sequence.getResolution();
        return Math.round(ticks);
    }

    /**
     * Converts beats to milliseconds.
     * <p>
     * Preconditions: {@code beats >= 0}
     *
     * @param beats the number of beats
     * @return the duration in milliseconds
     */
    public long beatsToMs(double beats) {
        return Math.round(beats / bpm * 60 * 1000);
    }

    /**
     * Converts beats to ticks using the sequence resolution.
     * <p>
     * Preconditions: {@code beats >= 0}
     *
     * @param beats the number of beats
     * @return the equivalent ticks
     */
    public long beatsToTicks(double beats) {
        double ticks = beats * (double) sequence.getResolution();
        return Math.round(ticks);
    }

    /**
     * Converts ticks to beats using the sequence resolution.
     * <p>
     * Preconditions: {@code ticks >= 0}
     *
     * @param ticks the tick count
     * @return the number of beats
     */
    public double ticksToBeats(long ticks) {
        return (double) ticks / (double) sequence.getResolution();
    }

    /**
     * Converts ticks to a 1-based beat index.
     * <p>
     * Preconditions: {@code ticks >= 0}
     *
     * @param ticks the tick count
     * @return the 1-based beat index
     */
    public double ticksToOnBeat(int ticks) {
        return ticksToBeats(ticks) + 1;
    }

    public void incrementBeatDivision() {
        beatDivision = Math.min(Player.PULSES_PER_QUARTER_NOTE, beatDivision + 1);
    }

    public void decrementBeatDivision() {
        beatDivision = Math.max(1, beatDivision - 1);
    }

    public void incrementBeatsPerMeasure() {
        beatsPerMeasure = Math.min(Player.PULSES_PER_QUARTER_NOTE * 4, beatsPerMeasure + 1);
    }

    public void decrementBeatsPerMeasure() {
        beatsPerMeasure = Math.max(1, beatsPerMeasure - 1);
    }

    public void startRulerDrag() {
        isDraggingRuler = true;
    }

    public void stopRulerDrag() {
        isDraggingRuler = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(playbackUpdateTimer)) {
            if (!isDraggingRuler) {
                syncToSequencerTickPosition();
            }
        }
    }

    /**
     * Returns the sequence length in beats.
     *
     * @return total length in beats
     */
    public abstract double getLengthBeats();

    /**
     * Returns the sequence length in milliseconds.
     *
     * @return total length in milliseconds
     */
    public abstract double getLengthMs();

    /**
     * Returns the sequence length in ticks.
     *
     * @return total length in ticks
     */
    public abstract long getLengthTicks();

    public long getTickPosition() {
        return tickPosition;
    }

    /**
     * Returns the timeline position in milliseconds (converted from ticks).
     *
     * @return current position in milliseconds
     */
    public double getPositionMs() {
        return ticksToMs(tickPosition);
    }

    /**
     * Returns the timeline position in beats (converted from ticks).
     *
     * @return current position in beats
     */
    public double getPositionBeats() {
        return ticksToBeats(tickPosition);
    }

    /**
     * Returns the 1-based beat index corresponding to the current position.
     *
     * @return the beat number (1-based)
     */
    public double getPositionOnBeat() {
        return getPositionBeats() + 1;
    }

    public float getBPM() {
        return bpm;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    public int getBeatDivision() {
        return beatDivision;
    }

    public boolean isDraggingRuler() {
        return isDraggingRuler;
    }

    @Override
    public JSONObject toJson() {
        JSONObject playerJson = new JSONObject();

        playerJson.put("beatsPerMinute", bpm);
        playerJson.put("tickPosition", tickPosition);
        playerJson.put("availableChannels", new JSONArray(availableChannels));

        return playerJson;
    }

    public Timer getPlaybackUpdaterTimer() {
        return playbackUpdateTimer;
    }
}