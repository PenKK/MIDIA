package model;

import org.junit.jupiter.api.Test;

import persistance.TestJson;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.BeforeEach;

// See https://midi.org/expanded-midi-1-0-messages-list 
// to understand the checking of bytes in MidiEvents in tests
public class TestTimeline extends TestJson {
    Timeline timeline;
    ArrayList<Integer> expectedChannels;

    @BeforeEach
    void runBefore() throws MidiUnavailableException, InvalidMidiDataException {
        timeline = new Timeline("test");
        expectedChannels = new ArrayList<>();
        expectedChannels.add(0);
        expectedChannels.add(1);
        expectedChannels.add(2);
        expectedChannels.add(3);
        expectedChannels.add(4);
        expectedChannels.add(5);
        expectedChannels.add(6);
        expectedChannels.add(7);
        expectedChannels.add(8);
        expectedChannels.add(10);
        expectedChannels.add(11);
        expectedChannels.add(12);
        expectedChannels.add(13);
        expectedChannels.add(14);
        expectedChannels.add(15);
    }

    @Test
    void testConstructor() throws MidiUnavailableException {
        assertEquals(timeline.getProjectName(), "test");
        assertEquals(timeline.getTracks(), new ArrayList<MidiTrack>());
        assertEquals(timeline.getBPM(), 120);
        assertEquals(timeline.getPositionTick(), 0);
        assertEquals(timeline.getBeatDivision(), 4);
        assertEquals(timeline.getBeatsPerMeasure(), 4);
        assertEquals(timeline.getHorizontalScale(), 0.1);
        assertEquals(timeline.getSequence().getResolution(), 960);
        assertEquals(timeline.getSequence().getDivisionType(), Sequence.PPQ);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());
    }

    @Test
    void testAddMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        MidiTrack midiTrack = timeline.createMidiTrack("cool track", 0, false);
        expectedMidiTracks.add(midiTrack);
        assertEquals(timeline.getTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(timeline.getTracks().size(), 1);
        assertEquals(midiTrack.getChannel(), 0);

        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        MidiTrack anotherMidiTrack = timeline.createMidiTrack("sick violin", 0, false);
        expectedMidiTracks.add(anotherMidiTrack);
        assertEquals(timeline.getTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(1), expectedMidiTracks.get(1));
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(anotherMidiTrack.getChannel(), 1);

        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());
    }

    @Test
    void testRemoveMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();
        MidiTrack midiTrack = timeline.createMidiTrack("cool track", 0, false);
        MidiTrack anotherMidiTrack = timeline.createMidiTrack("sick violin", 0, false);

        expectedMidiTracks.add(midiTrack);
        expectedMidiTracks.add(anotherMidiTrack);

        expectedChannels.remove(0);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), midiTrack);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        assertEquals(timeline.getTracks().size(), 1);
        assertEquals(timeline.removeMidiTrack(0), anotherMidiTrack);
        assertEquals(timeline.getTracks().size(), 0);
        expectedChannels.add(1);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());
    }

    @Test
    void testAddRemoveMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();
        MidiTrack mt1 = timeline.createMidiTrack("cool track", 0, false);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        MidiTrack mt2 = timeline.createMidiTrack("sick violin", 0, false);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        MidiTrack mt3 = timeline.createMidiTrack("bass drum", 0, true);
        // Do not remove any as the track is percussive
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        expectedMidiTracks.add(mt1);
        expectedMidiTracks.add(mt2);
        expectedMidiTracks.add(mt3);

        assertEquals(timeline.getTracks().size(), 3);
        assertEquals(timeline.removeMidiTrack(2), mt3);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), mt1);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());

        assertEquals(timeline.getTracks().size(), 1);
        assertEquals(timeline.removeMidiTrack(0), mt2);
        expectedChannels.add(1);
        assertEquals(expectedChannels, timeline.getAvaliableChannels());
        assertEquals(timeline.getTracks().size(), 0);
    }

    @Test
    void testManyTracksAndPlayBack() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (timeline.getAvaliableChannels().size() != 0) {
            midiTracks.add(timeline.createMidiTrack("instrumental", 4, false));
        }
        assertTrue(midiTracks.size() == 15);

        midiTracks.add(timeline.createMidiTrack("percussive", 0, true));
        assertEquals(timeline.createMidiTrack("cant make more", 0, false), null);

        timeline.removeMidiTrack(5);
        assertTrue(timeline.getAvaliableChannels().size() == 1);
        assertEquals(timeline.createMidiTrack("space for 1 more", 6, false).getChannel(), 5);
        assertEquals(timeline.createMidiTrack("cant make more", 0, false), null);

        assertTrue(timeline.getAvaliableChannels().isEmpty());
        timeline.removeMidiTrack(10);
        assertTrue(timeline.getAvaliableChannels().size() == 1);
        assertEquals(timeline.createMidiTrack("space for 1 more", 6, false).getChannel(), 12);
    }

    @Test
    void testManyTracksCreate() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (timeline.getAvaliableChannels().size() != 0) {
            midiTracks.add(timeline.createMidiTrack("instrumental", 4, false));
        }
        assertTrue(midiTracks.size() == 15);

        MidiTrack instrumentalTrack = timeline.createMidiTrack("inst", 0, false);
        MidiTrack percussionTrack = timeline.createMidiTrack("perc", 50, true);
        assertNull(instrumentalTrack);
        assertNotNull(percussionTrack);
    }

    @Test
    void testUpdateSequence() throws InvalidMidiDataException {
        MidiTrack mt1 = timeline.createMidiTrack("C notes", 0, false);
        MidiTrack mt2 = timeline.createMidiTrack("cool notes", 4, false);
        MidiTrack mt3 = timeline.createMidiTrack("basically muted", 50, false);
        mt3.setVolume(0);
        mt2.setVolume(50);

        Block b1 = new Block(0);
        Block b2 = new Block(5);
        Note n1 = new Note(60, 60, 0, 5);
        Note n2 = new Note(60, 50, 4, 9);
        Note n3 = new Note(62, 90, 9, 17);
        Note n4 = new Note(62, 40, 30, 10);

        b1.addNote(n1);
        b1.addNote(n2);
        b2.addNote(n3);
        b2.addNote(n4);

        mt1.addBlock(b1);
        mt2.addBlock(b2);

        // Update sequence via method
        timeline.updateSequence();

        // Update sequence by hand, creating the expected midi events
        MidiEvent n1on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 60), 0);
        MidiEvent n1off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 5);
        MidiEvent n2on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 50), 4);
        MidiEvent n2off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 13);
        MidiEvent n3on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, 62, 90), 14);
        MidiEvent n3off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, 62, 0), 31);
        MidiEvent n4on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 1, 62, 90), 35);
        MidiEvent n4off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 1, 62, 0), 45);

        Sequence expectedSequence = new Sequence(Sequence.PPQ, 960);
        Track track1 = expectedSequence.createTrack();
        Track track2 = expectedSequence.createTrack();

        MidiEvent programChangeEventT1 = new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                0, 0, 127), 0);
        MidiEvent volumeEventT1 = new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, 7, 100), 0);

        MidiEvent programChangeEventT2 = new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                1, 4, 127), 0);
        MidiEvent volumeEventT2 = new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 1, 7, 50), 0);

        track1.add(programChangeEventT1);
        track1.add(volumeEventT1);
        track2.add(programChangeEventT2);
        track2.add(volumeEventT2);

        track1.add(n1on);
        track1.add(n2on);
        track1.add(n1off);
        track1.add(n2off);
        track2.add(n3on);
        track2.add(n3off);
        track2.add(n4on);
        track2.add(n4off);

        Track[] expectedTracks = { track1, track2 };

        for (int i = 0; i < 2; i++) {
            Track expectedTrack = expectedTracks[i];
            Track actualTrack = timeline.getSequence().getTracks()[i];

            for (int j = 0; j < expectedTrack.size(); j++) {
                MidiMessage expectedEventData = expectedTrack.get(i).getMessage();
                MidiMessage realEventData = actualTrack.get(i).getMessage();

                assertEquals(expectedEventData.getStatus(), realEventData.getStatus());
                assertEquals(expectedEventData.getMessage()[0], realEventData.getMessage()[0]);
                assertEquals(expectedEventData.getMessage()[1], realEventData.getMessage()[1]);
                if ((expectedEventData.getMessage()[0] & 0xFF) != ShortMessage.PROGRAM_CHANGE) {
                    assertEquals(expectedEventData.getMessage()[2], realEventData.getMessage()[2]);
                }
            }
        }
    }

    @Test
    void testPlayBack() throws MidiUnavailableException, InvalidMidiDataException, InterruptedException {
        Block b = new Block(0);
        Note n = new Note(60, 100, 0, 960);
        Note n2 = new Note(64, 127, 960, 960);
        b.addNote(n);
        b.addNote(n2);
        MidiTrack midiTrack = timeline.createMidiTrack("test", 0, false);
        midiTrack.addBlock(b);

        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
        Thread.sleep((long) (timeline.getLengthMs() + 500));
        assertFalse(timeline.getSequencer().isRunning());

        // test playback a second time
        b.removeNote(1);
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
        Thread.sleep((long) (timeline.getLengthMs() + 500));
        assertFalse(timeline.getSequencer().isRunning());
        timeline.pause();

        // test play and pause
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
        timeline.pause();
        assertFalse(timeline.getSequencer().isRunning());
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
        Thread.sleep((long) (timeline.getLengthMs() / 2) + 500);
        assertFalse(timeline.getSequencer().isRunning());
    }

    @Test
    void testTimelinePosition() throws MidiUnavailableException {
        double roundingDelta = 0.01;

        timeline.setPositionTick(30);
        assertEquals(timeline.getPositionTick(), 30);

        assertEquals(timeline.getPositionMs(), 15.625, roundingDelta);
        assertEquals(timeline.getPositionBeats(), 0.03125, roundingDelta); // 30 ms is very short
        assertEquals(timeline.getPositionOnBeat(), 0.03125 + 1, roundingDelta);
        timeline.setPositionMs(15.625);
        assertEquals(timeline.getPositionTick(), 30);

        timeline.setPositionTick(20);
        assertEquals(timeline.getPositionTick(), 20);
        assertEquals(timeline.getPositionMs(), 10.42, roundingDelta);

        timeline.setBPM(240);
        timeline.setPositionTick(20);
        assertEquals(timeline.getPositionTick(), 20);
        assertEquals(timeline.getPositionMs(), 5.2, roundingDelta);
        assertEquals(timeline.getPositionBeats(), 0.0208, roundingDelta);
        assertEquals(timeline.getPositionOnBeat(), 0.0208 + 1, roundingDelta);

        timeline.setPositionTick(0);
        assertEquals(timeline.getPositionTick(), 0);
        assertEquals(timeline.getPositionBeats(), 0);
        assertEquals(timeline.getPositionOnBeat(), 1.0);

        timeline.setPositionBeat(11);
        assertEquals(timeline.getPositionMs(), 2500);

        timeline.setPositionBeat(1);
        assertEquals(timeline.getPositionMs(), 0);
    }

    @Test
    void testMutedTrackUpdateTimeline() throws InvalidMidiDataException {
        MidiTrack midiTrack1 = timeline.createMidiTrack("track", 0, false);
        MidiTrack midiTrack2 = timeline.createMidiTrack("track", 0, true);
        MidiTrack midiTrack3 = timeline.createMidiTrack("track", 0, true);
        MidiTrack midiTrack4 = timeline.createMidiTrack("track", 0, true);

        midiTrack1.setMuted(true);
        midiTrack2.setMuted(true);
        midiTrack3.setMuted(true);
        midiTrack4.setMuted(true);

        timeline.updateSequence();
        assertEquals(timeline.getSequence().getTracks().length, 0);

        midiTrack1.setMuted(false);
        timeline.updateSequence();
        assertEquals(timeline.getSequence().getTracks().length, 1);

        midiTrack2.setMuted(false);
        midiTrack3.setMuted(false);
        timeline.updateSequence();
        assertEquals(timeline.getSequence().getTracks().length, 3);

        midiTrack3.setMuted(true);
        timeline.updateSequence();
        assertEquals(timeline.getSequence().getTracks().length, 2);

        midiTrack3.setMuted(false);
        midiTrack4.setMuted(false);
        timeline.updateSequence();
        assertEquals(timeline.getSequence().getTracks().length, 4);
    }

    @Test
    void testTempoChange() {
        MidiTrack testTrack = timeline.createMidiTrack("track", 0, false);
        Block testBlock = new Block(0);
        Note testNote = new Note(60, 100, 0, 960);

        testBlock.addNote(testNote);
        testTrack.addBlock(testBlock);

        // PPQ = 960, note ends at 960, and the bpm is 120.
        // So 1 quarter note at 120 BPM
        // 120 BPM = 0.5 seconds per quarter note
        assertEquals(timeline.getLengthMs(), 500);
        assertEquals(timeline.msToTicks(500), 960); // Check the reverse
        assertEquals(timeline.getLengthBeats(), 1); // 1 beat = 1 quarter note
        MidiTrack testTrack2 = timeline.createMidiTrack("track2", 0, false);
        Block testBlock2 = new Block(960);
        Note testNote2 = new Note(60, 100, 0, 1920);

        testBlock2.addNote(testNote2);
        testTrack2.addBlock(testBlock2);

        assertEquals(timeline.getLengthMs(), 1500);
        assertEquals(timeline.msToTicks(1500), 1920 + 960); // Check the reverse
        assertEquals(timeline.getLengthBeats(), 3);

        timeline.setBPM(240); // double BPM
        assertEquals(timeline.getLengthMs(), 750); // ms halves

        testBlock2.addNote(new Note(60, 60, 231, 500));
        assertEquals(timeline.getLengthMs(), 750);
    }

    @Test
    void testBeats() {
        assertEquals(timeline.ticksToBeats(960), 1);
        assertEquals(timeline.ticksToOnBeat(960), 1 + 1);
        assertEquals(timeline.beatsToTicks(1), 960);
        assertEquals(timeline.ticksToBeats(960 + 960 / 2), 1.5);
        assertEquals(timeline.ticksToBeats(960 / 4), 0.25);
        assertEquals(timeline.ticksToOnBeat(960 / 4), 0.25 + 1);

        timeline.setBPM(100);
        // BPM does not change the converstion as beats = ticks / PPQN
        assertEquals(timeline.ticksToBeats(960 / 4), 0.25); // remains same

        timeline.setPositionBeat(5);
        assertEquals(timeline.getPositionTick(), 4 * 960);
        timeline.setPositionBeat(1.5);
        assertEquals(timeline.getPositionTick(), 960 / 2);
    }

    @Test
    void testProjectNameChange() {
        assertEquals(timeline.getProjectName(), "test");
        timeline.setProjectName("cool song");
        assertEquals(timeline.getProjectName(), "cool song");
        timeline.setProjectName("cooler song");
        assertEquals(timeline.getProjectName(), "cooler song");
    }

    @Test
    void testSingletonInstance() throws MidiUnavailableException, InvalidMidiDataException {
        Timeline timelineInstance = Timeline.getInstance();
        Timeline timeline = new Timeline("New Project");

        checkTimeline(timelineInstance, timeline);

        timeline.setProjectName("joe");
        timelineInstance.setProjectName("joe");

        checkTimeline(timelineInstance, timeline);
        timelineInstance = Timeline.getInstance();
        checkTimeline(timelineInstance, timeline);
    }

    @Test
    void testObserverPattern() throws MidiUnavailableException, InvalidMidiDataException {
        class TestObserver implements PropertyChangeListener {
            private int value = 0;
        
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("timeline")) {
                    value++;
                }
            }
        
            public int getValue() {
                return value;
            }
        }

        TestObserver testObserver = new TestObserver();
        
        Timeline.addObserver(testObserver);
        assertEquals(Timeline.getPropertyChangeSupport().getPropertyChangeListeners().length, 1);

        Timeline.setInstance(new Timeline("joe"));

        assertEquals(testObserver.getValue(), 1);

        Timeline.removeObserver(testObserver);
        Timeline.setInstance(timeline);

        assertEquals(testObserver.getValue(), 1);
    }
}
