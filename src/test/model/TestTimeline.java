package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.BeforeEach;

public class TestTimeline {
    Timeline timeline;

    @BeforeEach
    void runBefore() throws MidiUnavailableException, InvalidMidiDataException {
        timeline = new Timeline();
    }

    @Test
    void testConstructor() throws MidiUnavailableException {
        assertEquals(timeline.getTracks(), new ArrayList<MidiTrack>());
        assertEquals(timeline.getBPM(), 120);
        assertEquals(timeline.getPositionTick(), 0);
        assertEquals(timeline.getSequence().getResolution(), 960);
        assertEquals(timeline.getSequence().getDivisionType(), Sequence.PPQ);
    }

    @Test
    void testAddMidiTrack() {
        MidiTrack midiTrack = new MidiTrack("cool track", false);
        MidiTrack anotherMidiTrack = new MidiTrack("sick violin", false);
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        expectedMidiTracks.add(midiTrack);
        assertEquals(timeline.addMidiTrack(midiTrack), 0);
        assertEquals(timeline.getTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(timeline.getTracks().size(), 1);

        expectedMidiTracks.add(anotherMidiTrack);
        assertEquals(timeline.addMidiTrack(anotherMidiTrack), 1);
        assertEquals(timeline.getTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(1), expectedMidiTracks.get(1));
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(timeline.getTracks().size(), 2);
    }

    @Test
    void testRemoveMidiTrack() {
        MidiTrack midiTrack = new MidiTrack("cool guitar", false);
        MidiTrack anotherMidiTrack = new MidiTrack("sick violin", false);
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        timeline.addMidiTrack(midiTrack);
        timeline.addMidiTrack(anotherMidiTrack);
        expectedMidiTracks.add(midiTrack);
        expectedMidiTracks.add(anotherMidiTrack);

        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), midiTrack);
        assertEquals(timeline.getTracks().size(), 1);
        assertEquals(timeline.removeMidiTrack(0), anotherMidiTrack);
        assertEquals(timeline.getTracks().size(), 0);
    }

    @Test
    void testAddRemoveMidiTrack() {
        MidiTrack mt1 = new MidiTrack("cool guitar", false);
        MidiTrack mt2 = new MidiTrack("sick violin", false);
        MidiTrack mt3 = new MidiTrack("bass drums", true);
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        expectedMidiTracks.add(mt1);
        expectedMidiTracks.add(mt2);
        expectedMidiTracks.add(mt3);
        timeline.addMidiTrack(mt1);
        timeline.addMidiTrack(mt2);
        timeline.addMidiTrack(mt3);

        assertEquals(timeline.getTracks().size(), 3);
        assertEquals(timeline.removeMidiTrack(2), mt3);
        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), mt1);
        assertEquals(timeline.getTracks().size(), 1);

        assertEquals(timeline.addMidiTrack(mt1), 1);
        assertEquals(timeline.getTracks().size(), 2);
        assertEquals(timeline.getTrack(0), mt2);
    }

    @Test
    void testUpdateSequence() throws InvalidMidiDataException {

        MidiTrack mt1 = new MidiTrack("C notes", false);
        MidiTrack mt2 = new MidiTrack("cool notes", 4, true);
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

        timeline.addMidiTrack(mt1);
        timeline.addMidiTrack(mt2);

        // Update sequence via method
        timeline.updateSequence();

        // Update sequence by hand, creating the expected midi events
        MidiEvent n1on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 60), 0);
        MidiEvent n1off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 5);
        MidiEvent n2on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 50), 4);
        MidiEvent n2off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 13);
        MidiEvent n3on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 62, 90), 14);
        MidiEvent n3off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 62, 0), 31);
        MidiEvent n4on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 62, 90), 35);
        MidiEvent n4off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 62, 0), 45);

        Sequence expectedSequence = new Sequence(Sequence.PPQ, 960);
        Track track1 = expectedSequence.createTrack();
        Track track2 = expectedSequence.createTrack();

        MidiEvent programChangeEventT1 = new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                0, 0, 127), 0);
        MidiEvent volumeEventT1 = new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, 7, 100), 0);

        MidiEvent programChangeEventT2 = new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                9, 4, 127), 0);
        MidiEvent volumeEventT2 = new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 9, 7, 50), 0);

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
                MidiEvent expectedEvent = expectedTrack.get(i);
                MidiEvent realEvent = actualTrack.get(i);

                assertEquals(expectedEvent.getMessage().getStatus(), realEvent.getMessage().getStatus());
                assertEquals(expectedEvent.getMessage().getMessage()[0], realEvent.getMessage().getMessage()[0]);
                assertEquals(expectedEvent.getMessage().getMessage()[1], realEvent.getMessage().getMessage()[1]);
                if ((expectedEvent.getMessage().getMessage()[0] & 0xFF) != ShortMessage.PROGRAM_CHANGE) {
                    assertEquals(expectedEvent.getMessage().getMessage()[2], realEvent.getMessage().getMessage()[2]);
                }
            }
        }
    }

    @Test
    void testPlay() throws MidiUnavailableException, InvalidMidiDataException {
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
    }

    @Test
    void testPause() throws MidiUnavailableException {
        timeline.pause();
        assertFalse(timeline.getSequencer().isRunning());
    }

    @Test
    void testPlayBack() throws MidiUnavailableException, InvalidMidiDataException {
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
        timeline.pause();
        assertFalse(timeline.getSequencer().isRunning());
        timeline.pause();
        assertFalse(timeline.getSequencer().isRunning());
        timeline.play();
        assertTrue(timeline.getSequencer().isRunning());
    }

    @Test
    void testTimelinePosition() throws MidiUnavailableException {
        timeline.setPositionTick(30);
        assertEquals(timeline.getPositionTick(), 30);
        assertEquals(timeline.getPositionMs(), 15);

        timeline.setPositionTick(20);
        assertEquals(timeline.getPositionTick(), 20);
        assertEquals(timeline.getPositionMs(), 10);

        timeline.setBPM(240);

        timeline.setPositionTick(20);
        assertEquals(timeline.getPositionTick(), 20);
        assertEquals(timeline.getPositionMs(), 5);

        timeline.setPositionTick(0);
        assertEquals(timeline.getPositionTick(), 0);
    }

    @Test
    void testMutedTrackUpdateTimeline() throws InvalidMidiDataException {
        MidiTrack midiTrack1 = new MidiTrack("track", false);
        MidiTrack midiTrack2 = new MidiTrack("track2", true);
        MidiTrack midiTrack3 = new MidiTrack("track3", true);
        MidiTrack midiTrack4 = new MidiTrack("track4", true);

        midiTrack1.setMuted(true);
        midiTrack2.setMuted(true);
        midiTrack3.setMuted(true);
        midiTrack4.setMuted(true);

        timeline.addMidiTrack(midiTrack1);
        timeline.addMidiTrack(midiTrack2);
        timeline.addMidiTrack(midiTrack3);
        timeline.addMidiTrack(midiTrack4);

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
    void testBPMChange() {
        MidiTrack testTrack = new MidiTrack("track", false);
        Block testBlock = new Block(0);
        Note testNote = new Note(60, 100, 0, 960);

        testBlock.addNote(testNote);
        testTrack.addBlock(testBlock);
        timeline.addMidiTrack(testTrack);

        // PPQ = 960, note ends at 960, and the bpm is 120.
        // So 1 quarter note at 120 BPM
        // 120 BPM = 0.5 seconds per quarter note
        assertEquals(timeline.getLengthMS(), 500);
        assertEquals(Timeline.msToTicks(500, timeline.getBPM()), 960); // Check the reverse
        MidiTrack testTrack2 = new MidiTrack("track", false);
        Block testBlock2 = new Block(960);
        Note testNote2 = new Note(60, 100, 0, 1920);

        testBlock2.addNote(testNote2);
        testTrack2.addBlock(testBlock2);
        timeline.addMidiTrack(testTrack2);

        assertEquals(timeline.getLengthMS(), 1500);
        assertEquals(Timeline.msToTicks(1500, timeline.getBPM()), 1920 + 960); // Check the reverse

        timeline.setBPM(240); // double BPM
        assertEquals(timeline.getLengthMS(), 750); // ms halves

        testBlock2.addNote(new Note(60, 60, 231, 500));
        assertEquals(timeline.getLengthMS(), 750);
    }
}
