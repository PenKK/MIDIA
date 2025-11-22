package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.event.Event;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Event class
 */
public class EventTest {
    private Event e;
    private Event e2;
    private Date d;

    // NOTE: these tests might fail if the time at which line (2) below is executed
    // is different from the time that line (1) is executed.  Lines (1) and (2) must
    // run in the same millisecond for this test to make sense and pass.

    @BeforeEach
    public void runBefore() {
        e = new Event("Sensor open at door"); // (1)
        e2 = new Event("Sensor open at door");
        d = Calendar.getInstance().getTime(); // (2)
    }

    @Test
    public void testEvent() {
        assertEquals("Sensor open at door", e.getDescription());
    }

    @Test
    public void testToString() {
        assertEquals(d.toString() + "\n" + "Sensor open at door", e.toString());
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, e);
        assertNotEquals(new Object(), e);
    }

    @Test
    public void testHashCode() {
        // May not pass depending on the MS the two hashes are computed
        assertEquals(e.hashCode(), e2.hashCode());
    }
}