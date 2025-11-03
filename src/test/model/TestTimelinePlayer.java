package model;

import org.junit.jupiter.api.Test;

import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;
import persistance.TestUtil;

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

// See https://midi.org/expanded-midi-1-0-messages-list 
// to understand the checking of bytes in MidiEvents in tests
public class TestTimelinePlayer extends TestUtil {
    Timeline timeline;
    ArrayList<Integer> expectedChannels;
    Instrument instr = TonalInstrument.ACOUSTIC_GRAND_PIANO;

    @BeforeEach
    void runBefore() throws MidiUnavailableException, InvalidMidiDataException {
        timeline = new TimelineController().getTimeline();
        timeline.setProjectName("test");
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
    void testPixelScaling() {
        assertEquals(5, timeline.scaleTickToPixel(50));
        timeline.setHorizontalScaleFactor(5);
        assertEquals(26, timeline.scaleTickToPixel(50));

        timeline.setHorizontalScaleFactor(1);
        assertEquals(48, timeline.scalePixelToTick(5));
        timeline.setHorizontalScaleFactor(5);
        assertEquals(50, timeline.scalePixelToTick(26));
    }

    @Test
    void testConstructor() throws MidiUnavailableException {
        assertEquals("test", timeline.getProjectName());
        assertEquals(new ArrayList<MidiTrack>(), timeline.getMidiTracks());
        assertEquals(120, timeline.getPlayer().getBPM());
        assertEquals(0, timeline.getPlayer().getTickPosition());
        assertEquals(4, timeline.getPlayer().getBeatDivision());
        assertEquals(4, timeline.getPlayer().getBeatsPerMeasure());
        assertEquals(1, timeline.getHorizontalScaleFactor());
        assertEquals(960, timeline.getPlayer().getSequence().getResolution());
        assertEquals(Sequence.PPQ, timeline.getPlayer().getSequence().getDivisionType());
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());
    }

    @Test
    void testAddMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();

