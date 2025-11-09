package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.editing.DawClipboard;
import persistance.TestUtil;

@SuppressWarnings("PointlessArithmeticExpression")
public class TestBlock {
    Block block;

    @BeforeEach
    void runBefore() {
        block = new Block(10, 1000);
    }

    @Test
    void testConstructor() {
        assertEquals(new ArrayList<Note>(), block.getNotes());
        assertEquals(10, block.getStartTick());
        assertEquals("S: 10, N: 0", block.toString());
        assertEquals(1000, block.getDurationTicks());
        assertEquals("Start tick: 10, duration: 1000, current note count: 0", block.info());
    }

    @Test
    void testDurationSetter() {
        assertEquals(1000, block.getDurationTicks());
        block.setDurationTicks(300);
        assertEquals(300, block.getDurationTicks());
    }

    @Test
    void testAddNote() {
        Note note1 = new Note(60, 60, 0, 5);
        Note note2 = new Note(56, 50, 4, 9);
        Note note3 = new Note(65, 90, 9, 17);
        ArrayList<Note> expectedNotes = new ArrayList<>();

        assertEquals(0, block.addNote(note1));
        expectedNotes.add(note1);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(1, block.addNote(note2));
        expectedNotes.add(note2);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(2, block.addNote(note3));
        expectedNotes.add(note3);
        assertEquals(block.getNotes(), expectedNotes);

        block.getNotes().clear();
        assertEquals(0, block.getNotes().size());

        assertEquals(0, block.addNote(note1));
        assertEquals(1, block.addNote(note2));
        assertEquals(2, block.addNote(note3));
    }

    @Test
    void testAddNoteOutOfBounds() {
        Note note1 = new Note(60, 60, 0, 5);
        Note note11 = new Note(65, 90, 0, 999);
        Note note2 = new Note(56, 50, 4, 9);
        Note note22 = new Note(65, 90, 0, 1000);
        Note note3 = new Note(65, 90, 9, 17);
        Note note33 = new Note(65, 90, 0, 1001);
        Note note44 = new Note(65, 90, 1, 1000);
        ArrayList<Note> expectedNotes = new ArrayList<>();

        assertEquals(0, block.addNote(note1));
        expectedNotes.add(note1);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(1, block.addNote(note2));
        expectedNotes.add(note2);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(2, block.addNote(note3));
        expectedNotes.add(note3);
        assertEquals(block.getNotes(), expectedNotes);

        block.getNotes().clear();
        assertEquals(0, block.getNotes().size());

        assertEquals(0, block.addNote(note1));
        assertEquals(1, block.addNote(note2));
        assertEquals(2, block.addNote(note3));

        assertEquals(3, block.addNote(note11));
        assertEquals(4, block.addNote(note22));
        assertEquals(-1, block.addNote(note33));
        assertEquals(-1, block.addNote(note44));

        assertEquals(5, block.getNotes().size());
    }

    @Test
    void removeNote() {
        Note note1 = new Note(60, 60, 0, 5);
        Note note2 = new Note(65, 90, 9, 17);
        Note note3 = new Note(56, 50, 4, 9);
        ArrayList<Note> expectedNotes = new ArrayList<>();

        block.addNote(note1);
        block.addNote(note2);
        block.addNote(note3);

        expectedNotes.add(note1);
        expectedNotes.add(note2);
        expectedNotes.add(note3);

        assertEquals(block.removeNote(2), note3);
        expectedNotes.remove(2);
        assertEquals(expectedNotes, block.getNotes());

        assertEquals(block.removeNote(0), note1);
        expectedNotes.remove(0);
        assertEquals(expectedNotes, block.getNotes());

        assertEquals(block.removeNote(0), note2);
        expectedNotes.remove(0);
        assertEquals(expectedNotes.isEmpty(), block.getNotes().isEmpty());
    }

    @Test
    void testGetNotesTimeline() {
        Note note1 = new Note(60, 60, 0, 5);
        Note note2 = new Note(65, 90, 9, 17);
        Note note3 = new Note(56, 50, 4, 9);

        block.addNote(note1);
        block.addNote(note2);
        block.addNote(note3);

        ArrayList<Note> expectedNotes = new ArrayList<>();
        expectedNotes.add(new Note(60, 60, 0 + 10, 5));
        expectedNotes.add(new Note(65, 90, 9 + 10, 17));
        expectedNotes.add(new Note(56, 50, 4 + 10, 9));

        TestUtil.checkNotesEqual(expectedNotes, block.getNotesTimeline());

        expectedNotes.clear();

        block.setStartTick(4);
        expectedNotes.add(new Note(60, 60, 0 + 4, 5));
        expectedNotes.add(new Note(65, 90, 9 + 4, 17));
        expectedNotes.add(new Note(56, 50, 4 + 4, 9));

        TestUtil.checkNotesEqual(expectedNotes, block.getNotesTimeline());

        block.setStartTick(0);
        expectedNotes.clear();

        expectedNotes.add(new Note(60, 60, 0 + 0, 5));
        expectedNotes.add(new Note(65, 90, 9 + 0, 17));
        expectedNotes.add(new Note(56, 50, 4 + 0, 9));
        TestUtil.checkNotesEqual(expectedNotes, block.getNotesTimeline());
    }

    @Test
    void testClone() {
        block.addNote(new Note(42, 60, 22, 100));
        block.addNote(new Note(22, 20, 88, 900));
        Block cloneBlock = block.clone();

        assertNotEquals(block, cloneBlock);
        assertEquals(block.getNotes().size(), cloneBlock.getNotes().size());
        assertEquals(block.getNotesTimeline().size(), cloneBlock.getNotesTimeline().size());
        assertEquals(block.getStartTick(), cloneBlock.getStartTick());
        assertEquals(block.getDurationTicks(), cloneBlock.getDurationTicks());
    }

    @Test
    void testPaste() {
        Note n = new Note(0, 0, 0, 0);
        DawClipboard dawClipboard = new DawClipboard();
        assertTrue(dawClipboard.isEmpty());
        dawClipboard.copy(List.of(n));
        assertFalse(dawClipboard.isEmpty());

        assertEquals(0, block.getNotes().size());
        block.paste(dawClipboard.getContents(), 0);
        assertEquals(1, block.getNotes().size());

        n.setPitch(100);
        assertEquals(0, block.getNotes().get(0).getPitch());
    }

    @Test
    void testOddPaste() {
        DawClipboard dawClipboard = new DawClipboard();
        dawClipboard.copy(List.of(block));

        assertEquals(0, block.getNotes().size());
        block.paste(dawClipboard.getContents(), 0);
        assertEquals(0, block.getNotes().size());
    }
}