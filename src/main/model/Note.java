package model;

import org.json.JSONObject;

import model.editing.Copyable;
import persistance.Writable;

/**
 * A high-level MIDI note abstraction.
 * <p>
 * On its own, this Note is pure data, which will be converted to MIDI events and
 * applied to a {@code javax.sound.midi.Track} by MidiTrack at playback time.
 */
public class Note implements Writable, Copyable {

    public static final int PERCUSSIVE_DEFAULT_PITCH = 120;

    private int pitch; // Notes in a percussive track do not utilize pitch
    private int velocity;
    private long startTick;
    private long durationTicks;

    /**
     * Constructs a Note with the given attributes.
     * <p>
     * Preconditions:
     * - pitch and velocity must be in the range [0, 127]
     * - durationTicks >= 0 and startTick >= 0
     * <p>
     * Tick timings are kept relative to the block that they are in.
     *
     * @param pitch         the MIDI pitch (0-127); ignored for percussive tracks
     * @param velocity      the MIDI velocity (0-127)
     * @param startTick     the start tick relative to the containing block (>= 0)
     * @param durationTicks the duration in ticks (>= 0)
     */
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

    /**
     * Sets the pitch.
     *
     * @param newPitch the MIDI pitch (0-127)
     */
    public void setPitch(int newPitch) {
        pitch = newPitch;
    }

    /**
     * Sets the velocity.
     *
     * @param newVelocity the MIDI velocity (0-127)
     */
    public void setVelocity(int newVelocity) {
        velocity = newVelocity;
    }

    /**
     * Sets the start tick relative to the containing block.
     *
     * @param newStartTick the new start tick (>= 0)
     */
    public void setStartTick(long newStartTick) {
        startTick = newStartTick;
    }

    /**
     * Sets the duration in ticks.
     *
     * @param newDurationTicks the new duration in ticks (>= 0)
     */
    public void setDurationTicks(int newDurationTicks) {
        durationTicks = newDurationTicks;
    }

    /**
     * Returns a copy of this note with the same attributes.
     *
     * @return a new Note instance equal to this one
     */
    @Override
    public Note clone() {
        return new Note(getPitch(), getVelocity(), getStartTick(), getDurationTicks());
    }

    /**
     * Returns a JSON object representation of this note.
     *
     * @return the JSON representation of this note
     */
    @Override
    public JSONObject toJson() {
        JSONObject noteJson = new JSONObject();

        noteJson.put("pitch", pitch);
        noteJson.put("velocity", velocity);
        noteJson.put("startTick", startTick);
        noteJson.put("durationTicks", durationTicks);
        
        return noteJson;
    }

    /**
     * Returns a human-readable description of this note.
     *
     * @return a string containing pitch, velocity, startTick, and durationTicks
     */
    @Override 
    public String toString() {
        return String.format("pitch: %d, velocity: %d, startTick: %d, durationTicks: %d", 
                              pitch, velocity, startTick, durationTicks);
    }
}
