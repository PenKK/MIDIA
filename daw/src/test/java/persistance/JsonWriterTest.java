package persistance;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.InvalidPathException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.Test;

import model.Timeline;

public class JsonWriterTest {

    JsonWriter jsonWriter;
    JsonReader jsonReader;

    @Test
    void testWriteValidPath() throws IOException {
        jsonWriter = new JsonWriter(UtilTest.getWriteFilePath("testFile.json"));
        try {
            jsonWriter.open();
            // pass
        } catch (IOException e) {
            fail("Did not expect an exception due to invalid file path");
        }
    }

    @Test
    void testWriteInvalidPath() {
        try {
            jsonWriter = new JsonWriter(UtilTest.getWriteFilePath("\0exception.json"));
            jsonWriter.open();
            fail("Excepted exception due to invalid path");
        } catch (InvalidPathException e) {
            // pass
        } catch (IOException e) {
            fail("Expected InvalidPathException, got " + e.getClass().getSimpleName());
        }
    }

    @Test
    void testWriteEmptyTimeline() throws MidiUnavailableException {
        try {
            String path = UtilTest.getWriteFilePath("testWriteEmptyTimeline.json");
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("bob", null);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            UtilTest.assertTimelineEquals(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        }
    }

    @Test
    void testModifiedTimeline() throws MidiUnavailableException {
        try {
            String path = UtilTest.getWriteFilePath("testModifiedTimeline.json");
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("joe", null);
            timeline.setPropertyChangeSupport(new PropertyChangeSupport(timeline));
            timeline.getPlayer().setBPM(420);
            timeline.getPlayer().setTickPosition(1000);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            UtilTest.assertTimelineEquals(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

    @Test
    void testWriteInvalidMidiData() throws MidiUnavailableException {
        try {
            String path = UtilTest.getWriteFilePath("testInvalidMidiDataTimeline.json");
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("joe", null);
            timeline.setPropertyChangeSupport(new PropertyChangeSupport(timeline));
            timeline.getPlayer().setBPM(420);
            timeline.getPlayer().setTickPosition(1000);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            UtilTest.assertTimelineEquals(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

    @Test
    void testMidiTracksTimeline() throws MidiUnavailableException {
        try {
            String path = UtilTest.getWriteFilePath("testMidiTracksTimeline.json");
            jsonWriter = new JsonWriter(path);
            Timeline timeline = new Timeline("joe", null);
            timeline.setPropertyChangeSupport(new PropertyChangeSupport(timeline));
            UtilTest.addSampleSong(timeline);
            timeline.getPlayer().setBPM(160);

            jsonWriter.open();
            jsonWriter.write(timeline);
            jsonWriter.close();

            jsonReader = new JsonReader(path);
            Timeline timeline2 = jsonReader.read();
            UtilTest.assertTimelineEquals(timeline, timeline2);
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not have been thrown");
        }
    }

}
