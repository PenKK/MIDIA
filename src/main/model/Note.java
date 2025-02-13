package model;

// A high level MIDI note abstraction
// On its own, this Note is just pure information, which will be converted
// to a MIDIEvent object and applied to a Track object by the MidiTrack class.
public class Note {

    private int pitch; // Notes in a percussive track do not utilize pitch
    private int velocity;
    private int startTick;
    private int durationTicks;

    // REQUIRES: pitch and velocity are in range [0, 127].
    //           durationTicks, startTick >= 0
    // EFFECTS: Creates a note with pitch, velocity, startTick, and durationTicks.
    //          Tick timings are kept relative to the block that they are in
    public Note(int pitch, int velocity, int startTick, int durationTicks) {
        this.pitch = pitch;
        this.velocity = velocity;
        this.startTick = startTick;
        this.durationTicks = durationTicks;
    }

    public int getPitch() {
        return pitch;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public int getStartTick() {
        return startTick;
    }

    // REQUIRES: 0 <= newPitch <= 127
    public void setPitch(int newPitch) {
        pitch = newPitch;
    }

    // REQUIRES: 0 <= newVelocity <= 127
    public void setVelocity(int newVelocity) {
        velocity = newVelocity;
    }

    // REQUIRES: newStartTick >= 0
    public void setStartTick(int newStartTick) {
        startTick = newStartTick;
    }

    // REQUIRES: newDurationTicks>= 0
    public void setDurationTicks(int newDurationTicks) {
        durationTicks = newDurationTicks;
    }

    // EFFECTS: Creates a replica of the note and returns it. 
    //          Used for creating timeline adjusted notes.
    @Override
    public Note clone() {
        return new Note(getPitch(), getVelocity(), getStartTick(), getDurationTicks());
    }
}
