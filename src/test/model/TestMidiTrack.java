package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMidiTrack {

    MidiTrack midiTrack;

    @BeforeEach
    void runBefore() {
        midiTrack = new MidiTrack("Piano Melody", false);
    }

    @Test
    void testConstructor() {
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument(), 0); // 0 is piano
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Piano Melody");

        midiTrack = new MidiTrack("Precussive drums", true);
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument(), 35); // 35 is bass drum
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Precussive drums");
    }

    @Test
    void testConstructorOverload() {
        midiTrack = new MidiTrack("Non Precussive", 25, false); // 25 is acoustic guitar (nylon)
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument(), 25);
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Non Precussive");

        midiTrack = new MidiTrack("Precussive", 38, true); // 38 is acoustic snare
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument(), 38);
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Precussive");
    }

    @Test
    void testAddBlock() {
        Block b1 = new Block(0);
        Block b2 = new Block(30);
        Block b3 = new Block(50);
        ArrayList<Block> expectedBlocks = new ArrayList<>();

        assertEquals(0, midiTrack.addBlock(b1));
        expectedBlocks.add(b1);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        assertEquals(1, midiTrack.addBlock(b2));
        expectedBlocks.add(b2);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        assertEquals(2, midiTrack.addBlock(b3));
        expectedBlocks.add(b3);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        assertEquals(midiTrack.getBlocks().size(), 3);
    }

    @Test
    void testRemoveBlock() {
        Block b1 = new Block(0);
        Block b2 = new Block(30);
        Block b3 = new Block(50);
        ArrayList<Block> expectedBlocks = new ArrayList<>();

        midiTrack.addBlock(b1);
        midiTrack.addBlock(b2);
        midiTrack.addBlock(b3);
        expectedBlocks.add(b1);
        expectedBlocks.add(b2);
        expectedBlocks.add(b3);

        midiTrack.removeBlock(2);
        expectedBlocks.remove(2);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        midiTrack.removeBlock(0);
        expectedBlocks.remove(0);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        midiTrack.removeBlock(0);
        expectedBlocks.remove(0);
        assertEquals(expectedBlocks, midiTrack.getBlocks());

        assertEquals(midiTrack.getBlocks().size(), 0);
    }

    @Test
    public void testAddRemoveBlock() {
        Block b1 = new Block(0);
        Block b2 = new Block(30);
        Block b3 = new Block(50);
        Block b4 = new Block(70);
        Block b5 = new Block(100);
        ArrayList<Block> expectedBlocks = new ArrayList<>();

        midiTrack.addBlock(b1);
        expectedBlocks.add(b1);

        midiTrack.addBlock(b2);
        expectedBlocks.add(b2);

        midiTrack.addBlock(b3);
        expectedBlocks.add(b3);

        midiTrack.removeBlock(1);
        expectedBlocks.remove(1);

        assertEquals(expectedBlocks, midiTrack.getBlocks());

        midiTrack.addBlock(b4);
        expectedBlocks.add(b4);

        midiTrack.addBlock(b5);
        expectedBlocks.add(b5);

        assertEquals(expectedBlocks, midiTrack.getBlocks());
        assertEquals(4, midiTrack.getBlocks().size());

        midiTrack.removeBlock(0);
        expectedBlocks.remove(0);

        assertEquals(expectedBlocks, midiTrack.getBlocks());
        assertEquals(3, midiTrack.getBlocks().size());

        midiTrack.addBlock(new Block(1000));
        assertEquals(4, midiTrack.getBlocks().size());

    }

    @Test
    void testApplyToTrack() throws InvalidMidiDataException {
        Sequence sequence;
        Track track;

        Block b1;
        Block b2;
        Note n1;
        Note n2;
        Note n3;
        Note n4;

        sequence = new Sequence(Sequence.PPQ, 10);

        track = sequence.createTrack();

        b1 = new Block(0);
        b2 = new Block(5);

        n1 = new Note(60, 60, 0, 5);
        n2 = new Note(56, 50, 4, 9);

        n3 = new Note(65, 90, 9, 17);
        n4 = new Note(64, 40, 30, 10);

        b1.addNote(n1);
        b1.addNote(n2);
        b2.addNote(n3);
        b2.addNote(n4);

        midiTrack.addBlock(b1);
        midiTrack.addBlock(b2);
        midiTrack.applyToTrack(track);

        ArrayList<MidiEvent> expectedMidiEvents = new ArrayList<>();

        MidiEvent n1on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 60), 0);
        MidiEvent n1off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 60), 5);

        MidiEvent n2on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 56, 50), 4);
        MidiEvent n2off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 56, 50), 13);

        MidiEvent n3on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 65, 90), 14);
        MidiEvent n3off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 65, 90), 31);

        MidiEvent n4on = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 64, 40), 35);
        MidiEvent n4off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 64, 40), 45);
        // as Track keeps them in playing order, we add them in playing order as well
        expectedMidiEvents.add(n1on);
        expectedMidiEvents.add(n2on);
        expectedMidiEvents.add(n1off);
        expectedMidiEvents.add(n2off);
        expectedMidiEvents.add(n3on);
        expectedMidiEvents.add(n3off);
        expectedMidiEvents.add(n4on);
        expectedMidiEvents.add(n4off);

        // since Track does not allow direct access to MidiEvent arraylist
        for (int i = 0; i < expectedMidiEvents.size(); i++) {
            MidiEvent expectedEvent = expectedMidiEvents.get(i);
            MidiEvent realEvent = track.get(i);

            assertEquals(expectedEvent.getMessage().getStatus(), realEvent.getMessage().getStatus());
            assertEquals(expectedEvent.getMessage().getMessage()[0], realEvent.getMessage().getMessage()[0]);
            assertEquals(expectedEvent.getMessage().getMessage()[1], realEvent.getMessage().getMessage()[1]);
            assertEquals(expectedEvent.getMessage().getMessage()[2], realEvent.getMessage().getMessage()[2]);
        }
    }
}
