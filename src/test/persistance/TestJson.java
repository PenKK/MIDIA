package persistance;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class TestJson {
    protected void checkTimeline(Timeline timeline1, Timeline timeline2) {
        assertEquals(timeline1.getProjectName(), timeline2.getProjectName());
        assertEquals(timeline1.getBPM(), timeline2.getBPM());
        assertEquals(timeline1.getPositionTick(), timeline2.getPositionTick());
        assertEquals(timeline1.getAvaliableInstrumentalChannels(), 
                     timeline2.getAvaliableInstrumentalChannels());

        checkMidiTracks(timeline1.getTracks(), timeline2.getTracks());
    }

    protected void checkMidiTracks(ArrayList<MidiTrack> midiTracks1, ArrayList<MidiTrack> midiTracks2) {
        assertEquals(midiTracks1.size(), midiTracks2.size());
        for (int i = 0; i < midiTracks1.size(); i++) {
            MidiTrack mt1 = midiTracks1.get(i);
            MidiTrack mt2 = midiTracks2.get(i);

            checkMidiTrack(mt1, mt2);
        }
    }

    protected void checkMidiTrack(MidiTrack midiTrack1, MidiTrack midiTrack2) {
        assertEquals(midiTrack1.getChannel(), midiTrack2.getChannel());
        assertEquals(midiTrack1.getInstrument(), midiTrack2.getInstrument());
        assertEquals(midiTrack1.getVolume(), midiTrack2.getVolume());
        assertEquals(midiTrack1.getName(), midiTrack2.getName());
        
        assertEquals(midiTrack1.getBlocks().size(), midiTrack2.getBlocks().size());
        for (int i = 0; i < midiTrack1.getBlocks().size(); i++) {
            Block b1 = midiTrack1.getBlock(i);
            Block b2 = midiTrack2.getBlock(i);
            checkBlock(b1, b2);
        }
    }

    protected void checkBlock(Block block1, Block block2) {
        assertEquals(block1.getStartTick(), block2.getStartTick());

        assertEquals(block1.getNotes().size(), block2.getNotes().size());
        for (int i = 0; i < block1.getNotes().size(); i++) {
            Note n1 = block1.getNotes().get(i);
            Note n2 = block2.getNotes().get(i);

            checkNote(n1, n2);
        }
    }

    protected void checkNote(Note note1, Note note2) {
        assertEquals(note1.getPitch(), note2.getPitch());
        assertEquals(note1.getDurationTicks(), note2.getDurationTicks());
        assertEquals(note1.getStartTick(), note2.getStartTick());
        assertEquals(note1.getVelocity(), note2.getVelocity());
    }
}
