package model;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.jupiter.api.*;

import persistance.TestJson;

public class TestTimelineController extends TestJson {

    private TimelineController tc;

    @BeforeEach
    void setup() {
        tc = new TimelineController();
    }

    @Test
    void testObserverPattern() throws MidiUnavailableException, InvalidMidiDataException {
        class TestObserver implements PropertyChangeListener {
            private int value = 0;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("timelineReplaced")) {
                    value++;
                }
            }

            public int getValue() {
                return value;
            }
        }

        TestObserver testObserver = new TestObserver();
        assertEquals(testObserver.getValue(), 0);

        tc.addObserver(testObserver);
        assertEquals(tc.getPropertyChangeSupport().getPropertyChangeListeners().length, 1);

        tc.refresh();
        assertEquals(testObserver.getValue(), 1);

        tc.setInstance(new Timeline("joe", tc.getPropertyChangeSupport()));
        assertEquals(testObserver.getValue(), 2);

        tc.removeObserver(testObserver);
        tc.refresh();
        assertEquals(testObserver.getValue(), 2);
    }

    @Test
    void testPlayback() throws InterruptedException {
        tc.playTimeline();
        tc.pauseTimeline();
        addSampleSong(tc.getTimeline());
        assertFalse(tc.isPlaying());
        tc.playTimeline();
        assertTrue(tc.isPlaying());

        tc.getTimeline().getPlayer().setBPM(240);

        tc.pauseTimeline();
        assertFalse(tc.isPlaying());
        tc.playTimeline();
        assertTrue(tc.isPlaying());
    }
}
