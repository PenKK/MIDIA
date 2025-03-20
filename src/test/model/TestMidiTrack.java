package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.instrument.Instrument;
import model.instrument.InstrumentalInstrument;
import model.instrument.PercussionInstrument;

// Much of testing is done according to https://midi.org/spec-detail
public class TestMidiTrack {

    MidiTrack midiTrack;
    Instrument instr = InstrumentalInstrument.ACOUSTIC_GRAND_PIANO;

    @BeforeEach
    void runBefore() {
        midiTrack = new MidiTrack("Piano Melody", instr, 0);
    }

    @Test
    void testConstructor() {
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument().getProgramNumber(), 0); // 0 is piano
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getVolumeScaled(), 79); // 100 / 127
        assertEquals(midiTrack.getName(), "Piano Melody");
        assertEquals(midiTrack.getChannel(), 0);

        midiTrack = new MidiTrack("Percussive drums", PercussionInstrument.ACOUSTIC_BASS_DRUM, 9);
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument().getProgramNumber(), 35); // 35 is bass drum
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getVolumeScaled(), 79);
        assertEquals(midiTrack.getName(), "Percussive drums");
        assertEquals(midiTrack.getChannel(), 9);
    }

    @Test
    void testConstructorOverload() {
        midiTrack = new MidiTrack("Non Percussive", InstrumentalInstrument.ACOUSTIC_GUITAR_NYLON, 0);
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument().getProgramNumber(), 24);
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Non Percussive");

        midiTrack = new MidiTrack("Percussive", PercussionInstrument.ACOUSTIC_SNARE, 9); // 38 is acoustic snare
        assertFalse(midiTrack.isMuted());
        assertEquals(midiTrack.getBlocks(), new ArrayList<Block>());
        assertEquals(midiTrack.getInstrument().getProgramNumber(), 38);
        assertEquals(midiTrack.getVolume(), 100);
        assertEquals(midiTrack.getName(), "Percussive");
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
        assertEquals(midiTrack.getBlock(1), expectedBlocks.get(1));
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
    void testVolumeConversions() {
        midiTrack.setVolume(0);
        assertEquals(0, midiTrack.getVolume());
        midiTrack.setVolumeScaled(0);
        assertEquals(0, midiTrack.getVolume());
        assertEquals(0, midiTrack.getVolumeScaled());

        midiTrack.setVolume(127);
        assertEquals(127, midiTrack.getVolume());
        midiTrack.setVolumeScaled(100);
        assertEquals(127, midiTrack.getVolume());
        assertEquals(100, midiTrack.getVolumeScaled());

        midiTrack.setVolume(60);
        assertEquals(60, midiTrack.getVolume());
        assertEquals(47, midiTrack.getVolumeScaled());
        midiTrack.setVolumeScaled(100);
        assertEquals(127, midiTrack.getVolume());
        assertEquals(100, midiTrack.getVolumeScaled());
    }

    @Test
    void testSetInstrument() throws InvalidMidiDataException, MidiUnavailableException {
        Timeline timeline = new Timeline("test");
        midiTrack = timeline.createMidiTrack("Piano melody", instr, false);
        assertEquals(midiTrack.getInstrument().getProgramNumber(), 0);
        midiTrack.setInstrument(InstrumentalInstrument.ELECTRIC_PIANO_1);
        timeline.updateSequence();

        Track t = timeline.getSequence().getTracks()[0];
        assertEquals(t.get(0).getMessage().getMessage()[1], 4);

        midiTrack.setInstrument(InstrumentalInstrument.CELESTA);
        timeline.updateSequence();

        t = timeline.getSequence().getTracks()[0];
        assertEquals(t.get(0).getMessage().getMessage()[1], 8);
    }

    @Test
    void testApplyToTrack() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 960);
        Track track = sequence.createTrack();

        Block b1 = new Block(0);
        Block b2 = new Block(5);

        Note n1 = new Note(60, 60, 0, 5);
        Note n2 = new Note(56, 50, 4, 9);

        Note n3 = new Note(65, 90, 9, 17);
        Note n4 = new Note(64, 40, 30, 10);

        assertEquals(b1.addNote(n1), 0);
        assertEquals(b1.addNote(n2), 1);

        assertEquals(b2.addNote(n3), 0);
        assertEquals(b2.addNote(n4), 1);

        assertEquals(midiTrack.addBlock(b1), 0);
        assertEquals(midiTrack.addBlock(b2), 1);
        midiTrack.applyToTrack(track);

        ArrayList<MidiEvent> expectedMidiEvents = new ArrayList<>();

        MidiEvent n1on  = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 60, 60), 0);
        MidiEvent n1off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 60, 0), 5);
        MidiEvent n2on  = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 56, 50), 4);
        MidiEvent n2off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 56, 0), 13);
        MidiEvent n3on  = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 65, 90), 14);
        MidiEvent n3off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 65, 0), 31);
        MidiEvent n4on  = new MidiEvent(new ShortMessage(ShortMessage.NOTE_ON, 0, 64, 40), 35);
        MidiEvent n4off = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, 0, 64, 0), 45);

        MidiEvent programChangeEvent = new MidiEvent(new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                0, 0, 127), 0);
        MidiEvent volumeEvent = new MidiEvent(new ShortMessage(ShortMessage.CONTROL_CHANGE, 0, 7, 100), 0);

        expectedMidiEvents.add(programChangeEvent);
        expectedMidiEvents.add(volumeEvent);

        // as Track keeps them in playing order, we add them in playing order as well

        expectedMidiEvents.add(n1on);
        expectedMidiEvents.add(n2on);
        expectedMidiEvents.add(n1off);
        expectedMidiEvents.add(n2off);
        expectedMidiEvents.add(n3on);
        expectedMidiEvents.add(n3off);
        expectedMidiEvents.add(n4on);
        expectedMidiEvents.add(n4off);

        // We ignore the last element of track since it always ending signal
        assertEquals(expectedMidiEvents.size(), track.size() - 1);

        // since Java Track does not allow direct access to MidiEvent list, we loop
        for (int i = 0; i < expectedMidiEvents.size(); i++) {
            MidiMessage expectedEventData = expectedMidiEvents.get(i).getMessage();
            MidiMessage realEventData = track.get(i).getMessage();

            assertEquals(realEventData.getStatus(), expectedEventData.getStatus());
            assertEquals(realEventData.getMessage()[0], expectedEventData.getMessage()[0]);
            assertEquals(realEventData.getMessage()[1], expectedEventData.getMessage()[1]);
            if ((realEventData.getMessage()[0] & 0xFF) != ShortMessage.PROGRAM_CHANGE) { 
                // The last byte of program change events is unused, and can be random/unpredictable
                assertEquals(realEventData.getMessage()[2], expectedEventData.getMessage()[2]);
            }
        }
    }

    @Test
    void testPercussiveTrack() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 960);
        Track track = sequence.createTrack();

        midiTrack = new MidiTrack("percussive", PercussionInstrument.ACOUSTIC_BASS_DRUM, 9);
        assertTrue(midiTrack.isPercussive());
        midiTrack.applyToTrack(track);

        assertEquals(track.size(), 2); // volume, and end message, there should be no program change event

        byte[] volumeEventData = track.get(0).getMessage().getMessage();
        assertEquals(volumeEventData[0] & 0xFF, 185); // 185 is change to channel 10, which is percussive
        assertEquals(volumeEventData[1] & 0xFF, 7); // next byte is the function, 7 is channel volume
        assertEquals(volumeEventData[2] & 0xFF, 100); // set volume to 100

        sequence = new Sequence(Sequence.PPQ, 960);
        track = sequence.createTrack();

        Block block = new Block(0);
        Note note = new Note(0, 100, 0, 960);

        block.addNote(note);
        midiTrack.addBlock(block);
        midiTrack.setInstrument(PercussionInstrument.ELECTRIC_SNARE);
        midiTrack.applyToTrack(track);

        byte[] noteOnData = track.get(1).getMessage().getMessage();
        assertEquals(noteOnData[0] & 0xFF, 153); // Note on on channel 10
        assertEquals(noteOnData[1] & 0xFF, 40); // Note number 40, corresponding to instrument
        assertEquals(noteOnData[2] & 0xFF, 100); // Velocity of note

        byte[] noteOffData = track.get(2).getMessage().getMessage();
        assertEquals(noteOffData[0] & 0xFF, 137); // Note off on channel 10
        assertEquals(noteOffData[1] & 0xFF, 40); // Note number 40, corresponding to instrument
        assertEquals(noteOffData[2] & 0xFF, 0); // Velocity of note
    }

    @Test
    void testNameChange() {
        midiTrack.setName("Drums");
        assertEquals(midiTrack.getName(), "Drums");
        midiTrack.setName("Explosion sound");
        assertEquals(midiTrack.getName(), "Explosion sound");
    }

    @Test
    void testMidiInvalidDataException() throws InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 960);
        Block b = new Block(0);
        b.addNote(new Note(10000, 54346462, -345435, -43));
        midiTrack.addBlock(b);

        Track t = sequence.createTrack();
        try {
            midiTrack.applyToTrack(t);
            fail("Invalid midi data was not detected");
        } catch(RuntimeException e) {
            // success
        }

        b.removeNote(0); // Will now not throw
        midiTrack.applyToTrack(t); 

        Block blockWithInvalidNote = new Block(0);
        midiTrack.addBlock(blockWithInvalidNote);
        blockWithInvalidNote.addNote(new Note(128, 0, 0, 0)); // pitch is [0, 127]

        try {
            midiTrack.applyToTrack(t);
            fail();
        } catch (RuntimeException e) {
            // success
        }
    }
}
