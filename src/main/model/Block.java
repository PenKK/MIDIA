package model;

import java.util.ArrayList;

// A block exists in a track and is a group of notes.
// The block can be moved on the timeline by changing the startTick.
public class Block {

    private ArrayList<Note> notes;
    private int startTick;

    // REQUIRES: startTick >= 0
    // EFFECTS: Creates a block with no notes inside of it with a startTick
    public Block(int startTick) {
        this.notes = new ArrayList<>();
        this.startTick = startTick;
    }
    
    // MODIFIES: this
    // EFFECTS: Adds the note to the list of notes in this block and adjusts lengthTick if neccessary, 
    //          returns created notes index
    public int addNote(Note note) {
        notes.add(note);
        return notes.size() - 1;
    }

    // REQUIRES: 0 <= index <= notes.size() - 1
    // MODIFIES: this
    // EFFECTS: Removes the specified index note from notes and returns it
    public Note removeNote(int index) {
        return notes.remove(index);
    }

    // REQUIRES: newStartTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes the position of the block on the time line
    public void setStartTick(int newStartTick) {
        startTick = newStartTick;
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

    public int getStartTick() {
        return startTick;
    }

    // EFFECTS: Returns a clone of this block with its own unique memory address
    @Override
    public Block clone() {
        Block cloneBlock = new Block(startTick);
        for (Note note : notes) {
            cloneBlock.addNote(note.clone());
        }
        return cloneBlock;
    }

}
