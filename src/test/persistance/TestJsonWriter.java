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
        jsonWriter = new JsonWriter("./data/testFile.json");
        try {
            jsonWriter.open();
            // pass
        } catch (IOException e) {
            fail("Did not expect an exception due to invalid file path");
        }
    }

    @Test
    void testWriteInvalidPath() {
        jsonWriter = new JsonWriter("./data/\0fil:e.json");
        try {
            jsonWriter.open();
            fail("Excepted exception due to invalid path");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriteEmptyTimeline() throws MidiUnavailableException {
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
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        }
    }

    @Test
    void testModifiedTimeline() throws MidiUnavailableException {
        try {
            String path = "./data/testModifiedTimeline.json";
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
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

    @Test
    void testWriteInvalidMidiData() throws MidiUnavailableException {
        try {
            String path = "./data/testInvalidMidiDataTimeline.json";
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
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

    @Test
    void testMidiTracksTimeline() throws MidiUnavailableException {
        try {
            String path = "./data/testMidiTracksTimeline.json";
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
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

}
