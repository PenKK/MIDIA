package model;

import java.util.ArrayList;

// A block is a group of notes that can be moved on the timeline
public class Block {

    // REQUIRES: startTick > 0
    // EFFECTS: Creates a block with no notes inside of it with a startTick
    public Block(long startTick) {
        // stub
    }
    
    // MODIFIES: this
    // EFFECTS: Adds the note to the list of notes in this block and adjusts lengthTick if neccessary, 
    //          returns created notes index
    public int addNote(Note note) {
        return 0; // stub
    }

    // REQUIRES: 0 <= index <= notes.size() - 1
    // EFFECTS: Removes the specified index note from notes and returns it, adjusts lengthTick if neccessary
    public Note removeNote(int index) {
        return null; // stub
    }

    // REQUIRES: newStartTime > 0
    // EFFECTS: Changes the position of the block on the time line
    public void setStartTick(long newStartTick) {
        // stub
    }

    // EFFECTS: Returns notes with timings adjusted relative to the timeline, rather than the block
    public ArrayList<Note> getNotesTimeline() {
        return null; // stub
    }

    public ArrayList<Note> getNotes() {
        return null; // stub
    }

    public long getStartTick() {
        return 0; // stub
    }

}
