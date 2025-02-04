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

public class TestSequencerController {
    SequencerController sequencerController;

    @BeforeEach
    void runBefore() {
        sequencerController = new SequencerController();
    }

    @Test
    void testConstructor() {
        assertEquals(sequencerController.getTracks(), new ArrayList<MidiTrack>());
    }

    @Test
    void testAddMidiTrack() {
        MidiTrack midiTrack = new MidiTrack("cool track", false);
        MidiTrack anotherMidiTrack = new MidiTrack("sick violin", false);
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        expectedMidiTracks.add(midiTrack);
        assertEquals(sequencerController.addMidiTrack(midiTrack), 0);
        assertEquals(sequencerController.getTracks(), expectedMidiTracks);
        assertEquals(sequencerController.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(sequencerController.getTracks().size(), 1);

        expectedMidiTracks.add(anotherMidiTrack);
        assertEquals(sequencerController.addMidiTrack(anotherMidiTrack), 1);
        assertEquals(sequencerController.getTracks(), expectedMidiTracks);
        assertEquals(sequencerController.getTrack(1), expectedMidiTracks.get(1));
        assertEquals(sequencerController.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(sequencerController.getTracks().size(), 2);
    }

    @Test
    void testRemoveMidiTrack() {
        MidiTrack midiTrack = new MidiTrack("cool guitar", false);
        MidiTrack anotherMidiTrack = new MidiTrack("sick violin", false);
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        sequencerController.addMidiTrack(midiTrack);
        sequencerController.addMidiTrack(anotherMidiTrack);
        expectedMidiTracks.add(midiTrack);
        expectedMidiTracks.add(anotherMidiTrack);

        assertEquals(sequencerController.getTracks().size(), 2);
        assertEquals(sequencerController.removeMidiTrack(0), midiTrack);
        assertEquals(sequencerController.getTracks().size(), 1);
        assertEquals(sequencerController.removeMidiTrack(0), anotherMidiTrack);
        assertEquals(sequencerController.getTracks().size(), 0);
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
        sequencerController.addMidiTrack(mt1);
        sequencerController.addMidiTrack(mt2);
        sequencerController.addMidiTrack(mt3);

        assertEquals(sequencerController.getTracks().size(), 3);
        assertEquals(sequencerController.removeMidiTrack(2), mt3);
        assertEquals(sequencerController.getTracks().size(), 2);
        assertEquals(sequencerController.removeMidiTrack(0), mt1);
        assertEquals(sequencerController.getTracks().size(), 1);

        assertEquals(sequencerController.addMidiTrack(mt1), 1);
        assertEquals(sequencerController.getTracks().size(), 2);
        assertEquals(sequencerController.getTrack(0), mt2);
    }

    @Test
    void testUpdateSequence() throws InvalidMidiDataException {
        MidiEvent n1on;
        MidiEvent n1off;
        MidiEvent n2on;
        MidiEvent n2off;
        MidiEvent n3on;
        MidiEvent n3off;
        MidiEvent n4on;
        MidiEvent n4off;
        Sequence expectedSequence;
        Track track1;
        Track track2;

        // Update sequence via method
        MidiTrack mt1 = new MidiTrack("C notes", false);
        MidiTrack mt2 = new MidiTrack("cool notes", false);
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

        sequencerController.addMidiTrack(mt1);
        sequencerController.addMidiTrack(mt2);
        sequencerController.updateSequence();

        // Update sequence by hand
        n1on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 60), 0);
        n1off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 60), 5);
        n2on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 50), 4);
        n2off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 50), 13);
        n3on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 62, 90), 14);
        n3off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 62, 90), 31);
        n4on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 62, 90), 35);
        n4off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 62, 40), 45);

        expectedSequence = new Sequence(Sequence.PPQ, 960);
        track1 = expectedSequence.createTrack();
        track2 = expectedSequence.createTrack();

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
            Track actualTrack = sequencerController.getSequence().getTracks()[i];

            for (int j = 0; j < expectedTrack.size(); j++) {
                MidiEvent expectedEvent = expectedTrack.get(i);
                MidiEvent realEvent = actualTrack.get(i);
    
                assertEquals(expectedEvent.getMessage().getStatus(), realEvent.getMessage().getStatus());
                assertEquals(expectedEvent.getMessage().getMessage()[0], realEvent.getMessage().getMessage()[0]);
                assertEquals(expectedEvent.getMessage().getMessage()[1], realEvent.getMessage().getMessage()[1]);
                assertEquals(expectedEvent.getMessage().getMessage()[2], realEvent.getMessage().getMessage()[2]);
            }
        }
    }

    @Test
    void testPlay() {
        sequencerController.play();
        try {
            assertTrue(sequencerController.getSequencer().isRunning());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPause() {
        sequencerController.pause();
        try {
            assertFalse(sequencerController.getSequencer().isRunning());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPlayBack() {
        sequencerController.play();
        try {
            assertTrue(sequencerController.getSequencer().isRunning());
            sequencerController.pause();
            assertFalse(sequencerController.getSequencer().isRunning());
            sequencerController.pause();
            assertFalse(sequencerController.getSequencer().isRunning());
            sequencerController.play();
            assertTrue(sequencerController.getSequencer().isRunning());
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }
}
