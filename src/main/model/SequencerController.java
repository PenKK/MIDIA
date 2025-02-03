package model;

import java.util.ArrayList;

// The orchestrator of the whole project.
// This class will manage the primary Sequence object and is responsible for playback.
// Higher level MidiTrack(s) will be converted to the lower level Java Track here
public class SequencerController {

    // EFFECTS: Creates a sequencer with a single sequence with no tracks.
    public SequencerController() {
        // stub
    }

    // MODIFIES: this
    // EFFECTS: Adds the specified midiTrack to the list of midiTracks, returns the index of the created track
    public int addMidiTrack(MidiTrack midiTrack) {
        return 0; // stub
    }

    // MODIFIES: this
    // EFFECTS: updates the sequence with the current
    //          midiTracks, converting each one to a Java Track
    public void updateSequence() {
        // stub
    }

    // EFFECTS: Begins playback
    public void play() {
        // stub
    }

    // EFFECTS: Pauses playback
    public void pause() {
        // stub
    }

    // REQUIRES: time > 0
    // MODIFIES: this
    // EFFECTS: Changes timeline to start playback
    public void setCurrentTimelinePosition() {
        // stub
    }

    public ArrayList<MidiTrack> getTracks() {
        return new ArrayList<MidiTrack>(); //stub
    }

    // EFFECTS: returns the track at the specified index from tracks array
    public MidiTrack getTrack(int index) {
        return new MidiTrack(null, false); // stub
    }
}
