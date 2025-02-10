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
// Higher level MidiTrack(s) will be converted to the lower level Java Track here
public class Timeline {

    private Sequencer sequencer;
    private Sequence sequence;
    private ArrayList<MidiTrack> midiTracks;
    private float beatsPerMinute;
    private int positionTick;

    private static final int PULSES_PER_QUARTER_NOTE = 960;
    private static final float DEFAULT_BPM = 120;

    // EFFECTS: Creates a timeline with a single sequence with no tracks and the positon 
    //          tick at 0, and a BPM of 120.
    //          Method throws MidiUnavailableException if the device has no MIDI sequencer
    //          avaliable which is fatal and unrecoverable.
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
    }

    // MODIFIES: this
    // EFFECTS: Adds the specified midiTrack to the list of midiTracks, returns the index of the created track
    public int addMidiTrack(MidiTrack midiTrack) {
        midiTracks.add(midiTrack);
        return midiTracks.size() - 1;
    }

    // MODIFIES: this
    // EFFECTS: removes the MidiTrack at the specified index from midiTracks, and returns it
    public MidiTrack removeMidiTrack(int index) {
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
        sequencer.start();
    }

    // EFFECTS: Pauses playback
    public void pause() {
        sequencer.stop();
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline to start playback
    public void setPositionTick(int newPositionTick) {
        this.positionTick = newPositionTick;
    }

    // REQUIRES: bpm >= 1
    // EFFECTS: changes the BPM
    public void setBPM(float bpm) {
        this.beatsPerMinute = bpm;
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    public long getLengthMS() {
        double durationInQuarterNotes = (double) getLengthTicks() / (double) PULSES_PER_QUARTER_NOTE;
        double durationInMinutes =  durationInQuarterNotes / beatsPerMinute;
        double durationInMS = durationInMinutes * 60000;
        return (long) durationInMS;
    }

    // EFFECTS: calculates the tick at which the last note ends, this method
    //          is needed to calculate without first calling updateSequence()

    public long getLengthTicks() {
        long lastNoteEndTick = 0;
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

}
