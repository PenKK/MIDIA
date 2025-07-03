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
        assertEquals(timeline.scaleTickToPixel(50), 5);
        timeline.setHorizontalScaleFactor(5);
        assertEquals(timeline.scaleTickToPixel(50), 26);

        timeline.setHorizontalScaleFactor(1);
        assertEquals(timeline.scalePixelToTick(5), 48);
        timeline.setHorizontalScaleFactor(5);
        assertEquals(timeline.scalePixelToTick(26), 50);
    }

    @Test
    void testConstructor() throws MidiUnavailableException {
        assertEquals(timeline.getProjectName(), "test");
        assertEquals(timeline.getMidiTracks(), new ArrayList<MidiTrack>());
        assertEquals(timeline.getPlayer().getBPM(), 120);
        assertEquals(timeline.getPlayer().getPositionTick(), 0);
        assertEquals(timeline.getBeatDivision(), 4);
        assertEquals(timeline.getBeatsPerMeasure(), 4);
        assertEquals(timeline.getHorizontalScaleFactor(), 1);
        assertEquals(timeline.getPlayer().getSequence().getResolution(), 960);
        assertEquals(timeline.getPlayer().getSequence().getDivisionType(), Sequence.PPQ);
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
        assertEquals(timeline.getMidiTracks().size(), 1);
        assertEquals(midiTrack.getChannel(), 0);
        assertEquals(timeline.getMidiTracksArray()[0], (new MidiTrack[] { midiTrack })[0]);

        expectedChannels.remove(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        MidiTrack anotherMidiTrack = timeline.createMidiTrack("sick violin", instr);
        expectedMidiTracks.add(anotherMidiTrack);
        assertEquals(timeline.getMidiTracks(), expectedMidiTracks);
        assertEquals(timeline.getTrack(1), expectedMidiTracks.get(1));
        assertEquals(timeline.getTrack(0), expectedMidiTracks.get(0));
        assertEquals(timeline.getMidiTracks().size(), 2);
        assertEquals(anotherMidiTrack.getChannel(), 1);

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

        assertEquals(timeline.getMidiTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), midiTrack);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(timeline.getMidiTracks().size(), 1);
        assertEquals(timeline.removeMidiTrack(0), anotherMidiTrack);
        assertEquals(timeline.getMidiTracks().size(), 0);
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

        assertEquals(timeline.getMidiTracks().size(), 3);
        assertEquals(timeline.removeMidiTrack(2), mt3);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(timeline.getMidiTracks().size(), 2);
        assertEquals(timeline.removeMidiTrack(0), mt1);
        expectedChannels.add(0);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());

        assertEquals(timeline.getMidiTracks().size(), 1);
        assertEquals(timeline.removeMidiTrack(0), mt2);
        expectedChannels.add(1);
        assertEquals(expectedChannels, timeline.getPlayer().getAvailableChannels());
        assertEquals(timeline.getMidiTracks().size(), 0);
    }

    @Test
    void testManyTracksAndPlayBack() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (timeline.getPlayer().getAvailableChannels().size() != 0) {
            midiTracks.add(timeline.createMidiTrack("instrumental", TonalInstrument.ELECTRIC_PIANO_1));
        }
        assertTrue(midiTracks.size() == 15);

        midiTracks.add(timeline.createMidiTrack("percussive", instr));
        assertEquals(timeline.createMidiTrack("cant make more", instr), null);

        timeline.removeMidiTrack(5);
        assertTrue(timeline.getPlayer().getAvailableChannels().size() == 1);
        assertEquals(timeline.createMidiTrack("1 more", TonalInstrument.HARPSICHORD).getChannel(), 5);
        assertEquals(timeline.createMidiTrack("cant make more", instr), null);

        assertTrue(timeline.getPlayer().getAvailableChannels().isEmpty());
        timeline.removeMidiTrack(10);
        assertTrue(timeline.getPlayer().getAvailableChannels().size() == 1);
        assertEquals(timeline.createMidiTrack("1 more", TonalInstrument.HARPSICHORD).getChannel(), 12);
    }

    @Test
    void testManyTracksCreate() {
        ArrayList<MidiTrack> midiTracks = new ArrayList<>();

        while (timeline.getPlayer().getAvailableChannels().size() != 0) {
            midiTracks.add(timeline.createMidiTrack("instrumental", TonalInstrument.ELECTRIC_PIANO_1));
        }
        assertTrue(midiTracks.size() == 15);

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

        timeline.getPlayer().setPositionTick(30);
        assertEquals(timeline.getPlayer().getPositionTick(), 30);
        assertEquals(9984, timeline.getDurationRemainingMS());

        assertEquals(timeline.getPlayer().getPositionMs(), 15.625, roundingDelta);
        assertEquals(timeline.getPlayer().getPositionBeats(), 0.03125, roundingDelta); // 30 ms is very short
        assertEquals(timeline.getPlayer().getPositionOnBeat(), 0.03125 + 1, roundingDelta);
        timeline.getPlayer().setPositionMs(15.625);
        assertEquals(timeline.getPlayer().getPositionTick(), 30);
        assertEquals(9984, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionTick(20);
        assertEquals(timeline.getPlayer().getPositionTick(), 20);
        assertEquals(timeline.getPlayer().getPositionMs(), 10.42, roundingDelta);
        assertEquals(9989, timeline.getDurationRemainingMS());

        timeline.getPlayer().setBPM(240);
        timeline.getPlayer().setPositionTick(20);
        assertEquals(timeline.getPlayer().getPositionTick(), 20);
        assertEquals(timeline.getPlayer().getPositionMs(), 5.2, roundingDelta);
        assertEquals(timeline.getPlayer().getPositionBeats(), 0.0208, roundingDelta);
        assertEquals(timeline.getPlayer().getPositionOnBeat(), 0.0208 + 1, roundingDelta);
        assertEquals(4994, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionTick(0);
        assertEquals(timeline.getPlayer().getPositionTick(), 0);
        assertEquals(timeline.getPlayer().getPositionBeats(), 0);
        assertEquals(timeline.getPlayer().getPositionOnBeat(), 1.0);
        assertEquals(5000, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionBeat(11);
        assertEquals(timeline.getPlayer().getPositionMs(), 2500);
        assertEquals(2500, timeline.getDurationRemainingMS());

        timeline.getPlayer().setPositionBeat(1);
        assertEquals(timeline.getPlayer().getPositionMs(), 0);
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
        assertEquals(timeline.getPlayer().getSequence().getTracks().length, 0);

        midiTrack1.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(timeline.getPlayer().getSequence().getTracks().length, 1);

        midiTrack2.setMuted(false);
        midiTrack3.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(timeline.getPlayer().getSequence().getTracks().length, 3);

        midiTrack3.setMuted(true);
        timeline.getPlayer().updateSequence();
        assertEquals(timeline.getPlayer().getSequence().getTracks().length, 2);

        midiTrack3.setMuted(false);
        midiTrack4.setMuted(false);
        timeline.getPlayer().updateSequence();
        assertEquals(timeline.getPlayer().getSequence().getTracks().length, 4);
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
        assertEquals(timeline.getLengthMs(), 500);
        assertEquals(timeline.getPlayer().msToTicks(500), 960); // Check the reverse
        assertEquals(timeline.getLengthBeats(), 1); // 1 beat = 1 quarter note
        MidiTrack testTrack2 = timeline.createMidiTrack("track2", instr);
        Block testBlock2 = new Block(960, 1920);
        Note testNote2 = new Note(60, 100, 0, 1920);

        assertNotEquals(testBlock2.addNote(testNote2), -1);
        testTrack2.addBlock(testBlock2);

        assertEquals(timeline.getLengthMs(), 1500);
        assertEquals(timeline.getPlayer().msToTicks(1500), 1920 + 960); // Check the reverse
        assertEquals(timeline.getLengthBeats(), 3);

        timeline.getPlayer().setBPM(240); // double BPM
        assertEquals(timeline.getLengthMs(), 750); // ms halves

        testBlock2.addNote(new Note(60, 60, 231, 500));
        assertEquals(timeline.getLengthMs(), 750);
    }

    @Test
    void testBeats() {
        assertEquals(timeline.getPlayer().ticksToBeats(960), 1);
        assertEquals(timeline.getPlayer().ticksToOnBeat(960), 1 + 1);
        assertEquals(timeline.getPlayer().beatsToTicks(1), 960);
        assertEquals(timeline.getPlayer().ticksToBeats(960 + 960 / 2), 1.5);
        assertEquals(timeline.getPlayer().ticksToBeats(960 / 4), 0.25);
        assertEquals(timeline.getPlayer().ticksToOnBeat(960 / 4), 0.25 + 1);
        assertEquals(timeline.getPlayer().beatsToMs(1), 500);

        timeline.getPlayer().setBPM(100);
        // BPM does not change the converstion as beats = ticks / PPQN
        assertEquals(timeline.getPlayer().ticksToBeats(960 / 4), 0.25); // remains same

        timeline.getPlayer().setPositionBeat(5);
        assertEquals(timeline.getPlayer().getPositionTick(), 4 * 960);
        timeline.getPlayer().setPositionBeat(1.5);
        assertEquals(timeline.getPlayer().getPositionTick(), 960 / 2);
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
    void testTickSnapping() {
        assertEquals(timeline.snapTickLower(959), 720);
        assertEquals(timeline.snapTickLower(960), 960);
        assertEquals(timeline.snapTickLower(961), 960);
        assertEquals(timeline.snapTickLower(960 + 960 / 4 - 1), 960);
        assertEquals(timeline.snapTickLower(960 + 960 / 4 - 0), 1200);

        assertEquals(timeline.snapTickNearest(959), 960);
        assertEquals(timeline.snapTickNearest(960), 960);
        assertEquals(timeline.snapTickNearest(961), 960);
        assertEquals(timeline.snapTickNearest(960 + 960 / 8 - 1), 960);
        assertEquals(timeline.snapTickNearest(960 + 960 / 8 - 0), 1200);
    }
}
