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

// A block exists in a track and is a group of notes.
// The block can be moved on the timeline by changing the startTick.
public class Block implements Writable, Copyable, Pastable {

    private final ArrayList<Note> notes;
    private long startTick;
    private long durationTicks;

    // REQUIRES: startTick >= 0
    // EFFECTS: Creates a block with no notes inside of it with a startTick
    public Block(long startTick, long durationTicks) {
        this.notes = new ArrayList<>();
        this.startTick = startTick;
        this.durationTicks = durationTicks;
    }
    
    // MODIFIES: this
    // EFFECTS: Adds the note to the list of notes in this block and adjusts lengthTick if neccessary, 
    //          returns created notes index
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

    // REQUIRES: 0 <= index <= notes.size() - 1
    // MODIFIES: this
    // EFFECTS: Removes the specified index note from notes and returns it
    public Note removeNote(int index) {
        Note n = notes.remove(index);

        Event e = new Event(String.format("Removed note: %s from Block: %s", n, info()));
        EventLog.getInstance().logEvent(e);

        return n;
    }

    // REQUIRES: newStartTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes the position of the block on the timeline
    public void setStartTick(long newStartTick) {
        startTick = newStartTick;
    }

    public void setDurationTicks(long durationTicks) {
        this.durationTicks = durationTicks;
    }

    // EFFECTS: Returns notes with timings adjusted relative to the timeline, rather than the block
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

    // EFFECTS: Returns a clone of this block with its own unique memory address
    @Override
    public Block clone() {
        Block cloneBlock = new Block(startTick, durationTicks);
        for (Note note : notes) {
            cloneBlock.addNote(note.clone());
        }
        return cloneBlock;
    }

    // EFFECTS: returns a JSON object representation of the block
    @Override
    public JSONObject toJson() {
        JSONObject blockJson = new JSONObject();

        blockJson.put("durationTicks", durationTicks);
        blockJson.put("startTick", startTick);
        blockJson.put("notes", notesToJson());
        
        return blockJson;
    }

    // EFFECTS: returns a JSON Array representation of the notes in this block
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

    // EFFECTS: returns a string with general information about the block
    public String info() {
        return String.format("Start tick: %d, duration: %d, current note count: %d",
                             startTick, durationTicks, notes.size());
    }

    // EFFECTS: returns a string with start tick and note count
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
