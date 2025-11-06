package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import model.editing.Copyable;
import model.editing.Pastable;
import model.event.Event;
import model.event.EventLog;
import persistance.Writable;

/**
 * A block is a group of notes that exists within a track.
 * The block can be moved on the timeline by changing its start tick.
 */
public class Block implements Writable, Copyable, Pastable {

    private final ArrayList<Note> notes;
    private long startTick;
    private long durationTicks;

    /**
     * Constructs an empty block.
     * <p>
     * Preconditions: startTick >= 0
     *
     * @param startTick     the start tick of the block (>= 0)
     * @param durationTicks the duration of the block in ticks
     */
    public Block(long startTick, long durationTicks) {
        this.notes = new ArrayList<>();
        this.startTick = startTick;
        this.durationTicks = durationTicks;
    }
    
    /**
     * Adds a note to this block.
     * <p>
     * If the note extends beyond this block's duration, the note is rejected and -1 is returned.
     * Logs an event describing the operation.
     *
     * @param note the note to add
     * @return the index of the added note, or -1 if the note is out of bounds
     */
    public int addNote(Note note) {
        if (note.getStartTick() + note.getDurationTicks() > durationTicks) {
            Event e = new Event(String.format("Unable to add note (out of bounds): %s to Block: %s", note, info()));
            EventLog.getInstance().logEvent(e);
            return -1;
        }

        notes.add(note);
        Event e = new Event(String.format("Added note: %s to Block: %s", note, info()));
        EventLog.getInstance().logEvent(e);

        return notes.size() - 1;
    }

    /**
     * Removes and returns the note at the given index.
     * <p>
     * Preconditions: 0 <= index <= notes.size() - 1
     *
     * @param index the index of the note to remove
     * @return the removed note
     */
    public Note removeNote(int index) {
        Note n = notes.remove(index);

        Event e = new Event(String.format("Removed note: %s from Block: %s", n, info()));
        EventLog.getInstance().logEvent(e);

        return n;
    }

    /**
     * Sets the start tick of the block, changing its position on the timeline.
     * <p>
     * Preconditions: newStartTick >= 0
     *
     * @param newStartTick the new start tick (>= 0)
     */
    public void setStartTick(long newStartTick) {
        startTick = newStartTick;
    }

    public void setDurationTicks(long durationTicks) {
        this.durationTicks = durationTicks;
    }

    /**
     * Returns the notes with their start times adjusted relative to the timeline (not the block).
     *
     * @return a new list of notes adjusted to absolute timeline ticks
     */
    public ArrayList<Note> getNotesTimeline() {
        ArrayList<Note> adjustedNotes = new ArrayList<>();

        for (Note note : notes) {
            Note adjustedNote = note.clone();
            adjustedNote.setStartTick(this.startTick + note.getStartTick());
            adjustedNotes.add(adjustedNote);
        }

        return adjustedNotes;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public long getStartTick() {
        return startTick;
    }

    /**
     * Returns a deep copy of this block and its notes.
     *
     * @return a new Block instance containing clones of this block's notes
     */
    @Override
    public Block clone() {
        Block cloneBlock = new Block(startTick, durationTicks);
        for (Note note : notes) {
            cloneBlock.addNote(note.clone());
        }
        return cloneBlock;
    }

    /**
     * Returns a JSON object representation of this block.
     *
     * @return the JSON representation of this block
     */
    @Override
    public JSONObject toJson() {
        JSONObject blockJson = new JSONObject();

        blockJson.put("durationTicks", durationTicks);
        blockJson.put("startTick", startTick);
        blockJson.put("notes", notesToJson());
        
        return blockJson;
    }

    /**
     * Returns a JSON array representation of the notes in this block.
     *
     * @return a JSON array containing the notes
     */
    private JSONArray notesToJson() {
        JSONArray notesJson = new JSONArray();

        for (Note note : notes) {
            notesJson.put(note.toJson());
        }

        return notesJson;
    }

    public long getDurationTicks() {
        return durationTicks;
    }

    /**
     * Returns a string containing general information about the block.
     *
     * @return formatted string with start tick, duration, and note count
     */
    public String info() {
        return String.format("Start tick: %d, duration: %d, current note count: %d",
                             startTick, durationTicks, notes.size());
    }

    /**
     * Returns a concise string with start tick and note count.
     *
     * @return formatted summary string
     */
    @Override
    public String toString() {
        return String.format("S: %d, N: %d", startTick, notes.size());
    }

    @Override
    public void paste(List<Copyable> copied, long position) {

        for (Copyable c : copied) {
            if (!c.getClass().equals(Note.class)) { // Only notes may be pasted into a block
                continue;
            }

            this.addNote((Note) c);
        }
        
    }
}
