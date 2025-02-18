package persistance;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.Test;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;

public class TestJsonWriter extends TestJson {

    JsonWriter jsonWriter;
    JsonReader jsonReader;

    @Test
    void testWriteValidPath() {
        jsonWriter = new JsonWriter("./data/file.json");
        try {
            jsonWriter.open();
            // pass
        } catch (IOException e) {
            fail("Did not expect an exception due to invalid file path");
        }
    }

    @Test
    void testWriteInvalidPath() {
        jsonWriter = new JsonWriter("./data/\0file.json");
        try {
            jsonWriter.open();
            fail("Excepted exception due to invalid path");
        } catch (IOException e) {
            // pass
        }

        jsonWriter = new JsonWriter("./data/f:ile.json");
        try {
            jsonWriter.open();
            fail("Excepted exception due to invalid path");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriteEmptyTimeline() throws MidiUnavailableException, InvalidMidiDataException {
        try {
            String path = "./data/testWriteEmptyTimeline.json";
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("bob");

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            checkTimeline(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testModifiedTimeline() throws MidiUnavailableException, InvalidMidiDataException {
        try {
            String path = "./data/testWriteEmptyTimeline.json";
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("joe");
            timeline.setBPM(420);
            timeline.setPositionTick(1000);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            checkTimeline(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testMidiTracksTimeline() throws MidiUnavailableException, InvalidMidiDataException {
        try {
            String path = "./data/testWriteEmptyTimeline.json";
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("joe");
            addSampleSong(timeline);
            timeline.setBPM(160);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            checkTimeline(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    private void addSampleSong(Timeline timeline) {
        MidiTrack melody = timeline.createMidiTrack("synth pad", 89, false);
        MidiTrack drums = timeline.createMidiTrack("bass drum", 35, true);
        MidiTrack bass = timeline.createMidiTrack("bass", 38, false);
        MidiTrack hiHat = timeline.createMidiTrack("hi-hat", 42, true);

        melody.setVolume(127);
        drums.setVolume(110);
        bass.setVolume(110);

        final int beatTicks = timeline.beatsToTicks(1);

        Block melodyBlock = new Block(0);
        Block drumsBlock = new Block(0);
        Block bassBlock = new Block(beatTicks * 3);
        Block hiHatBlock = new Block(beatTicks * 4);

        melody.addBlock(melodyBlock);
        drums.addBlock(drumsBlock);
        bass.addBlock(bassBlock);
        hiHat.addBlock(hiHatBlock);

        for (int beat = 0; beat < 20; beat++) {
            drumsBlock.addNote(new Note(0, 127, beatTicks * beat, beatTicks));
        }

        for (int beat = 0; beat < 14; beat++) {
            int velocity = beat % 4 == 0 ? 127 : 76; // louder first hit-hat of measure
            hiHatBlock.addNote(new Note(0, velocity, beat * beatTicks + beatTicks / 2, beatTicks / 2));
        }

        melodyBlock.addNote(new Note(60, 127, beatTicks * 4, beatTicks * 2));
        melodyBlock.addNote(new Note(62, 127, beatTicks * 6, beatTicks));
        melodyBlock.addNote(new Note(56, 127, beatTicks * 7, beatTicks * 4));

        melodyBlock.addNote(new Note(60, 127, beatTicks * 12, beatTicks * 2));
        melodyBlock.addNote(new Note(62, 127, beatTicks * 14, beatTicks));
        melodyBlock.addNote(new Note(66, 127, beatTicks * 15, beatTicks * 4));

        bassBlock.addNote(new Note(32, 100, beatTicks, beatTicks / 2));
        bassBlock.addNote(new Note(32, 100, (int) (beatTicks * (double) 1.5), beatTicks / 2));

        bassBlock.addNote(new Note(32, 100, beatTicks * 4, beatTicks / 2));
        bassBlock.addNote(new Note(32, 100, (int) (beatTicks * (double) 4.5), beatTicks / 2));

        Block bassBlock2 = bassBlock.clone();
        bassBlock2.setStartTick(beatTicks * 11);
        bass.addBlock(bassBlock2);
    }
}
