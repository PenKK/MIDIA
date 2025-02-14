package model;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

// The orchestrator of the whole project.
// This class will manage the primary Sequence object and is responsible for playback.
// Higher level MidiTrack(s) will be converted to the lower level Java Track for playback
public class Timeline {

    private Sequencer sequencer;
    private Sequence sequence;
    private ArrayList<MidiTrack> midiTracks;
    private float beatsPerMinute;
    private int positionTick;

    private ArrayList<Integer> avaliableInstrumentalChannels;

    private static final int PULSES_PER_QUARTER_NOTE = 960;
    private static final float DEFAULT_BPM = 120;

    // EFFECTS: Creates a timeline with a single sequence with no tracks and the positon 
    //          tick at 0, and a BPM of 120.
    //          Method throws MidiUnavailableException if the device has no MIDI sequencer
    //          avaliable, which is fatal and unrecoverable.
    //          Method throws InvalidMidiDataException if the Sequence has an invalid 
    //          divison type, which is impossile since the division type will be constant.
    public Timeline() throws MidiUnavailableException, InvalidMidiDataException {
        sequencer = MidiSystem.getSequencer();
        sequence = new Sequence(Sequence.PPQ, PULSES_PER_QUARTER_NOTE);

        sequencer.open();
        sequencer.setTempoInBPM(DEFAULT_BPM);

        beatsPerMinute = DEFAULT_BPM;
        midiTracks = new ArrayList<>();
        positionTick = 0;

        avaliableInstrumentalChannels = new ArrayList<>() {
            {
                for (int i = 0; i <= 15; i++) {
                    if (i != 9) {
                        add(i); // add numbers 0-15 exlcuding 9, 9 is for percussion
                    }

                }
            }
        };
    }

    // REQUIRES: avaliableChannels.size() >= 0
    // MODIFIES: this
    // EFFECTS: Creates a midiTrack, add its to the list of tracks and returns it
    public MidiTrack createMidiTrack(String name, int instrument, boolean percussive) {
        if (avaliableInstrumentalChannels.size() <= 0) {
            return null;
        }

        MidiTrack newMidiTrack = new MidiTrack(name, instrument,
                percussive ? 9 : avaliableInstrumentalChannels.remove(0));
        midiTracks.add(newMidiTrack);
        return newMidiTrack;
    }

    // REQUIRES: index >= 0 and midiTracks.size() > 0
    // MODIFIES: this
    // EFFECTS: removes the MidiTrack at the specified index from midiTracks, adds the
    //          channel to avaliable channels if the MidiTrack is not percussive, and then
    //          returns the MidiTrack
    public MidiTrack removeMidiTrack(int index) {
        MidiTrack toRemove = midiTracks.get(index);
        if (!toRemove.isPercussive()) {
            avaliableInstrumentalChannels.add(toRemove.getChannel());
        }
        return midiTracks.remove(index);
    }

    // MODIFIES: this
    // EFFECTS: updates the sequence with the current midiTracks, converting each one 
    //          to a Java Track. Throws InvalidMidiDataException if invalid midi data
    //          is found when setting the sequence to the sequencer
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        for (MidiTrack currentMidiTrack : midiTracks) {
            if (currentMidiTrack.isMuted()) {
                continue;
            }

            Track track = sequence.createTrack();
            currentMidiTrack.applyToTrack(track);
        }

        sequencer.setSequence(sequence);
    }

    // MODIFIES: this
    // EFFECTS: deletes all Tracks from the sequence, essentially resetting playback
    public void resetTracks() {
        for (Track track : sequence.getTracks()) {
            sequence.deleteTrack(track);
        }
    }

    // EFFECTS: Begins playback, can throw InvalidMidiDataException if invalid
    //          midi data is found during the sequence update (handled by UI)
    public void play() throws InvalidMidiDataException {
        updateSequence();
        sequencer.setTickPosition(positionTick);
        sequencer.setTempoInBPM(beatsPerMinute);
        sequencer.start();
    }

    // EFFECTS: Pauses playback
    public void pause() {
        sequencer.stop();
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback given ticks
    public void setPositionTick(int newPositionTick) {
        this.positionTick = newPositionTick;
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline positionto start playback given milliseconds
    public void setPositionMs(double newPositionMs) {
        this.positionTick = msToTicks(newPositionMs);
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline positionto start playback given milliseconds
    public void setPositionBeats(double newPositionBeats) {
        this.positionTick = beatsToTicks(newPositionBeats);
    }

    // REQUIRES: bpm >= 1
    // EFFECTS: changes the BPM
    public void setBPM(float bpm) {
        this.beatsPerMinute = bpm;
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    public double getLengthMs() {
        return ticksToMs(getLengthTicks());
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    public double getLengthBeats() {
        return ticksToBeats(getLengthTicks());
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: converts ticks to milliseconds given the BPM
    public double ticksToMs(int ticks) {
        double durationInQuarterNotes = (double) ticks / (double) PULSES_PER_QUARTER_NOTE;
        double durationInMinutes = durationInQuarterNotes / beatsPerMinute;
        double durationInMS = durationInMinutes * 60000;
        return durationInMS;
    }

    // REQUIRES: ms >= 0
    // EFFECTS: converts milliseconds to ticks (reverse of above)
    public int msToTicks(double ms) {
        double durationInMinutes = ms / (double) 60000;
        double durationInQuarterNotes = beatsPerMinute * durationInMinutes;
        double ticks = durationInQuarterNotes * PULSES_PER_QUARTER_NOTE;
        return (int) Math.round(ticks);
    }

    // REQUIRES: beats >= 0
    // EFFECTS: calculates beats to ticks conversion
    public int beatsToTicks(double beats) {
        double ticks = beats * (double) PULSES_PER_QUARTER_NOTE;
        return (int) Math.round(ticks);
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: calculates ticks to beats conversion (reverse of above)
    public double ticksToBeats(int ticks) {
        double beats = (double) ticks / (double) PULSES_PER_QUARTER_NOTE;
        return beats;
    }

    // EFFECTS: calculates the tick at which the last note ends, this method
    //          is needed to calculate length ticks without first calling updateSequence()
    public int getLengthTicks() {
        int lastNoteEndTick = 0;
        for (MidiTrack midiTrack : midiTracks) {
            for (Block block : midiTrack.getBlocks()) {
                for (Note note : block.getNotesTimeline()) {
                    int endTick = note.getStartTick() + note.getDurationTicks();
                    if (endTick > lastNoteEndTick) {
                        lastNoteEndTick = endTick;
                    }
                }
            }
        }
        return lastNoteEndTick;
    }

    public ArrayList<MidiTrack> getTracks() {
        return midiTracks;
    }

    // EFFECTS: returns the timeline position in ms by converting ticks
    public double getPositionMs() {
        return ticksToMs(positionTick);
    }

    // EFFECTS: returns the timeline position in beats by converting ticks
    public double getPositionBeats() {
        return ticksToBeats(positionTick);
    }

    // EFFECTS: returns the track at the specified index from tracks array
    public MidiTrack getTrack(int index) {
        return midiTracks.get(index);
    }

    public float getBPM() {
        return beatsPerMinute;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public int getPositionTick() {
        return positionTick;
    }

    public ArrayList<Integer> getAvaliableInstrumentalChannels() {
        return avaliableInstrumentalChannels;
    }
}
