package persistance;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Player;
import model.Timeline;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class UtilTest {

    public static final Path TEST_PATH = Paths.get( "src", "test", "json-tests");
    public static final Path TEST_PATH_WRITE = TEST_PATH.resolve("write");
    public static final Path TEST_PATH_READ = TEST_PATH.resolve("read");

    public static String getReadFilePath(String filename) throws IOException {
        Files.createDirectories(TEST_PATH_READ);
        return TEST_PATH_READ.resolve(filename).toString();
    }

    public static String getWriteFilePath(String filename) throws IOException {
        Files.createDirectories(TEST_PATH_WRITE);
        return TEST_PATH_WRITE.resolve(filename).toString();
    }

    public static void assertTimelineEquals(Timeline timeline1, Timeline timeline2) {
        assertEquals(timeline1.getProjectName(), timeline2.getProjectName());
        assertEquals(timeline1.getPlayer().getBeatDivision(), timeline2.getPlayer().getBeatDivision());
        assertEquals(timeline1.getPlayer().getBeatsPerMeasure(), timeline2.getPlayer().getBeatsPerMeasure());
        assertEquals(timeline1.getHorizontalScaleFactor(), timeline2.getHorizontalScaleFactor());
        assertPlayerEquals(timeline1.getPlayer(), timeline2.getPlayer());

        assertMidiTracksEquals(timeline1.getMidiTracks(), timeline2.getMidiTracks());
    }

    public static void assertPlayerEquals(Player p1, Player p2) {
        assertEquals(p1.getBPM(), p2.getBPM());
        assertEquals(p1.getTickPosition(), p2.getTickPosition());
        assertEquals(p1.getAvailableChannels(), p2.getAvailableChannels());
    }

    public static void assertMidiTracksEquals(ArrayList<MidiTrack> midiTracks1, ArrayList<MidiTrack> midiTracks2) {
        assertEquals(midiTracks1.size(), midiTracks2.size());
        for (int i = 0; i < midiTracks1.size(); i++) {
            MidiTrack mt1 = midiTracks1.get(i);
            MidiTrack mt2 = midiTracks2.get(i);

            assertMidiTrackEquals(mt1, mt2);
        }
    }

    public static void assertMidiTrackEquals(MidiTrack midiTrack1, MidiTrack midiTrack2) {
        assertEquals(midiTrack1.getChannel(), midiTrack2.getChannel());
        assertEquals(midiTrack1.getInstrument(), midiTrack2.getInstrument());
        assertEquals(midiTrack1.getVolume(), midiTrack2.getVolume());
        assertEquals(midiTrack1.getName(), midiTrack2.getName());

        assertEquals(midiTrack1.getBlocks().size(), midiTrack2.getBlocks().size());
        for (int i = 0; i < midiTrack1.getBlocks().size(); i++) {
            Block b1 = midiTrack1.getBlock(i);
            Block b2 = midiTrack2.getBlock(i);
            assertBlockEquals(b1, b2);
        }
    }

    public static void assertBlockEquals(Block block1, Block block2) {
        assertEquals(block1.getStartTick(), block2.getStartTick());
        assertEquals(block1.getDurationTicks(), block2.getDurationTicks());

        assertEquals(block1.getNotes().size(), block2.getNotes().size());
        for (int i = 0; i < block1.getNotes().size(); i++) {
            Note n1 = block1.getNotes().get(i);
            Note n2 = block2.getNotes().get(i);

            assertNoteEquals(n1, n2);
        }
    }

    public static void assertNoteEquals(Note note1, Note note2) {
        assertEquals(note1.getPitch(), note2.getPitch());
        assertEquals(note1.getDurationTicks(), note2.getDurationTicks());
        assertEquals(note1.getStartTick(), note2.getStartTick());
        assertEquals(note1.getVelocity(), note2.getVelocity());
    }

    public static void addSampleSong(Timeline timeline) {
        MidiTrack melody = timeline.createMidiTrack("synth pad", TonalInstrument.PAD_2);
        MidiTrack drums = timeline.createMidiTrack("bass drum", PercussiveInstrument.ACOUSTIC_BASS_DRUM);
        MidiTrack bass = timeline.createMidiTrack("bass", TonalInstrument.ELECTRIC_BASS_FRETLESS);
        MidiTrack hiHat = timeline.createMidiTrack("hi-hat", PercussiveInstrument.CLOSED_HI_HAT);

        melody.setVolume(127);
        drums.setVolume(110);
        bass.setVolume(110);

        final long beatTicks = timeline.getPlayer().beatsToTicks(1);

        Block melodyBlock = new Block(0, beatTicks * 19);
        Block drumsBlock = new Block(0, 20 * Player.PULSES_PER_QUARTER_NOTE);
        Block bassBlock = new Block(beatTicks, (long) ((beatTicks * 4.5) + ((double) beatTicks / 2)));
        Block hiHatBlock = new Block(beatTicks * 4, beatTicks * 14);

        melody.addBlock(melodyBlock);
        drums.addBlock(drumsBlock);
        bass.addBlock(bassBlock);
        hiHat.addBlock(hiHatBlock);

        for (int beat = 0; beat < 20; beat++) {
            drumsBlock.addNote(new Note(0, 127, beatTicks * beat, beatTicks));
        }

        for (int beat = 0; beat < 14; beat++) {
            int velocity = beat % 4 == 0 ? 127 : 76; // louder first hit-hat of measure
            hiHatBlock.addNote(new Note(0, velocity, beat * beatTicks + beatTicks / 2, beatTicks / 2));
        }

        melodyBlock.addNote(new Note(60, 127, beatTicks * 4, beatTicks * 2));
        melodyBlock.addNote(new Note(62, 127, beatTicks * 6, beatTicks));
        melodyBlock.addNote(new Note(56, 127, beatTicks * 7, beatTicks * 4));

        melodyBlock.addNote(new Note(60, 127, beatTicks * 12, beatTicks * 2));
        melodyBlock.addNote(new Note(62, 127, beatTicks * 14, beatTicks));
        melodyBlock.addNote(new Note(66, 127, beatTicks * 15, beatTicks * 4));

        bassBlock.addNote(new Note(32, 100, beatTicks, beatTicks / 2));
        bassBlock.addNote(new Note(32, 100, (int) (beatTicks * 1.5), beatTicks / 2));

        bassBlock.addNote(new Note(32, 100, beatTicks * 4, beatTicks / 2));
        bassBlock.addNote(new Note(32, 100, (int) (beatTicks * 4.5), beatTicks / 2));

        Block bassBlock2 = bassBlock.clone();
        bassBlock2.setStartTick(beatTicks * 11);
        bass.addBlock(bassBlock2);
    }

    
    public static void checkNotesEqual(ArrayList<Note> l1, ArrayList<Note> l2) {
        assertEquals(l1.size(), l2.size());
        for (int i = 0; i < l1.size(); i++) {
            assertEquals(l1.get(i).getPitch(), l2.get(i).getPitch());
            assertEquals(l1.get(i).getStartTick(), l2.get(i).getStartTick());
            assertEquals(l1.get(i).getDurationTicks(), l2.get(i).getDurationTicks());
            assertEquals(l1.get(i).getVelocity(), l2.get(i).getVelocity());
        }
    }

}
