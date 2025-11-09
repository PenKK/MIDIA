package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestNote {
    Note note;

    @BeforeEach
    void runBefore() {
        note = new Note(60, 60, 0, 5);
    }

    @Test
    void testConstructor() {
        assertEquals(60, note.getPitch());
        assertEquals(60, note.getVelocity());
        assertEquals(0, note.getStartTick());
        assertEquals(5, note.getDurationTicks());
    }

    @Test
    void testSetters() {
        note.setDurationTicks(100);
        assertEquals(100, note.getDurationTicks());
        note.setDurationTicks(3);
        assertEquals(3, note.getDurationTicks());

        note.setPitch(100);
        assertEquals(100, note.getPitch());
        note.setPitch(65);
        assertEquals(65, note.getPitch());

        note.setVelocity(99);
        assertEquals(99, note.getVelocity());
        note.setVelocity(33);
        assertEquals(33, note.getVelocity());

        note.setStartTick(43);
        assertEquals(43, note.getStartTick());
        note.setStartTick(2000);
        assertEquals(2000, note.getStartTick());
    }

    @Test
    void testClone() {
        Note noteClone = note.clone();

        assertNotEquals(note, noteClone);
        assertEquals(note.getPitch(), noteClone.getPitch());
        assertEquals(note.getVelocity(), noteClone.getVelocity());
        assertEquals(note.getDurationTicks(), noteClone.getDurationTicks());
        assertEquals(note.getStartTick(), noteClone.getStartTick());
    }
}
