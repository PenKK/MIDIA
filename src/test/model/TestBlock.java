package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBlock {
    Block block;

    @BeforeEach
    void runBefore() {
        block = new Block(10);
    }

    @Test
    void testConstructor() {
        assertEquals(new ArrayList<Note>(), block.getNotes());
        assertEquals(10, block.getStartTick());
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

        checkNotesEqual(expectedNotes, block.getNotesTimeline());

        expectedNotes.clear();

        block.setStartTick(4);
        expectedNotes.add(new Note(60, 60, 0 + 4, 5));
        expectedNotes.add(new Note(65, 90, 9 + 4, 17));
        expectedNotes.add(new Note(56, 50, 4 + 4, 9));

        checkNotesEqual(expectedNotes, block.getNotesTimeline());
    }

    private void checkNotesEqual(ArrayList<Note> n1, ArrayList<Note> n2) {
        assertEquals(n1.size(), n2.size());
        for (int i = 0; i < n1.size(); i++) {
            assertEquals(n1.get(i).getPitch(), n2.get(i).getPitch());
            assertEquals(n1.get(i).getStartTick(), n2.get(i).getStartTick());
            assertEquals(n1.get(i).getDurationTicks(), n2.get(i).getDurationTicks());
            assertEquals(n1.get(i).getVelocity(), n2.get(i).getVelocity());
        }
    }
}