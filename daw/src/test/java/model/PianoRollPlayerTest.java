package model;

import model.instrument.TonalInstrument;
import static org.junit.jupiter.api.Assertions.*;
import static persistance.UtilTest.skipIfHeadless;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import java.awt.*;

// See https://midi.org/expanded-midi-1-0-messages-list
// to understand the checking of bytes in MidiEvents in tests
public class PianoRollPlayerTest {

    private PianoRollPlayer pianoRollPlayer;
    private TimelineController timelineController;

    @BeforeEach
    void setup() {
        skipIfHeadless();
        timelineController = new TimelineController();
        MidiTrack mt = timelineController.getTimeline().createMidiTrack("piano", TonalInstrument.ACOUSTIC_GRAND_PIANO);
        Block b = new Block(0, 1920);
        b.addNote(new Note(60, 100, 0, 480));
        mt.addBlock(b);
        pianoRollPlayer = new PianoRollPlayer(b, mt, 120);
    }


    @Test
    void testConstructor() {
        assertEquals(pianoRollPlayer.getBlock(),
                timelineController.getTimeline().getMidiTracks().get(0).getBlock(0));
        assertEquals(pianoRollPlayer.getParentMidiTrack(), timelineController.getTimeline().getMidiTracks().get(0));
        assertEquals(120, pianoRollPlayer.getBPM());
        assertEquals(100, pianoRollPlayer.getVolume());
    }

    @Test
    void testSequence() throws InvalidMidiDataException {
        assertNotEquals(-1, pianoRollPlayer.getBlock().addNote(new Note(62, 80, 960, 960)));
        pianoRollPlayer.updateSequence();

        Track track = pianoRollPlayer.getSequence().getTracks()[0];
        assertEquals(7, track.size());
        byte[] message0 = track.get(0).getMessage().getMessage();
        byte[] message1 = track.get(1).getMessage().getMessage();
        byte[] message2 = track.get(2).getMessage().getMessage();
        byte[] message3 = track.get(3).getMessage().getMessage();
        byte[] message4 = track.get(4).getMessage().getMessage();
        byte[] message5 = track.get(5).getMessage().getMessage();
        byte[] message6 = track.get(6).getMessage().getMessage();

        assertEquals(ShortMessage.PROGRAM_CHANGE, message0[0] & 0xFF);
        assertEquals(0, message0[1] & 0xFF); // On channel 0
        // The third byte is not used
        assertEquals(0, track.get(0).getTick()); // At tick 0

        assertEquals(ShortMessage.CONTROL_CHANGE, message1[0] & 0xFF);
        assertEquals(7, message1[1] & 0xFF); // 7 is the volume controller
        assertEquals(100, message1[2] & 0xFF); // volume 100
        assertEquals(0, track.get(1).getTick()); // At tick 0

        assertEquals(ShortMessage.NOTE_ON, message2[0] & 0xFF);
        assertEquals(60, message2[1] & 0xFF); // With pitch 60
        assertEquals(100, message2[2] & 0xFF); // With velocity 100
        assertEquals(0, track.get(2).getTick()); // At tick 0

        assertEquals(ShortMessage.NOTE_OFF, message3[0] & 0xFF);
        assertEquals(60, message3[1] & 0xFF); // With pitch 60
        // can safely ignore the third byte, velocity not used for note off
        assertEquals(480, track.get(3).getTick());

        assertEquals(ShortMessage.NOTE_ON, message4[0] & 0xFF);
        assertEquals(62, message4[1] & 0xFF); // With pitch 60
        assertEquals(80, message4[2] & 0xFF); // With velocity 100
        assertEquals(960, track.get(4).getTick()); // At tick 0

        assertEquals(ShortMessage.NOTE_OFF, message5[0] & 0xFF);
        assertEquals(62, message5[1] & 0xFF); // With pitch 60
        // can safely ignore the third byte, velocity not used for note off
        assertEquals(1920, track.get(5).getTick());

        assertEquals(ShortMessage.SYSTEM_RESET, message6[0] & 0xFF);
        assertEquals(1920, track.get(6).getTick());
    }

    @Test
    void testSystemResetTick() throws InvalidMidiDataException {
        pianoRollPlayer.updateSequence();

    }

    @Test
    void testGetters() {
        assertEquals(2, pianoRollPlayer.getLengthBeats());
        assertEquals(1000, pianoRollPlayer.getLengthMs());
        pianoRollPlayer.getBlock().setDurationTicks(960);
        assertEquals(1, pianoRollPlayer.getLengthBeats());
        assertEquals(500, pianoRollPlayer.getLengthMs());
        pianoRollPlayer.setBPM(240);
        assertEquals(250, pianoRollPlayer.getLengthMs());

        assertEquals(100, pianoRollPlayer.getVolume());
        pianoRollPlayer.setVolume(120);
        assertEquals(120, pianoRollPlayer.getVolume());


    }
}
