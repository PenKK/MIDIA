package model;

import javax.sound.midi.Track;

// A high level representation of a track, which is a single layer/instrument of the project 
public class MidiTrack {

    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, a default instrument, a default volume (out of 127),
    //          and a name.
    public MidiTrack(String name) {
        // stub
    }

    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, set to a specified instrument, a default volume (out of 127),
    //          and a name.
    public MidiTrack(String name, int instrument) {
        // stub
    }

    // MODIFIES: this
    // EFFECTS: Adds a block to the list of blocks, returns the index it was created it
    public int addBlock(Block block) {
        return 0; // stub
    }

    // MODIFIES: track
    // EFFECTS: Converts each block from blocks to individual notes to MIDI events and applies it to an actual
    //          lower level Track object. Creates one event for the play sound, and one for the end per note.
    public void applyToTrack(Track track) {
        // stub
    }

    //REQUIRES: 0 <= newVolume <= 127
    public void setVolume(int newVolume) {
        //stub
    }

    public void setMuted(boolean mutedValue) {
        // stub
    }

    //REQUIRES: 0 <= instrument <= 127
    public void setInstrument(int instrument) {
        // stub
    }

    public int getVolume() {
        return 0; // stub
    }

    public boolean isMuted() {
        return false; // stub
    }
}
