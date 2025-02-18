package persistance;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.json.JSONObject;

import model.Block;
import model.MidiTrack;
import model.Timeline;

// Represents a reader that reads Timeline from JSON data stored in file
// Code adapted from src/main/persistance/JsonReader
//     at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReader {
    private String sourcePath;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        // stub
    }

    // EFFECTS: reads workroom from file and returns it;
    // throws IOException if an error occurs reading data from file
    public Timeline read() throws IOException, MidiUnavailableException, InvalidMidiDataException {
        return new Timeline(null); // stub
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        return null; // stub
    }

    // EFFECTS: parses workroom from JSON object and returns it
    private Timeline parseTimeline(JSONObject jsonObject) throws MidiUnavailableException, InvalidMidiDataException {
        return new Timeline(null); // stub
    }

    // MODIFIES: timeline
    // EFFECTS: parses MidiTracks and adds them to timeline
    private void addMidiTracks(Timeline timeline) {
        // stub
    }

    // MOFIES: midiTrack
    // EFFECTS: parses blocks and adds them to midiTrack
    private void addBlocks(MidiTrack midiTrack) {
        // stub
    }

    // MODIFIES: block
    // EFFECTS: parses notes and adds them to block
    private void addNotes(Block block) {
        // stub
    }
}
