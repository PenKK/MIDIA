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

/**
 * Reads a Timeline from JSON data stored in a file.
 */
public record JsonReader(String sourcePath) {

    /**
     * Reads a timeline from the JSON file.
     *
     * @return the parsed Timeline
     * @throws IOException                if an error occurs reading data from the file
     * @throws MidiUnavailableException   if MIDI resources are unavailable
     * @throws InvalidMidiDataException   if invalid MIDI data is encountered
     */
    public Timeline read() throws IOException, MidiUnavailableException, InvalidMidiDataException {
        String jsonData = readFile(sourcePath);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseTimeline(jsonObject);
    }

    /**
     * Reads the given file path into a string.
     *
     * @param source the file path
     * @return the file contents as a string
     * @throws IOException if reading fails
     */
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(contentBuilder::append);
        }

        return contentBuilder.toString();
    }

    /**
     * Parses a Timeline from a JSON object.
     *
     * @param jsonObject the JSON object representing the timeline
     * @return the parsed Timeline
     */
    private Timeline parseTimeline(JSONObject jsonObject) {
        String projectName = jsonObject.getString("projectName");
        Timeline timeline = new Timeline(projectName, new PropertyChangeSupport(projectName));

        int beatDivision = jsonObject.getInt("beatDivision");
        int beatsPerMeasure = jsonObject.getInt("beatsPerMeasure");
        double horizontalScale = jsonObject.getDouble("horizontalScaleFactor");

        Player player = parsePlayer(jsonObject.getJSONObject("player"), timeline);

        JSONArray midiTracksJsonArray = jsonObject.getJSONArray("midiTracks");

        timeline.setPlayer(player);
        timeline.getPlayer().setBeatDivision(beatDivision);
        timeline.getPlayer().setBeatsPerMeasure(beatsPerMeasure);
        timeline.setHorizontalScaleFactor(horizontalScale);
        addMidiTracks(timeline, midiTracksJsonArray);

        timeline.setPropertyChangeSupport(null);
        return timeline;
    }

    private Player parsePlayer(JSONObject playerJson, Timeline tl) {
        Player p = new TimelinePlayer(tl);
        float beatsPerMinute = playerJson.getFloat("beatsPerMinute");
        int tickPosition = playerJson.getInt("tickPosition");

        JSONArray availableChannels = playerJson.getJSONArray("availableChannels");
        ArrayList<Integer> availableChannelsList = parseIntegerArrayList(availableChannels);

        p.setBPM(beatsPerMinute);
        p.setTickPosition(tickPosition);
        p.setAvailableChannels(availableChannelsList);

        return p;
    }

    /**
     * Converts a JSONArray of integers into an ArrayList.
     *
     * @param jsonArray the JSON array
     * @return a list of integers
     */
    private ArrayList<Integer> parseIntegerArrayList(JSONArray jsonArray) {
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getInt(i));
        }

        return list;
    }

    /**
     * Parses MidiTracks from JSON and adds them to the timeline.
     *
     * @param timeline       the timeline to receive the tracks
     * @param midiTracksJson the JSON array of tracks
     */
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

    /**
     * Parses blocks from JSON and adds them to the given MidiTrack.
     *
     * @param midiTrack       the track to receive parsed blocks
     * @param blocksJsonArray the JSON array of blocks
     */
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

    /**
     * Parses notes from JSON and adds them to the given block.
     *
     * @param block          the block to receive parsed notes
     * @param notesJsonArray the JSON array of notes
     */
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
