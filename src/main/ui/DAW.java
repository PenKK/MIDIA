package ui;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;

// Digital Audio Workstation application
// This class is partially inspired by TellerApp ui/TellerApp.java 
// https://github.students.cs.ubc.ca/CPSC210/TellerApp/blob/main/src/main/ca/ubc/cpsc210/bank/ui/TellerApp.java
//
public class DAW {

    private Timeline timeline;
    private Scanner sc;

    // EFFECTS: initializes an empty timeline, sets up scanner, and runs the application
    public DAW() throws InvalidMidiDataException {
        try {
            timeline = new Timeline();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.out.println("No MIDI device avaliable, exiting");
            return;
        }
        sc = new Scanner(System.in);
        appLoop();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    @SuppressWarnings("methodlength")
    public void appLoop() {
        while (true) {
            displayTimelineOptions();
            String input = getStringInput(new String[] { "p", "t", "q", "c", "b", "s" }, false);

            switch (input) {
                case "p":
                    play();
                    break;
                case "c":
                    changeTimelinePositionSeconds();
                    break;
                case "t":
                    handleTrackOptions();
                    break;
                case "q":
                    quit();
                    return;
                case "b":
                    changeTimelinePositionBeats();
                    break;
                case "s":
                    changeTimelineBPM();
                    break;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: changes the BPM of the timeline to the prompted input
    private void changeTimelineBPM() {
        System.out.println("What is the new BPM?");
        double input = getNumericalInput(1, 1200);
        timeline.setBPM((float) input);
    }

    // MODIFIES: this
    // EFFECTS: system resources are let free, allowing for safe closure
    private void quit() {
        System.out.println("Exiting");
        timeline.getSequencer().close();
        sc.close();
    }

    // MODIFIES: this
    // EFFECTS: moves the timeline position to prompted beat
    private void changeTimelinePositionBeats() {
        System.out.println("To which beat?");
        timeline.setPositionBeats(getNumericalInput(0, timeline.getLengthBeats()));
    }

    // MODIFIES: this
    // EFFECTS: moves the timeline position to prompted time
    private void changeTimelinePositionSeconds() {
        System.out.println("To how many seconds?");
        timeline.setPositionMs(getNumericalInput(0, timeline.getLengthMs() / (double) 1000) * 1000);
    }

    // MODIFIES: this
    // EFFECTS: Starts playback
    private void play() {
        clearConsole();
        System.out.println("Playing... press enter to stop and exit");
        try {
            timeline.play();
        } catch (InvalidMidiDataException e) {
            System.out.println("Invalid MIDI data found in sequence, playback may not work");
        }

        sc.nextLine();
        timeline.pause();
    }

    // EFFECTS: displays timeline options
    private void displayTimelineOptions() {
        clearConsole();
        System.out.println("Welcome to the project timeline!");
        System.out.printf("Length  : %.2f seconds, %.2f beats%n", timeline.getLengthMs() / 1000, timeline.getLengthBeats());
        System.out.printf("Position: %.2f seconds, %.2f beats%n", timeline.getPositionMs() / 1000, timeline.getPositionBeats());
        System.out.printf("BPM: %.2f%n%n", timeline.getBPM());

        System.out.println("Play the project!                  [p]");
        System.out.println("Track options                      [t]");
        System.out.println("Change timeline position (seconds) [c]");
        System.out.println("Change timeline position (beats)   [b]");
        System.out.println("Change timeline BPM                [s]");
        System.out.println("Quit                               [q]");
    }

    // EFFECTS: prints a bunch of newline characters to clear the console
    private void clearConsole() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    // MODIFES: this
    // EFFECTS: Processes track options by
    public void handleTrackOptions() {
        String input;
        String[] validStrings = new String[] { "n", "n", "r", "d" };

        if (timeline.getTracks().size() > 0) {
            validStrings[0] = "e";
        }

        displayTrackOptions();

        input = getStringInput(validStrings, false);

        switch (input) {
            case "e":
                selectTrack();
                break;
            case "n":
                editTrack(createNewTrack());
            case "r":
                return;
        }
    }

    private void displayTrackOptions() {
        clearConsole();
        System.out.printf("Current number of tracks: %d, what would you like to do?%n",
                timeline.getTracks().size());
        if (timeline.getTracks().size() > 0) {
            System.out.println("Edit a track       [e]");
        }
        System.out.println("Create a new track [n]");
        System.out.println("Return to timeline [r]");
    }

    private void selectTrack() {
        displayTracks();
        System.out.println("Enter the index of the track to edit");
        editTrack(getNumericalInput(1, timeline.getTracks().size()) - 1);
    }

    // MODIFIES: this
    // EFFECTS: creates new track in the sequence, and prompts for a track name
    //          and instrument, returns the index of the created track
    public int createNewTrack() {
        System.out.println("Enter a name for the track");
        String name = getStringInput(null, true);

        System.out.println("Is this track percussive? t/f");
        boolean percussive = getStringInput(new String[] { "t", "f" }, false).equals("t");

        System.out.println(
                "What instrument does this track play?\n(see program change events https://en.wikipedia.org/wiki/General_MIDI)");
        int instrument = percussive ? getNumericalInput(35, 81) : getNumericalInput(0, 127);

        return timeline.addMidiTrack(new MidiTrack(name, instrument, percussive));
    }

    // REQUIRES: index >= 0 and for there to be at least 1 track in the timeline
    // EFFECTS: Prompts the user to edit a track by giving an index
    public void editTrack(int index) {
        String[] validInputs = { "n", "b", "i", "v", "m", "n", "r", "d" };
        MidiTrack selectedTrack = timeline.getTrack(index);
        displayEditTrackOptions(validInputs, selectedTrack);
        validInputs[0] = "e";

        String input = getStringInput(validInputs, false);

        switch (input) {
            case "n":
                System.out.println("Enter a new name");
                selectedTrack.setName(getStringInput(null, true));
                break;
            case "b":
                editBlock(createNewBlock(selectedTrack), selectedTrack);
                break;
            case "e":
                chooseBlock(selectedTrack);
                break;
            case "r":
                return;
            default:
                break;
        }

        editTrack(index);
    }

    private void chooseBlock(MidiTrack selectedTrack) {
        displayBlocks(selectedTrack);
        System.out.println("Select an index");
        editBlock(getNumericalInput(1, selectedTrack.getBlocks().size()) - 1, selectedTrack);
    }

    private void displayEditTrackOptions(String[] validInputs, MidiTrack selectedTrack) {
        clearConsole();

        System.out.printf("Track             %s%n", selectedTrack.getName());
        System.out.printf("Number of blocks  %d%n", selectedTrack.getBlocks().size());
        System.out.printf("Instrument        %d%s%n", selectedTrack.getInstrument(),
                selectedTrack.isPercussive() ? " (Percussive)" : "");
        System.out.printf("Volume            %d%n", selectedTrack.getVolume());
        System.out.printf("Muted             %s%n%n", selectedTrack.isMuted() ? "Yes" : "No");

        System.out.println("Change name        [n]");
        System.out.println("Create new block   [b]");
        if (selectedTrack.getBlocks().size() > 0) {
            System.out.println("Edit blocks        [e]");
        }

        System.out.println("Change instrument  [i]");
        System.out.println("Change volume      [v]");
        System.out.println("Toggle mute        [m]");
        System.out.println("Delete this track  [d]");
        System.out.println("Return             [r]");
    }

    // EFFECTS: Displays tracks and their indexes
    public void displayTracks() {
        System.out.println("Here are the tracks");
        ArrayList<MidiTrack> tracks = timeline.getTracks();
        for (int i = 0; i < timeline.getTracks().size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, tracks.get(i).getName());
        }
    }

    // MODIFIES: this
    // EFFECTS : removes the given track by index from the sequence
    public void deleteTrack(int index) {
        //stub
    }

    // MODIFIES: this, midiTrack
    // EFFECTS: creates new block in a track, and prompts for a start tick, and returns the index it was created at
    public int createNewBlock(MidiTrack midiTrack) {
        System.out.println("At what beat does this block start?");
        double startBeat = getNumericalInput(0, Double.MAX_VALUE);
        midiTrack.addBlock(new Block(timeline.beatsToTicks(startBeat)));
        return midiTrack.getBlocks().size() - 1;
    }

    // REQUIRES: index >= 0 and the midiTrack has to have at least 1 block
    // MODIFIES: midiTrack, this
    // EFFECTS: selects the indexed block
    public void editBlock(int index, MidiTrack midiTrack) {
        Block selectedBlock = midiTrack.getBlock(index);
        String[] validStrings = new String[] { "n", "n", "d", "r" };

        displayEditBlockOptions(index, selectedBlock, validStrings);

        String input = getStringInput(validStrings, false);

        switch (input) {
            case "n":
                createNewNote(selectedBlock);
                break;
            case "d":
                midiTrack.removeBlock(index);
                break;
            case "e":
                chooseNote(selectedBlock);
                break;
            case "r":
                return;
            default:
                break;
        }

        editBlock(index, midiTrack);
    }

    // EFFECTS: Prompts user to select a note from the given table of notes
    private void chooseNote(Block selectedBlock) {
        displayNotes(selectedBlock.getNotes());
        System.out.println("Enter an index");
        int index = getNumericalInput(1, selectedBlock.getNotes().size());
    }

    private void displayEditBlockOptions(int index, Block selectedBlock, String[] validStrings) {
        clearConsole();
        System.out.printf("You are editing block %d%n", index + 1);
        System.out.printf("The block starts at %.2f seconds, on beat %.2f%n",
                timeline.ticksToMs(selectedBlock.getStartTick()) / 1000,
                timeline.ticksToBeats(selectedBlock.getStartTick()));
        System.out.printf("Add a note         [n]%n", index + 1);
        if (selectedBlock.getNotes().size() > 0) {
            System.out.printf("Edit a note        [e]%n", index + 1);
            validStrings[0] = "e";
        }
        System.out.printf("Delete this block  [d]%n", index + 1);
        System.out.printf("Return             [r]%n");
    }

    // EFFECTS: creates a table of the given notes and their relevant fields
    public void displayNotes(ArrayList<Note> notes) {
        clearConsole();
        System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s%n", "Index", "Pitch", "Velocity", "Start", "End", "Duration");
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            System.out.printf("%-12d%-12d%-12d%-12.2f%-12.2f%-12.2f%n", i + 1, note.getPitch(),
                    note.getVelocity(), timeline.ticksToBeats(note.getStartTick()),
                    timeline.ticksToBeats(note.getDurationTicks() + note.getStartTick()),
                    timeline.ticksToBeats(note.getDurationTicks()));
        }
    }

    // MODIFIES: block
    // EFFECTS: creates new note in a block, prompts for the appropriate parameters, 
    //          returns the created notes index
    public void createNewNote(Block block) {
        System.out.println("Whats the note pitch?");
        System.out.println("(See https://inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies)");
        int pitch = getNumericalInput(0, 127);

        System.out.println("Whats the note velocity?");
        int velocity = getNumericalInput(0, 127);

        System.out.println("What beat does the note start on? (relative to this block)");
        double startBeat = getNumericalInput(0, Double.MAX_VALUE);

        System.out.println("How many beats long is the note?");
        double durationBeats = getNumericalInput(0, Double.MAX_VALUE);

        block.addNote(new Note(pitch, velocity, timeline.beatsToTicks(startBeat),
                timeline.beatsToTicks(durationBeats)));
    }

    // EFFECTS: Displays tracks and their indexes
    public void displayBlocks(MidiTrack midiTrack) {
        clearConsole();
        System.out.println("All blocks and the number of notes in each one:");
        for (int i = 0; i < midiTrack.getBlocks().size(); i++) {
            System.out.printf("[%d] Note count: %d%n", i + 1, midiTrack.getBlock(i).getNotes().size());
        }
    }

    // MODIFIES: this, block
    // EFFECTS: moves the specified blocks start time to the prompted user input
    public void moveBlockStartTime(Block block) {
        // stub        
    }

    // MODIFIES: track
    // EFFECTS : removes the given block by index from the track
    public void deleteBlock(MidiTrack track, int index) {
        // stub
    }

    // MODIFIES: block
    // EFFECTS: prompts the user to modify (and pottenally delete) a notes pitch, velocity, startTick, 
    //          and duration by prompted index
    public void selectNote(Note block) {
        // stub
    }

    // EFFECTS: prompts the user for integer input in a range where min <= max
    public int getNumericalInput(int min, int max) {
        int input;
        System.out.printf("Input in range [%d, %d]: ", min, max);

        try {
            input = sc.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Enter an integer!");
            sc.nextLine();
            return getNumericalInput(min, max);
        }

        if (input < min || input > max) {
            System.out.println("Input out of range!");
            sc.nextLine();
            return getNumericalInput(min, max);
        }

        sc.nextLine();
        return input;
    }

    // EFFECTS: prompts the user for double input in a range where min <= max
    public double getNumericalInput(double min, double max) {
        double input;
        if (max == Double.MAX_VALUE) {
            System.out.printf("Input in range [%.2f, infinity]: ", min, max);
        } else {
            System.out.printf("Input in range [%.2f, %.2f]: ", min, max);
        }

        try {
            input = sc.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Enter an integer!");
            sc.nextLine();
            return getNumericalInput(min, max);
        }

        if (input < min || input > max) {
            System.out.println("Input out of range!");
            sc.nextLine();
            return getNumericalInput(min, max);
        }
        sc.nextLine();
        return input;
    }

    // EFFECTS: takes input for a string value. If acceptsAnyString is true accepted strings will be ingnored
    public String getStringInput(String[] acceptedStrings, boolean acceptsAnyString) {
        String input;
        while (true) {
            System.out.printf("Input: ");
            input = sc.nextLine();

            if (acceptsAnyString) {
                if (input.equals("")) {
                    System.out.println("Enter a non empty input!");
                    continue;
                }
                return input;
            }

            for (int i = 0; i < acceptedStrings.length; i++) {
                if (input.equals(acceptedStrings[i])) {
                    return input;
                }
            }


            System.out.println("Invalid input! Choose a valid option.");
        }
    }
}
