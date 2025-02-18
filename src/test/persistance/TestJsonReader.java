package persistance;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.Test;

import model.Timeline;

public class TestJsonReader extends TestJson {

    JsonReader reader;

    @Test
    void testReaderNonExistentFile() throws MidiUnavailableException {
        reader = new JsonReader("./data/noSuchFile.json");
        try {
            Timeline timeline = reader.read();
            fail("IOException expected");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testNewTimeline() throws MidiUnavailableException {
        reader = new JsonReader("./data/testReaderNewTimeline.json");
        try {
            Timeline timeline = reader.read();
            checkTimeline(timeline, new Timeline("New project"));
        } catch (IOException e) {
            fail("Path should exist and be accessible");
        } catch (InvalidMidiDataException e) {
            fail("Should not have invalid midi data");
        }
    }

    @Test
    void testExtensiveTimeline() throws MidiUnavailableException {
        reader = new JsonReader("./data/testReaderExtensive.json");
        try {
            Timeline timeline = reader.read();
            Timeline timeline2 = new Timeline("aaaa");

            addSampleSong(timeline2);
            checkTimeline(timeline, timeline2);
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        } catch (IOException e) {
            fail("File should exist and be accessible");
        }
    }
}
