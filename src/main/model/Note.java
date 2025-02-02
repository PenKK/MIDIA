package model;

// A high level MIDI note abstraction
// On its own, this Note is just pure information, which will be converted
// to a MIDIEvent object and applied to a Track object by the MidiTrack class.
public class Note {

    // REQUIRES: startTick >= 0
    // EFFECTS: Creates a note with pitch, velocity, startTick, and durationTicks.
    //          Tick timings are kept relative to the block that they are in
    public Note(int pitch, int velocity, long startTick, long durationTicks) {
        // stub
    }

    public int getPitch() {
        return 0; // stub
    }

    public int getVelocity() {
        return 0; // stub
    }

    public long getDurationTicks() {
        return 0; // stub
    }

    public long getStartTick() {
        return 0; // stub
    }

    public void setPitch(int pitch) {
        // stub
    }

    public void setVelocity(int velocity) {
        // stub
    }

    public void setStartTick(long startTick) {
        // stub
    }

    public void setDurationTicks(long durationTicks) {
        // stub
    }
}
