package model;

// A high level MIDI note abstraction
// On its own, this Note is just pure information, which will be converted
// to a MIDIEvent object and applied to a Track object by the MidiTrack class.
public class Note {

    private int pitch;
    private int velocity;
    private int startTick;
    private int durationTicks;

    // REQUIRES: startTick >= 0
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

    public void setPitch(int newPitch) {
        pitch = newPitch;
    }

    public void setVelocity(int newVelocity) {
        velocity = newVelocity;
    }

    public void setStartTick(int newStartTick) {
        startTick = newStartTick;
    }

    public void setDurationTicks(int newDurationTicks) {
        durationTicks = newDurationTicks;
    }

    @Override
    public Note clone() {
        return new Note(getPitch(), getVelocity(), getStartTick(), getDurationTicks());
    }
}
