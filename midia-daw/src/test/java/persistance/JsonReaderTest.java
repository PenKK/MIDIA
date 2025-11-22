package persistance;

import static org.junit.jupiter.api.Assertions.fail;
import static persistance.UtilTest.skipIfHeadless;

import java.beans.PropertyChangeSupport;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.Test;

import model.Timeline;


public class JsonReaderTest {

    JsonReader reader;
    PropertyChangeSupport pcsTest = new PropertyChangeSupport("test");

    @Test
    void testReaderNonExistentFile() throws MidiUnavailableException, IOException {
        reader = new JsonReader(UtilTest.getReadFilePath("noSuchFile.json"));
        try {
            reader.read(pcsTest);
            fail("IOException expected");
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testNewTimeline() throws MidiUnavailableException, IOException {
        skipIfHeadless();
        reader = new JsonReader(UtilTest.getReadFilePath("testReaderNewTimeline.json"));
        try {
            Timeline timeline = reader.read(pcsTest);
            UtilTest.assertTimelineEquals(timeline, new Timeline("New project", null));
        } catch (IOException e) {
            fail("Path should exist and be accessible");
        } catch (InvalidMidiDataException e) {
            fail("Should not have invalid midi data");
        }
    }

    @Test
    void testExtensiveTimeline() throws MidiUnavailableException, IOException {
        skipIfHeadless();
        reader = new JsonReader(UtilTest.getReadFilePath("testReaderExtensive.json"));
        try {
            Timeline timeline = reader.read(pcsTest);
            Timeline timeline2 = new Timeline("aaaa", null);
            timeline2.setPropertyChangeSupport(new PropertyChangeSupport(timeline2));
            timeline2.getPlayer().setBPM(160);

            UtilTest.addSampleSong(timeline2);
            UtilTest.assertTimelineEquals(timeline, timeline2);
        } catch (InvalidMidiDataException e) {
            fail("InvalidMidiDataException should not be thrown");
        } catch (IOException e) {
            fail("File should exist and be accessible");
        }
    }
}
