package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;
import persistance.JsonReader;
import persistance.JsonWriter;

// Digital Audio Workstation console based application
// This class is the user interface to interact with the MIDI timeline
// This class is partially inspired by TellerApp ui/TellerApp.java 
// https://github.students.cs.ubc.ca/CPSC210/TellerApp/blob/main/src/main/ca/ubc/cpsc210/bank/ui/TellerApp.java
// The MIDI spec was referenced heavily to interpret MIDI events: https://midi.org/spec-detail
// The MIDI instruments prompted for are from https://en.wikipedia.org/wiki/General_MIDI
public class DAW {

    private Timeline timeline;
    private Scanner sc;
    JsonReader reader;
    JsonWriter writer;

    // EFFECTS: initializes an empty timeline, sets up scanner, and runs the application
    public DAW() throws InvalidMidiDataException {
        try {
            timeline = new Timeline("New project");
            sc = new Scanner(System.in);
            appLoop();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
            System.out.println("No MIDI device avaliable, exiting");
            return;
        }
    }

    // EFFECTS: displays and handles user intereaction with program 
    @SuppressWarnings("methodlength")
    private void appLoop() throws MidiUnavailableException, InvalidMidiDataException {
        while (true) {
            displayMenuOptions();
            String[] validInputs = new String[] { "s", "l", "q", "t", "n", "d" };
            String input = getStringInput(validInputs, false);

            switch (input) {
                case "q":
                    quit();
                    return;
                case "s":
                    save();
                    break;
                case "l":
                    load();
                    timelineOptions();
                    break;
                case "n":
                    timeline = new Timeline("New project");
                    timelineOptions();
                    break;
                case "t":
                    timelineOptions();
                    break;
                case "d":
                    deleteProject();
                    break;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts user to delete a project by index
    private void deleteProject() {
        File projectsDirectory = new File("./data/projects");
        File[] projectFiles = projectsDirectory.listFiles();
        displayProjects(projectFiles);

        System.out.println("Enter the index of the project to delete, enter a non number to cancel");
        int index = getNumericalInput(1, projectFiles.length, true) - 1;
        if (index == -2) {
            return;
        }

        projectFiles[index].delete();
    }

    // EFFECTS: prints possible menu options and additional data
    private void displayMenuOptions() {
        clearConsole();
        System.out.printf("Project: %s%n", timeline.getProjectName());
        System.out.println("Open timeline          [t]");
        System.out.println("New project            [n]");
        System.out.println("Save project           [s]");
        System.out.println("Load project           [l]");
        System.out.println("Delete a saved project [d]");
        System.out.println("Quit                   [q]");
    }

    // EFFECTS: displays and handles user interaction with timeline
    private void timelineOptions() throws MidiUnavailableException {
        while (true) {
            displayTimelineOptions();
            String input = getStringInput(new String[] { "p", "t", "s", "b", "y", "r" }, false);

            switch (input) {
                case "p":
                    play();
                    break;
                case "s":
                    changeTimelinePositionSeconds();
                    break;
                case "t":
                    handleTrackOptions();
                    break;
                case "b":
                    changeTimelinePositionBeat();
                    break;
                case "y":
                    changeTimelineBPM();
                    break;
                case "r":
                    return;
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: prompts user to select a project to load by index
    private void load() throws MidiUnavailableException {
        clearConsole();
        File projectsDirectory = new File("./data/projects");
        File[] projectFiles = projectsDirectory.listFiles();

        displayProjects(projectFiles);

        System.out.println("Select an index to load");
        int index = getNumericalInput(1, projectFiles.length, false) - 1;

        reader = new JsonReader(projectFiles[index].getPath());
        try {
            System.out.println("Loading...");
            timeline = reader.read();
        } catch (IOException e) {
            System.out.println("Something went terribly wrong, unable to load project");
        } catch (InvalidMidiDataException e) {
            System.out.println("The project had invalid MIDI data! Unable to load project");
        }
    }

    // EFFECTS: prints project file names and index
    private void displayProjects(File[] projectFiles) {
        int i = 1;
        for (File projectFile : projectFiles) {
            String projectName = projectFile.getName();
            System.out.printf("[%d] %s%n", i++, projectName.substring(0, projectName.length() - 5));
        }
    }

    // MODIFIES: this
    // EFFECTS: saves timeline to file
    private void save() {
        clearConsole();
        if (timeline.getProjectName().equals("New project")) {
            saveNewProject();
            return;
        }

        System.out.printf("Would you like to save this project under a name other than %s?%n",
                timeline.getProjectName());
        System.out.printf("y/n: ");
        String response = getStringInput(new String[] { "y", "n" }, false);

        if (response.equals("y")) {
            timeline.setProjectName("New project");
            save();
        } else {
            try {
                writer = new JsonWriter("./data/projects/" + timeline.getProjectName() + ".json");
                writer.open();
                writer.write(timeline);
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("Unable to save");
                return;
            }
        }
    }

    private void saveNewProject() {
        System.out.println("Name this project\nIf the project name already exists it will be overwritten");
        String name = getStringInput(null, true);
        timeline.setProjectName(name);

        writer = new JsonWriter("./data/projects/" + name + ".json");
        try {
            writer.open();
            writer.write(timeline);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to save file, the name must not have invalid characters\n"
                    + "Press enter to continue");

            timeline.setProjectName("New project");
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
    private void changeTimelinePositionBeat() {
        System.out.println("To which beat?");
        double maxBeat = timeline.getLengthBeats() + 1;
        timeline.setPositionBeat(getNumericalInput(1, maxBeat));
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
        System.out.printf("Project Name: %s%n", timeline.getProjectName());
        System.out.printf("Length      : %.2f seconds, %.2f beats%n",
                timeline.getLengthMs() / 1000, timeline.getLengthBeats());
        System.out.printf("Position    : %.2f seconds, on beat %.2f%n",
                timeline.getPositionMs() / 1000, timeline.getPositionOnBeat());
        System.out.printf("BPM         : %.2f%n%n", timeline.getBPM());

        System.out.println("Play the project!                  [p]");
        System.out.println("Track options                      [t]");
        System.out.println("Change timeline position (seconds) [s]");
        System.out.println("Change timeline position (beat)    [b]");
        System.out.println("Change timeline BPM                [y]");
        System.out.println("Return to menu                     [r]");
    }

    // EFFECTS: prints a bunch of newline characters to clear the console
    private void clearConsole() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
    }

    // EFFECTS: Prompts user for options to modify track array
    private void handleTrackOptions() {
        String input;
        String[] validStrings = new String[] { "n", "n", "r", "d", };

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
                break;
            case "r":
                return;
        }

        handleTrackOptions();
    }

    // EFFECTS: displays possible options for a user with tracks
    private void displayTrackOptions() {
        int instrumentalTracks = 15 - timeline.getAvaliableChannels().size();
        clearConsole();
        System.out.printf("Project: %s%n", timeline.getProjectName());
        System.out.printf("Instrumental tracks: %d/15%nPercussion tracks  : %d%nTotal tracks       : %d%n%n",
                instrumentalTracks, timeline.getTracks().size() - instrumentalTracks,
                timeline.getTracks().size());
        if (timeline.getTracks().size() > 0) {
            System.out.println("Edit a track       [e]");
        }
        System.out.println("Create a new track [n]");
        System.out.println("Return to timeline [r]");
    }

    // EFFECTS: displays tracks then prompts for an index to edit
    private void selectTrack() {
        displayTracks();
        System.out.println("Enter the index of the track to edit");
        editTrack(getNumericalInput(1, timeline.getTracks().size(), false) - 1);
    }

    // MODIFIES: this
    // EFFECTS: creates new track in the sequence, and prompts for a track name
    //          and instrument, returns the index of the created track
    private int createNewTrack() {
        System.out.println("Enter a name for the track");
        String name = getStringInput(null, true);

        boolean percussive;

        if (timeline.getAvaliableChannels().size() == 0) {
            System.out.println("\nMaximum instrumental tracks, track will be percussive");
            percussive = true;
        } else {
            System.out.println("Is this track percussive? t/f");
            percussive = getStringInput(new String[] { "t", "f" }, false).equals("t");
        }

        System.out.println(
                "What instrument does this track play?\n(see program change events https://en.wikipedia.org/wiki/General_MIDI)");
        int instrument = percussive ? getNumericalInput(35, 81, false) : (getNumericalInput(1, 128, false) - 1);
        MidiTrack newMidiTrack = timeline.createMidiTrack(name, instrument, percussive);
        return timeline.getTracks().indexOf(newMidiTrack);
    }

    // REQUIRES: index >= 0 and for there to be at least 1 track in the timeline
    // EFFECTS: Prompts the user to edit a track by giving an index
    @SuppressWarnings("methodlength")
    private void editTrack(int index) {
        String[] validInputs = { "n", "i", "v", "m", "n", "r", "d", "c" };
        MidiTrack selectedTrack = timeline.getTrack(index);
        displayEditTrackOptions(validInputs, selectedTrack);
        if (selectedTrack.getBlocks().size() > 0) {
            validInputs[0] = "e";
        }
        String input = getStringInput(validInputs, false);

        switch (input) {
            case "c":
                changeTrackName(selectedTrack);
                break;
            case "n":
                editBlock(createNewBlock(selectedTrack), selectedTrack);
                break;
            case "e":
                chooseBlock(selectedTrack);
                break;
            case "i":
                changeTrackInstrument(selectedTrack);
                break;
            case "v":
                changeTrackVolume(selectedTrack);
                break;
            case "d":
                timeline.removeMidiTrack(index);
                return;
            case "m":
                selectedTrack.setMuted(!selectedTrack.isMuted());
                break;
            case "r":
                return;
            default:
                break;
        }

        editTrack(index);
    }

    // MODFIES: selectedTrack
    // EFFECTS: prompts user for new volume in 0 to 100 and applies to to selectedTrack
    private void changeTrackVolume(MidiTrack selectedTrack) {
        System.out.println("Enter new volume");
        int volume = getNumericalInput(0, 100, false);
        int volumeScaled = (int) Math.round(volume * 1.27);
        selectedTrack.setVolume(volumeScaled);
    }

    // MODIFIES: selectedTrack
    // EFFECTS: prompts user for a new name for the selected track and applies it
    private void changeTrackName(MidiTrack selectedTrack) {
        System.out.println("Enter a new name");
        selectedTrack.setName(getStringInput(null, true));
    }

    // MODIFIES: midiTrack
    // EFFECTS: Prompts for input to change a tracks instrument, gives different ranges depending on
    //          if the track is percussive.
    private void changeTrackInstrument(MidiTrack midiTrack) {
        System.out.printf("What is the new instrument for track %s?%n", midiTrack.getName());
        System.out.println("(see program change events https://en.wikipedia.org/wiki/General_MIDI)");

        int instrument = midiTrack.isPercussive() ? getNumericalInput(35, 81, false) : getNumericalInput(1, 128, false) - 1;
        midiTrack.setInstrument(instrument);
    }

    // EFFECTS: prompts user to select a block by index to edit
    private void chooseBlock(MidiTrack selectedTrack) {
        displayBlocks(selectedTrack);
        System.out.println("Select an index");
        editBlock(getNumericalInput(1, selectedTrack.getBlocks().size(), false) - 1, selectedTrack);
    }

    // EFFECTS: prints possible options for user to edit a track and other track info
    private void displayEditTrackOptions(String[] validInputs, MidiTrack selectedTrack) {
        clearConsole();

        System.out.printf("Track             %s%n", selectedTrack.getName());
        System.out.printf("Number of blocks  %d%n", selectedTrack.getBlocks().size());
        System.out.printf("Instrument        %d%s%n",
                selectedTrack.getInstrument() + (selectedTrack.isPercussive() ? 0 : 1),
                selectedTrack.isPercussive() ? " (Percussive)" : "");
        System.out.printf("Volume            %d%n", selectedTrack.getVolumeScaled());
        System.out.printf("Muted             %s%n%n", selectedTrack.isMuted() ? "Yes" : "No");

        System.out.println("Create new block   [n]");
        if (selectedTrack.getBlocks().size() > 0) {
            System.out.println("Edit blocks        [e]");
        }
        System.out.println("Change name        [c]");
        System.out.println("Change instrument  [i]");
        System.out.println("Change volume      [v]");
        System.out.println("Toggle mute        [m]");
        System.out.println("Delete this track  [d]");
        System.out.println("Return             [r]");
    }

    // EFFECTS: Displays tracks and their indexes
    private void displayTracks() {
        clearConsole();
        ArrayList<MidiTrack> tracks = timeline.getTracks();
        for (int i = 0; i < timeline.getTracks().size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, tracks.get(i).getName());
        }
    }

    // MODIFIES: midiTrack
    // EFFECTS: creates new block in a track, and prompts for a start tick, and returns the index it was created at
    private int createNewBlock(MidiTrack midiTrack) {
        System.out.println("At what beat does this block start?");
        double startBeat = getNumericalInput(1, Double.MAX_VALUE) - 1;
        midiTrack.addBlock(new Block(timeline.beatsToTicks(startBeat)));
        return midiTrack.getBlocks().size() - 1;
    }

    // REQUIRES: index >= 0 and the midiTrack has to have at least 1 block
    // MODIFIES: midiTrack, this
    // EFFECTS: selects the indexed block
    private void editBlock(int index, MidiTrack midiTrack) {
        Block selectedBlock = midiTrack.getBlock(index);
        String[] validStrings = new String[] { "n", "n", "d", "r", "o" };

        displayEditBlockOptions(index, selectedBlock, validStrings);

        String input = getStringInput(validStrings, false);

        switch (input) {
            case "n":
                createNewNote(selectedBlock, midiTrack.isPercussive());
                break;
            case "d":
                midiTrack.removeBlock(index);
                return;
            case "e":
                chooseNoteToEdit(selectedBlock, midiTrack.isPercussive());
                break;
            case "o":
                selectedBlock.setStartTick(timeline.beatsToTicks(getNumericalInput(1, Double.MAX_VALUE) - 1));
                break;
            case "r":
                return;
            default:
                break;
        }

        editBlock(index, midiTrack);
    }

    // EFFECTS: prints block information and possible options
    private void displayEditBlockOptions(int index, Block selectedBlock, String[] validStrings) {
        clearConsole();
        System.out.printf("You are editing block %d%n", index + 1);
        System.out.printf("The block starts at %.2f seconds, on beat %.2f%n",
                timeline.ticksToMs(selectedBlock.getStartTick()) / 1000,
                timeline.ticksToOnBeat(selectedBlock.getStartTick()));
        System.out.printf("Add a note            [n]%n", index + 1);
        if (selectedBlock.getNotes().size() > 0) {
            System.out.printf("Edit a note           [e]%n", index + 1);
            validStrings[0] = "e";
        }
        System.out.printf("Change block on beat  [o]%n", index + 1);
        System.out.printf("Delete this block     [d]%n", index + 1);
        System.out.printf("Return                [r]%n");
    }

    // EFFECTS: Prompts user to select a note from the given table of notes
    private void chooseNoteToEdit(Block selectedBlock, boolean percussive) {
        displayNotes(selectedBlock.getNotes(), percussive);
        System.out.println("Enter an index");
        int displayIndex = getNumericalInput(1, selectedBlock.getNotes().size(), false);
        editNote(selectedBlock, displayIndex, percussive);
    }

    // REQUIRES: displayIndex > 0, must be at least 1 note in the block
    // MODIFIES: block
    // EFFECTS: prompts user for input to edit the note in a block
    @SuppressWarnings("methodlength")
    private void editNote(Block block, int displayIndex, boolean percussive) {
        Note note = block.getNotes().get(displayIndex - 1);
        displayEditNoteOptions(note, displayIndex, percussive);
        String[] validStrings = new String[] { "p", "v", "o", "d", "r", "b"};

        if (percussive) {
            validStrings[0] = "v";
        }

        String input = getStringInput(validStrings, false);

        switch (input) {
            case "p":
                note.setPitch(getNumericalInput(0, 127, false));
                break;
            case "v":
                note.setVelocity(getNumericalInput(0, 127, false));
                break;
            case "o":
                note.setStartTick(timeline.beatsToTicks(getNumericalInput(1, Double.MAX_VALUE) - 1));
                break;
            case "b":
                note.setDurationTicks(timeline.beatsToTicks(getNumericalInput(0, Double.MAX_VALUE)));
                break;
            case "d":
                block.removeNote(displayIndex - 1);
                return;
            case "r":
                return;
        }

        chooseNoteToEdit(block, percussive);
    }

    // EFFECTS: prints possible options in editing a note and other note info
    private void displayEditNoteOptions(Note note, int displayIndex, boolean percussive) {
        clearConsole();
        System.out.printf("Note index      : %d%n", displayIndex);
        if (!percussive) {
            System.out.printf("Pitch           : %d%n", note.getPitch());
        }
        System.out.printf("Velocity        : %d%n", note.getVelocity());
        System.out.printf("On beat         : %.2f%n", timeline.ticksToOnBeat(note.getStartTick()));
        System.out.printf("Off beat        : %.2f%n", timeline.ticksToOnBeat(note.getStartTick()
                + note.getDurationTicks()));
        System.out.printf("Duration beats  : %.2f%n%n", timeline.ticksToBeats(note.getDurationTicks()));

        System.out.println("What would you like to do?");
        if (!percussive) {
            System.out.println("Change pitch           [p]");
        }
        System.out.println("Change velocity        [v]");
        System.out.println("Change on beat         [o]");
        System.out.println("Change duration beats  [b]");
        System.out.println("Delete this note       [d]");
        System.out.println("Return                 [r]");
    }

    // EFFECTS: prints notes out in a table according to if they are percussive
    private void displayNotes(ArrayList<Note> notes, boolean percussive) {
        clearConsole();
        if (percussive) {
            displayPercussiveNotes(notes);
        } else {
            displayInstrumentalNotes(notes);
        }
    }

    // EFFECTS: prints out all note data in table format
    private void displayInstrumentalNotes(ArrayList<Note> notes) {
        System.out.printf("%-12s%-12s%-12s%-12s%-12s%-12s%n",
                "Index", "Pitch", "Velocity", "On beat", "Off beat", "Duration beats");
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            System.out.printf("%-12d%-12d%-12d%-12.2f%-12.2f%-12.2f%n",
                    i + 1,
                    note.getPitch(),
                    note.getVelocity(),
                    timeline.ticksToOnBeat(note.getStartTick()),
                    timeline.ticksToOnBeat(note.getStartTick() + note.getDurationTicks()),
                    timeline.ticksToBeats(note.getDurationTicks()));
        }
    }

    // EFFECTS: prints out all note data excluding pitch in table format
    private void displayPercussiveNotes(ArrayList<Note> notes) {
        System.out.printf("%-12s%-12s%-12s%-12s%-12s%n",
                "Index", "Velocity", "On beat", "Off beat", "Duration beats");
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            System.out.printf("%-12d%-12d%-12.2f%-12.2f%-12.2f%n",
                    i + 1,
                    note.getVelocity(),
                    timeline.ticksToOnBeat(note.getStartTick()),
                    timeline.ticksToOnBeat(note.getStartTick() + note.getDurationTicks()),
                    timeline.ticksToBeats(note.getDurationTicks()));
        }
    }

    // MODIFIES: block
    // EFFECTS: creates new note in a block, prompts for the appropriate parameters, 
    //          returns the created notes index
    private void createNewNote(Block block, boolean percussive) {
        int pitch = 0;

        if (!percussive) {
            System.out.println("Whats the note pitch?");
            System.out.println("(See note table on https://studiocode.dev/resources/midi-middle-c/)");
            pitch = getNumericalInput(0, 127, false);
        }

        System.out.println("Whats the note velocity?");
        int velocity = getNumericalInput(0, 127, false);

        System.out.println("What beat does the note start on? (relative to this block)");
        double startBeat = getNumericalInput(1, Double.MAX_VALUE) - 1;

        System.out.println("How many beats long is the note?");
        double durationBeats = getNumericalInput(0, Double.MAX_VALUE);

        block.addNote(new Note(pitch, velocity, timeline.beatsToTicks(startBeat),
                timeline.beatsToTicks(durationBeats)));
    }

    // EFFECTS: Displays tracks and their indexes
    private void displayBlocks(MidiTrack midiTrack) {
        clearConsole();
        for (int i = 0; i < midiTrack.getBlocks().size(); i++) {
            Block b = midiTrack.getBlock(i);
            System.out.printf("[%d] Note count: %d       On beat: %.2f %n",
                    i + 1, b.getNotes().size(),
                    timeline.ticksToOnBeat(b.getStartTick()));
        }
    }

    // REQUIRES: min <= max
    // EFFECTS: prompts the user for integer input in a range and returns it.
    //          if returnFail is true then -1 will be returned on a non numeric input instead of reprompting
    private int getNumericalInput(int min, int max, boolean returnFail) {
        while (true) {
            int input;
            System.out.printf("Input in range [%d, %d]: ", min, max);

            try {
                input = sc.nextInt();
            } catch (InputMismatchException e) {
                sc.nextLine();
                if (returnFail) {
                    return -1;
                }

                System.out.println("Enter an integer!");
                continue;
            }

            if (input < min || input > max) {
                System.out.println("Input out of range!");
                sc.nextLine();
                continue;
            }

            sc.nextLine();
            return input;
        }
    }

    // REQUIRES: min <= max
    // EFFECTS: prompts the user for double input in a range and returns it
    private double getNumericalInput(double min, double max) {
        while (true) {
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
                continue;
            }

            if (input < min || input > max) {
                System.out.println("Input out of range!");
                sc.nextLine();
                continue;
            }

            sc.nextLine();
            return input;
        }
    }

    // REQUIRES: if acceptsAnyString == false, acceptedStrings != null.
    // EFFECTS: takes input for a string value. If acceptsAnyString is true accepted strings will be ingnored
    private String getStringInput(String[] acceptedStrings, boolean acceptsAnyString) {
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
