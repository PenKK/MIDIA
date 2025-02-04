package model;

import static org.junit.Assert.assertEquals;

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
}
