package persistance;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.json.JSONArray;
import org.json.JSONObject;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Player;
import model.Timeline;
import model.TimelinePlayer;
import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;

// Represents a reader that reads Timeline from JSON data stored in file
// Code adapted from src/main/persistance/JsonReader
//     at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonReader {
    private String sourcePath;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.sourcePath = source;
    }

    // EFFECTS: reads timeline from json file and returns it;
    // throws IOException if an error occurs reading data from file
    public Timeline read() throws IOException, MidiUnavailableException, InvalidMidiDataException {
        String jsonData = readFile(sourcePath);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseTimeline(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses timeline from JSON object and returns it
    private Timeline parseTimeline(JSONObject jsonObject) throws MidiUnavailableException, InvalidMidiDataException {
        String projectName = jsonObject.getString("projectName");
        Timeline timeline = new Timeline(projectName, new PropertyChangeSupport(projectName));

        int beatDivision = jsonObject.getInt("beatDivision");
        int beatsPerMeasure = jsonObject.getInt("beatsPerMeasure");
        double horizontalScale = jsonObject.getDouble("horizontalScaleFactor");

        Player player = parsePlayer(jsonObject.getJSONObject("player"), timeline);

        JSONArray midiTracksJsonArray = jsonObject.getJSONArray("midiTracks");

        timeline.setPlayer(player);
        timeline.setBeatDivision(beatDivision);
        timeline.setBeatsPerMeasure(beatsPerMeasure);
        timeline.setHorizontalScaleFactor(horizontalScale);
        addMidiTracks(timeline, midiTracksJsonArray);

        timeline.setPropertyChangeSupport(null);
        return timeline;
    }

    private Player parsePlayer(JSONObject playerJson, Timeline tl) {
        Player p = new TimelinePlayer(tl);
        float beatsPerMinute = playerJson.getFloat("beatsPerMinute");
        int positionTick = playerJson.getInt("positionTick");

        JSONArray availableChannels = playerJson.getJSONArray("availableChannels");
        ArrayList<Integer> availableChannelsList = parseIntegerArrayList(availableChannels);

        p.setBPM(beatsPerMinute);
        p.setPositionTick(positionTick);
        p.setAvailableChannels(availableChannelsList);

        return p;
    }

    // EFFECTS: returns the JSONArray as an ArrayList
    private ArrayList<Integer> parseIntegerArrayList(JSONArray jsonArray) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getInt(i));
        }

        return list;
    }

    // MODIFIES: timeline
    // EFFECTS: parses MidiTracks and adds them to timeline
    private void addMidiTracks(Timeline timeline, JSONArray midiTracksJson) {
        for (Object midiTrackJson : midiTracksJson) {
            JSONObject midiTrackData = (JSONObject) midiTrackJson;
            String name = midiTrackData.getString("name");
            int channel = midiTrackData.getInt("channel");
            Instrument instrument = parseInstrument(midiTrackData.getJSONObject("instrument"));
            int volume = midiTrackData.getInt("volume");

            MidiTrack currentMidiTrack = new MidiTrack(name, instrument, channel);
            currentMidiTrack.setVolume(volume);

            JSONArray blocksJsonArray = midiTrackData.getJSONArray("blocks");
            addBlocks(currentMidiTrack, blocksJsonArray);

            timeline.addMidiTrack(currentMidiTrack);
        }
    }

    private Instrument parseInstrument(JSONObject instrumentJsonObject) {
        String className = instrumentJsonObject.getString("type");
        String name = instrumentJsonObject.getString("name");

        if (className.equals("tonal")) {
            return TonalInstrument.valueOf(name);
        } else {
            return PercussiveInstrument.valueOf(name);
        }
    }

    // MOFIES: midiTrack
    // EFFECTS: parses blocks and adds them to midiTrack
    private void addBlocks(MidiTrack midiTrack, JSONArray blocksJsonArray) {
        for (Object blockJson : blocksJsonArray) {
            JSONObject blockData = (JSONObject) blockJson;

            long startTick = blockData.getLong("startTick");
            long durationTicks = blockData.getLong("durationTicks");
            Block currentBlock = new Block(startTick, durationTicks);

            JSONArray notesJsonArray = blockData.getJSONArray("notes");
            addNotes(currentBlock, notesJsonArray);
            midiTrack.addBlock(currentBlock);
        }
    }

    // MODIFIES: block
    // EFFECTS: parses notes and adds them to block
    private void addNotes(Block block, JSONArray notesJsonArray) {
        for (Object noteJson : notesJsonArray) {
            JSONObject noteData = (JSONObject) noteJson;

            int pitch = noteData.getInt("pitch");
            int velocity = noteData.getInt("velocity");
            long startTick = noteData.getLong("startTick");
            long durationTicks = noteData.getLong("durationTicks");

            Note currentNote = new Note(pitch, velocity, startTick, durationTicks);
            block.addNote(currentNote);
        }
    }
}
