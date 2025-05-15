package model;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import persistance.Writable;

public class Player implements Writable {

    public static final int PULSES_PER_QUARTER_NOTE = 960;
    private static final float DEFAULT_BPM = 120;

    private Sequencer sequencer;
    private Sequence sequence;
    private Timeline timeline;

    private float bpm;
    private long positionTick;
    private ArrayList<Integer> availableChannels;

    public Player(Timeline timeline) {
        bpm = DEFAULT_BPM;
        positionTick = 0;
        this.timeline = timeline;

        try {
            sequencer = MidiSystem.getSequencer();
            sequence = new Sequence(Sequence.PPQ, PULSES_PER_QUARTER_NOTE);
            sequencer.open();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("MIDI device unavaliable, unable to initialize player", e);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid MIDI data found during player initialization, PPQ may be invalid", e);
        }

        availableChannels = new ArrayList<>() {
            {
                for (int i = 0; i <= 15; i++) {
                    if (i != 9) {
                        add(i); // add numbers 0-15 excluding 9, 9 is for percussion
                    }
                }
            }
        };
    }

    public ArrayList<Integer> getAvailableChannels() {
        return availableChannels;
    }

    // MODIFIES: this
    // EFFECTS: updates the sequence with the current midiTracks, converting each one 
    //          to a Java Track. Throws InvalidMidiDataException if invalid midi data
    //          is found when setting the sequence to the sequencer
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

    // MODIFIES: this
    // EFFECTS: deletes all Tracks from the sequence, essentially resetting playback
    public void resetTracks() {
        for (Track track : sequence.getTracks()) {
            sequence.deleteTrack(track);
        }

        Event e = new Event(String.format("Playback sequence cleared in Timeline %s", timeline.getProjectName()));
        EventLog.getInstance().logEvent(e);
    }

    // MODIFIES: this
    // EFFECTS: Begins playback at the current tick position and with tempo according to 
    //          beatsPerMinute. Can throw InvalidMidiDataException if invalid
    //          midi data is found during the sequence update (handled by UI)
    public void play() throws InvalidMidiDataException {
        updateSequence();
        sequencer.setTickPosition(positionTick);
        sequencer.setTempoInBPM(bpm);
        sequencer.start();

        Event e = new Event(String.format("Playback started, Sequence length: %d ticks",
                sequencer.getTickLength()));
        EventLog.getInstance().logEvent(e);
    }

    // MODIFIES: this
    // EFFECTS: Pauses playback
    public void pause() {
        sequencer.stop();
        updatePositionTick();

        Event e = new Event(String.format("Playback paused in Timeline %s at tick: %d",
                timeline.getProjectName(), sequencer.getTickPosition()));
        EventLog.getInstance().logEvent(e);
    }

    // MODFIES: this
    // EFFECTS: updates the position tick according to the current playback tick
    public void updatePositionTick() {
        setPositionTick(sequencer.getTickPosition());
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback given ticks
    public void setPositionTick(long newPositionTick) {
        long oldPositionTick = positionTick;
        this.positionTick = newPositionTick;

        timeline.getPropertyChangeSupport().firePropertyChange("positionTick", oldPositionTick, newPositionTick);
    }

    // MODIFIES: this
    // EFFECTS: replaces current available channels with new ones, use wisely
    //          channels may be out of sync with tracks 
    public void setAvailableChannels(ArrayList<Integer> newChannels) {
        availableChannels = newChannels;
    }

    public boolean isPlaying() {
        return sequencer.isRunning();
    }

    // REQUIRES: newPositionMs >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback at the given milliseconds
    public void setPositionMs(double newPositionMs) {
        setPositionTick(msToTicks(newPositionMs));
    }

    // REQUIRES: newPositionBeat >= 1
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback given the beat to start at
    public void setPositionBeat(double newPositionBeat) {
        setPositionTick(beatsToTicks(newPositionBeat - 1));
    }

    // REQUIRES: bpm >= 1
    // EFFECTS: changes the BPM
    public void setBPM(float bpm) {
        float oldBpm = bpm;
        this.bpm = bpm;

        timeline.getPropertyChangeSupport().firePropertyChange("bpm", oldBpm, bpm);
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: converts ticks to milliseconds given the BPM
    public double ticksToMs(long ticks) {
        double durationInQuarterNotes = (double) ticks / (double) sequence.getResolution();
        double durationInMinutes = durationInQuarterNotes / bpm;
        return durationInMinutes * 60000;
    }

    // REQUIRES: ms >= 0
    // EFFECTS: converts milliseconds to ticks (reverse of above)
    public long msToTicks(double ms) {
        double durationInMinutes = ms / (double) 60000;
        double durationInQuarterNotes = bpm * durationInMinutes;
        double ticks = durationInQuarterNotes * sequence.getResolution();
        return Math.round(ticks);
    }

    // REQUIRES: beats >= 0
    // EFFECTS: converts beats to ms
    public long beatsToMs(double beats) {
        return Math.round(beats / bpm * 60 * 1000);
    }

    // REQUIRES: beats >= 0
    // EFFECTS: calculates beats to ticks conversion
    public long beatsToTicks(double beats) {
        double ticks = beats * (double) sequence.getResolution();
        return Math.round(ticks);
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: calculates ticks to beats conversion (reverse of above)
    public double ticksToBeats(long ticks) {
        double beats = (double) ticks / (double) sequence.getResolution();
        return beats;
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: converts the ticks to which beat the ticks are on
    public double ticksToOnBeat(int ticks) {
        return ticksToBeats(ticks) + 1;
    }

    // EFFECTS: returns the calculation of the sequence length in beats
    public double getLengthBeats() {
        return ticksToBeats(timeline.getLengthTicks());
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    public double getLengthMs() {
        return ticksToMs(timeline.getLengthTicks());
    }

    public long getPositionTick() {
        return positionTick;
    }

    // EFFECTS: returns the timeline position in ms by converting ticks to ms
    public double getPositionMs() {
        return ticksToMs(positionTick);
    }

    // EFFECTS: returns the timeline position in beats by converting ticks to beats
    public double getPositionBeats() {
        return ticksToBeats(positionTick);
    }

    // EFFECTS: returns the beat on which the timeline position starts playback
    public double getPositionOnBeat() {
        return getPositionBeats() + 1;
    }

    public long getPositionTimeMs() {
        return sequencer.getMicrosecondLength();
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

    @Override
    public JSONObject toJson() {
        JSONObject playerJson = new JSONObject();

        playerJson.put("beatsPerMinute", bpm);
        playerJson.put("positionTick", positionTick);
        playerJson.put("availableChannels", new JSONArray(availableChannels));

        return playerJson;
    }
}