        assertEquals(0, timeline.getMidiTracksArray().length);
        MidiTrack midiTrack = timeline.createMidiTrack("cool track", instr);
        expectedMidiTracks.add(midiTrack);
        assertEquals(timeline.getMidiTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(1, timeline.getMidiTracks().size());
        assertEquals(0, midiTrack.getChannel());
        assertEquals(timeline.getMidiTracksArray()[0], midiTrack);

        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        MidiTrack anotherMidiTrack = timeline.createMidiTrack("sick violin", instr);
        expectedMidiTracks.add(anotherMidiTrack);
        assertEquals(timeline.getMidiTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(1), expectedMidiTracks.get(1));
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(2, timeline.getMidiTracks().size());
        assertEquals(1, anotherMidiTrack.getChannel());

        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());
    }

    @Test
    void testRemoveMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();
        MidiTrack midiTrack = timeline.createMidiTrack("cool track", instr);
        MidiTrack anotherMidiTrack = timeline.createMidiTrack("sick violin", instr);

        expectedMidiTracks.add(midiTrack);
        expectedMidiTracks.add(anotherMidiTrack);

        expectedChannels.remove(0);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(2, timeline.getMidiTracks().size());
        assertEquals(timeline.removeMidiTrack(0), midiTrack);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(1, timeline.getMidiTracks().size());
        assertEquals(timeline.removeMidiTrack(0), anotherMidiTrack);
        assertEquals(0, timeline.getMidiTracks().size());
        expectedChannels.add(1);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());
    }

    @Test
    void testAddRemoveMidiTrack() {
        ArrayList<MidiTrack> expectedMidiTracks = new ArrayList<>();
        MidiTrack mt1 = timeline.createMidiTrack("cool track", instr);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        MidiTrack mt2 = timeline.createMidiTrack("sick violin", TonalInstrument.VIOLIN);
        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        MidiTrack mt3 = timeline.createMidiTrack("bass drum", PercussiveInstrument.ACOUSTIC_BASS_DRUM);
        // Do not remove any as the track is percussive
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        expectedMidiTracks.add(mt1);
        expectedMidiTracks.add(mt2);
        expectedMidiTracks.add(mt3);

        assertEquals(3, timeline.getMidiTracks().size());
        assertEquals(expectedMidiTracks, timeline.getMidiTracks());
        assertEquals(timeline.removeMidiTrack(2), mt3);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(2, timeline.getMidiTracks().size());
        assertEquals(timeline.removeMidiTrack(0), mt1);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(1, timeline.getMidiTracks().size());
        assertEquals(timeline.removeMidiTrack(0), mt2);
        expectedChannels.add(1);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());
        assertEquals(0, timeline.getMidiTracks().size());
    }

    @Test
    void testManyTracksAndPlayBack() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (!timeline.getPlayer().getAvailableChannels().isEmpty()) {
            midiTracks.add(timeline.createMidiTrack("instrumental", TonalInstrument.ELECTRIC_PIANO_1));
        }
        assertEquals(15, midiTracks.size());

        midiTracks.add(timeline.createMidiTrack("percussive", instr));
        assertNull(timeline.createMidiTrack("cant make more", instr));

        timeline.removeMidiTrack(5);
        assertEquals(1, timeline.getPlayer().getAvailableChannels().size());
        assertEquals(5, timeline.createMidiTrack("1 more", TonalInstrument.HARPSICHORD).getChannel());
        assertNull(timeline.createMidiTrack("cant make more", instr));

        assertTrue(timeline.getPlayer().getAvailableChannels().isEmpty());
        timeline.removeMidiTrack(10);
        assertEquals(1, timeline.getPlayer().getAvailableChannels().size());
        assertEquals(12, timeline.createMidiTrack("1 more", TonalInstrument.HARPSICHORD).getChannel());
    }

    @Test
    void testManyTracksCreate() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (!timeline.getPlayer().getAvailableChannels().isEmpty()) {
            midiTracks.add(timeline.createMidiTrack("instrumental", TonalInstrument.ELECTRIC_PIANO_1));
        }
        assertEquals(15, midiTracks.size());

        MidiTrack instrumentalTrack = timeline.createMidiTrack("inst", instr);
        MidiTrack percussionTrack = timeline.createMidiTrack("perc", PercussiveInstrument.HIGH_TOM);
        assertNull(instrumentalTrack);
        assertNotNull(percussionTrack);
    }

    @Test
    void testUpdateSequence() throws InvalidMidiDataException {
        MidiTrack mt1 = timeline.createMidiTrack("C notes", instr);
        MidiTrack mt2 = timeline.createMidiTrack("cool notes", TonalInstrument.ELECTRIC_PIANO_1);
        MidiTrack mt3 = timeline.createMidiTrack("basically muted", TonalInstrument.SYNTH_STRINGS_1);
        mt3.setVolume(0);
        mt2.setVolume(50);

        Block b1 = new Block(0, 1000);
        Block b2 = new Block(5, 1000);
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
        timeline.getPlayer().updateSequence();

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
            Track actualTrack = timeline.getPlayer().getSequence().getTracks()[i];

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
        Block b = new Block(0, 3000);
        Note n = new Note(60, 100, 0, 960);
        Note n2 = new Note(64, 127, 960, 960);
        b.addNote(n);
        b.addNote(n2);
        MidiTrack midiTrack = timeline.createMidiTrack("test", instr);
        midiTrack.addBlock(b);

        timeline.play();
        assertTrue(timeline.getPlayer().isPlaying());
        Thread.sleep((long) (timeline.getLengthMs() + 500));
        assertFalse(timeline.getPlayer().isPlaying());

        // test playback a second time
        b.removeNote(1);
        timeline.play();
        assertTrue(timeline.getPlayer().isPlaying());
        Thread.sleep((long) (timeline.getLengthMs() + 500));
        assertFalse(timeline.getPlayer().isPlaying());
        timeline.pause();

        // test play and pause
        timeline.play();
        assertTrue(timeline.getPlayer().isPlaying());
        timeline.pause();
        assertFalse(timeline.getPlayer().isPlaying());
        timeline.play();
        assertTrue(timeline.getPlayer().isPlaying());
        Thread.sleep((long) (timeline.getLengthMs() / 2) + 500);
        assertFalse(timeline.getPlayer().isPlaying());
    }

    @Test
    void testTimelinePosition() throws MidiUnavailableException {
        double roundingDelta = 0.01;

        addSampleSong(timeline);

        assertEquals(10000, timeline.getDurationRemainingMS());

        timeline.getPlayer().setTickPosition(30);
        assertEquals(30, timeline.getPlayer().getTickPosition());
        assertEquals(9984, timeline.getDurationRemainingMS());

        assertEquals(15.625, timeline.getPlayer().getPositionMs(), roundingDelta);
        assertEquals(0.03125, timeline.getPlayer().getPositionBeats(), roundingDelta); // 30 ms is very short
        assertEquals(0.03125 + 1, timeline.getPlayer().getPositionOnBeat(), roundingDelta);
        timeline.getPlayer().setPositionMs(15.625);
        assertEquals(30, timeline.getPlayer().getTickPosition());
        assertEquals(9984, timeline.getDurationRemainingMS());

        timeline.getPlayer().setTickPosition(20);
        assertEquals(20, timeline.getPlayer().getTickPosition());
        assertEquals(10.42, timeline.getPlayer().getPositionMs(), roundingDelta);
        assertEquals(9989, timeline.getDurationRemainingMS());

        timeline.getPlayer().setBPM(240);
        timeline.getPlayer().setTickPosition(20);
        assertEquals(20, timeline.getPlayer().getTickPosition());
        assertEquals(5.2, timeline.getPlayer().getPositionMs(), roundingDelta);
        assertEquals(0.0208, timeline.getPlayer().getPositionBeats(), roundingDelta);
        assertEquals(0.0208 + 1, timeline.getPlayer().getPositionOnBeat(), roundingDelta);
        assertEquals(4994, timeline.getDurationRemainingMS());

        timeline.getPlayer().setTickPosition(0);
        assertEquals(0, timeline.getPlayer().getTickPosition());
        assertEquals(0, timeline.getPlayer().getPositionBeats());
        assertEquals(1.0, timeline.getPlayer().getPositionOnBeat());
        assertEquals(5000, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionBeat(11);
        assertEquals(2500, timeline.getPlayer().getPositionMs());
        assertEquals(2500, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionBeat(1);
        assertEquals(0, timeline.getPlayer().getPositionMs());
        assertEquals(5000, timeline.getDurationRemainingMS());
    }

    @Test
    void testMutedTrackUpdateTimeline() throws InvalidMidiDataException {
        MidiTrack midiTrack1 = timeline.createMidiTrack("track", instr);
        MidiTrack midiTrack2 = timeline.createMidiTrack("track", instr);
        MidiTrack midiTrack3 = timeline.createMidiTrack("track", instr);
        MidiTrack midiTrack4 = timeline.createMidiTrack("track", instr);

        midiTrack1.setMuted(true);
        midiTrack2.setMuted(true);
        midiTrack3.setMuted(true);
        midiTrack4.setMuted(true);

        timeline.getPlayer().updateSequence();
        assertEquals(0, timeline.getPlayer().getSequence().getTracks().length);

        midiTrack1.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(1, timeline.getPlayer().getSequence().getTracks().length);

        midiTrack2.setMuted(false);
        midiTrack3.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(3, timeline.getPlayer().getSequence().getTracks().length);

        midiTrack3.setMuted(true);
        timeline.getPlayer().updateSequence();
        assertEquals(2, timeline.getPlayer().getSequence().getTracks().length);

        midiTrack3.setMuted(false);
        midiTrack4.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(4, timeline.getPlayer().getSequence().getTracks().length);
    }

    @Test
    void testTempoChange() {
        MidiTrack testTrack = timeline.createMidiTrack("track", instr);
        Block testBlock = new Block(0, 1000);
        Note testNote = new Note(60, 100, 0, 960);

        testBlock.addNote(testNote);
        testTrack.addBlock(testBlock);

        // PPQ = 960, note ends at 960, and the bpm is 120.
        // So 1 quarter note at 120 BPM
        // 120 BPM = 0.5 seconds per quarter note
        assertEquals(500, timeline.getLengthMs());
        assertEquals(960, timeline.getPlayer().msToTicks(500)); // Check the reverse
        assertEquals(1, timeline.getLengthBeats()); // 1 beat = 1 quarter note
        MidiTrack testTrack2 = timeline.createMidiTrack("track2", instr);
        Block testBlock2 = new Block(960, 1920);
        Note testNote2 = new Note(60, 100, 0, 1920);

        assertNotEquals(-1, testBlock2.addNote(testNote2));
        testTrack2.addBlock(testBlock2);

        assertEquals(1500, timeline.getLengthMs());
        assertEquals(1920 + 960, timeline.getPlayer().msToTicks(1500)); // Check the reverse
        assertEquals(3, timeline.getLengthBeats());

        timeline.getPlayer().setBPM(240); // double BPM
        assertEquals(750, timeline.getLengthMs()); // ms halves

        testBlock2.addNote(new Note(60, 60, 231, 500));
        assertEquals(750, timeline.getLengthMs());
    }

    @Test
    void testBeats() {
        assertEquals(1, timeline.getPlayer().ticksToBeats(960));
        assertEquals(1 + 1, timeline.getPlayer().ticksToOnBeat(960));
        assertEquals(960, timeline.getPlayer().beatsToTicks(1));
        assertEquals(1.5, timeline.getPlayer().ticksToBeats(960 + 960 / 2));
        assertEquals(0.25, timeline.getPlayer().ticksToBeats(960 / 4));
        assertEquals(0.25 + 1, timeline.getPlayer().ticksToOnBeat(960 / 4));
        assertEquals(500, timeline.getPlayer().beatsToMs(1));

        timeline.getPlayer().setBPM(100);
        // BPM does not change the conversion as beats = ticks / PPQN
        assertEquals(0.25, timeline.getPlayer().ticksToBeats(960 / 4)); // remains same

        timeline.getPlayer().setPositionBeat(5);
        assertEquals(4 * 960, timeline.getPlayer().getTickPosition());
        timeline.getPlayer().setPositionBeat(1.5);
        assertEquals(960 / 2, timeline.getPlayer().getTickPosition());
    }

    @Test
    void testProjectNameChange() {
        assertEquals("test", timeline.getProjectName());
        timeline.setProjectName("cool song");
        assertEquals("cool song", timeline.getProjectName());
        timeline.setProjectName("cooler song");
        assertEquals("cooler song", timeline.getProjectName());
    }

    @Test
    void testTickSnapping() {
        assertEquals(720, timeline.getPlayer().snapTickLowerDivision(959));
        assertEquals(960, timeline.getPlayer().snapTickLowerDivision(960));
        assertEquals(960, timeline.getPlayer().snapTickLowerDivision(961));
        assertEquals(960, timeline.getPlayer().snapTickLowerDivision(960 + 960 / 4 - 1));
        assertEquals(1200, timeline.getPlayer().snapTickLowerDivision(960 + 960 / 4 - 0));

        assertEquals(960, timeline.getPlayer().snapTickNearest(959));
        assertEquals(960, timeline.getPlayer().snapTickNearest(960));
        assertEquals(960, timeline.getPlayer().snapTickNearest(961));
        assertEquals(960, timeline.getPlayer().snapTickNearest(960 + 960 / 8 - 1));
        assertEquals(1200, timeline.getPlayer().snapTickNearest(960 + 960 / 8 - 0));
    }
}
