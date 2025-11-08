package model;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.jupiter.api.*;

import persistance.TestUtil;

public class TestTimelineController extends TestUtil {

    private TimelineController tc;

    @BeforeEach
    void setup() {
        tc = new TimelineController();
    }

    @Test
    void testObserverPattern() {
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
        assertEquals(0, testObserver.getValue());

        tc.addObserver(testObserver);
        assertEquals(1, tc.getPropertyChangeSupport().getPropertyChangeListeners().length);

        tc.getPropertyChangeSupport().firePropertyChange("timelineReplaced", null, tc.getTimeline());
        assertEquals(1, testObserver.getValue());

        tc.setInstance(new Timeline("joe", tc.getPropertyChangeSupport()));
        assertEquals(2, testObserver.getValue());

        tc.removeObserver(testObserver);
        tc.getPropertyChangeSupport().firePropertyChange("timelineReplaced", null, tc.getTimeline());
        assertEquals(2, testObserver.getValue());
    }

    @Test
    void testPlayback() throws InterruptedException {
        tc.playTimeline();
        tc.pauseTimeline();
        tc.playTimeline();
        Thread.sleep(100);
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

    @Test
    void testRulerDrag() {
        assertFalse(tc.getTimeline().getPlayer().isDraggingRuler());
        tc.getTimeline().getPlayer().startRulerDrag();
        assertTrue(tc.getTimeline().getPlayer().isDraggingRuler());
        tc.getTimeline().getPlayer().stopRulerDrag();
        assertFalse(tc.getTimeline().getPlayer().isDraggingRuler());
    }

    @Test
    void testSetInstanceDuringPlayback() {
        addSampleSong(tc.getTimeline());
        assertFalse(tc.getTimeline().getPlayer().isPlaying());
        tc.playTimeline();
        assertTrue(tc.getTimeline().getPlayer().isPlaying());
        tc.setInstance(new Timeline("new", tc.getPropertyChangeSupport()));
        assertFalse(tc.getTimeline().getPlayer().isPlaying());
    }

    @Test
    void testSetInstanceTimelineNull() {
        try {
            tc.setInstance(null);
            fail();
        } catch (NullPointerException e) {
            // pass
        }
    }
}
