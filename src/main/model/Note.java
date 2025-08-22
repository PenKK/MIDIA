package model;

import org.json.JSONObject;

import model.editing.Copyable;
import persistance.Writable;

// A high level MIDI note abstraction.
// On its own, this Note is just pure information, which will be converted
// to a MIDIEvent object and applied to a Track object by the MidiTrack class (by the timeline).
public class Note implements Writable, Copyable {

    public static final int PERCUSSIVE_DEFAULT_PITCH = 120;

    private int pitch; // Notes in a percussive track do not utilize pitch
    private int velocity;
    private long startTick;
    private long durationTicks;

    // REQUIRES: pitch and velocity are in range [0, 127].
    //           durationTicks, startTick >= 0
    // EFFECTS: Creates a note with pitch, velocity, startTick, and durationTicks.
    //          Tick timings are kept relative to the block that they are in
    public Note(int pitch, int velocity, long startTick, long durationTicks) {
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

    public long getDurationTicks() {
        return durationTicks;
    }

    public long getStartTick() {
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
    public void setStartTick(long newStartTick) {
        startTick = newStartTick;
    }

    // REQUIRES: newDurationTicks>= 0
    public void setDurationTicks(int newDurationTicks) {
        durationTicks = newDurationTicks;
    }

    // EFFECTS: Creates a clone of the note at a new memory address and returns it.
    @Override
    public Note clone() {
        return new Note(getPitch(), getVelocity(), getStartTick(), getDurationTicks());
    }

    // EFFECTS: returns a JSON object representation of the note
    @Override
    public JSONObject toJson() {
        JSONObject noteJson = new JSONObject();

        noteJson.put("pitch", pitch);
        noteJson.put("velocity", velocity);
        noteJson.put("startTick", startTick);
        noteJson.put("durationTicks", durationTicks);
        
        return noteJson;
    }

    // EFFECTS: returns a more informative toString
    @Override 
    public String toString() {
        return String.format("pitch: %d, velocity: %d, startTick: %d, durationTicks: %d", 
                              pitch, velocity, startTick, durationTicks);
    }
}
