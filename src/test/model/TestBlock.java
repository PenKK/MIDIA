package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.editing.DawClipboard;
import persistance.TestUtil;

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
        assertEquals(block.toString(), "S: 10, N: 0");
        assertEquals(block.getDurationTicks(), 1000);
        assertEquals(block.info(), "Start tick: 10, duration: 1000, current note count: 0");
    }

    @Test
    void testAddNote() {
        Note note1 = new Note(60, 60, 0, 5);
        Note note2 = new Note(56, 50, 4, 9);
        Note note3 = new Note(65, 90, 9, 17);
        ArrayList<Note> expectedNotes = new ArrayList<>();

        assertEquals(block.addNote(note1), 0);
        expectedNotes.add(note1);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(block.addNote(note2), 1);
        expectedNotes.add(note2);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(block.addNote(note3), 2);
        expectedNotes.add(note3);
        assertEquals(block.getNotes(), expectedNotes);

        block.getNotes().clear();
        assertEquals(block.getNotes().size(), 0);

        assertEquals(block.addNote(note1), 0);
        assertEquals(block.addNote(note2), 1);
        assertEquals(block.addNote(note3), 2);
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

        assertEquals(block.addNote(note1), 0);
        expectedNotes.add(note1);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(block.addNote(note2), 1);
        expectedNotes.add(note2);
        assertEquals(block.getNotes(), expectedNotes);

        assertEquals(block.addNote(note3), 2);
        expectedNotes.add(note3);
        assertEquals(block.getNotes(), expectedNotes);

        block.getNotes().clear();
        assertEquals(block.getNotes().size(), 0);

        assertEquals(block.addNote(note1), 0);
        assertEquals(block.addNote(note2), 1);
        assertEquals(block.addNote(note3), 2);

        assertEquals(block.addNote(note11), 3);
        assertEquals(block.addNote(note22), 4);
        assertEquals(block.addNote(note33), -1);
        assertEquals(block.addNote(note44), -1);

        assertEquals(block.getNotes().size(), 5);
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
        dawClipboard.copy(Arrays.asList(n));
        assertFalse(dawClipboard.isEmpty());

        assertEquals(block.getNotes().size(), 0);
        block.paste(dawClipboard.getContents(), 0);
        assertEquals(block.getNotes().size(), 1);

        n.setPitch(100);
        assertEquals(block.getNotes().get(0).getPitch(), 0);
    }

    @Test
    void testOddPaste() {
        DawClipboard dawClipboard = new DawClipboard();
        dawClipboard.copy(Arrays.asList(block));

        assertEquals(block.getNotes().size(), 0);
        block.paste(dawClipboard.getContents(), 0);
        assertEquals(block.getNotes().size(), 0);
    }
}