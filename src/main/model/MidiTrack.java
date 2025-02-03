package model;

import java.util.ArrayList;

import javax.sound.midi.Track;

// A high level representation of a track, which is a single layer/instrument of the project
public class MidiTrack {
    
    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, a default instrument, a default volume (out of 127),
    //          percussive according to parameter, and a name. If it is precussive default instrument is 35 (bass drum)
    public MidiTrack(String name, boolean precussive) {
        // stub
    }

    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, set to a specified instrument, a default volume (out of 127),
    //          percussive according to parameter, and a name.
    public MidiTrack(String name, int instrument, boolean precussive) {
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

    public ArrayList<Block> getBlocks() {
        return new ArrayList<>(); // stub
    }

    public int getInstrument() {
        return 0; // stub
    }

    public String getName() {
        return ""; // stub
    }

    public boolean getPercussive() {
        return false; // stub
    }
}
