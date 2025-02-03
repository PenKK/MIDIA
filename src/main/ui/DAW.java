package ui;

import model.Block;
import model.MidiTrack;
import model.Note;

// Digital Audio Workstation application
// This class is partially inspired by TellerApp ui/TellerApp.java 
// https://github.students.cs.ubc.ca/CPSC210/TellerApp/blob/main/src/main/ca/ubc/cpsc210/bank/ui/TellerApp.java
public class DAW {
    // EFFECTS: initializes an empty project/sequencer and runs the application
    public DAW() {
        // stub 
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    public void appLoop() {
        // stub
    }

    // EFFECTS: Displays options of possible actions/commands for user in the timeline, and processes them
    public void handleTimelineOptions() {
        // stub
    }

    // MODIFIES: this
    // EFFECTS: creates new track in the sequence, and prompts for a track name
    //          and instrument, returns the index of the created track
    public int createNewTrack() {
        return 0; // stub
    }

    // EFFECTS: Prompts the user to select a track by giving an index
    public void selectTrack(int index) {

    }

    // EFFECTS: Processes track options by
    public void handleTrackOptions() {
        // stub
    }

    // EFFECTS: Displays tracks and their indexes
    public void displayTracks() {
        // stub
    }

    // MODIFIES: track
    // EFFECTS : removes the given track by index from the sequence
    public void deleteTrack(int index) {
        //stub
    }

    // MODIFIES: this
    // EFFECTS: creates new block in a track, and prompts for a start tick, and returns the index it was created at
    public int createNewBlock(MidiTrack track) {
        return 0;
    }

    // EFFECTS: selects the indexed block
    public void selectBlock(int index) {

    }

    // EFFECTS: Displays options of possible actions/commands for user in a block
    public void handleBlockOptions() {
        // stub
    }

    // EFFECTS: Displays tracks and their indexes
    public void displayBlocks() {
        // stub
    }

    // MODIFIES: block
    // EFFECTS: moves the specified blocks start time to the prompted user input
    public void moveBlockStartTime(Block block) {
        // stub        
    }

    // MODIFIES: track
    // EFFECTS : removes the given block by index from the track
    public void deleteBlock(MidiTrack track, int index) {
        //stub
    }

    // MODIFIES: block
    // EFFECTS: creates new note in a block, prompts for the appropriate parameters, 
    //          returns the created notes index
    public int createNewNote(Block block, int pitch, int velocity, long startTick, long durationTicks) {
        return 0; //stub
    }

    // MODIFIES: block
    // EFFECTS: prompts the user to modify (and pottenally delete) a notes pitch, velocity, startTick, 
    //          and duration by prompted index
    public void selectNote(Note block) {

    }
}
