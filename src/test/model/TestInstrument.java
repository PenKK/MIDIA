package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import model.instrument.Instrument;
import model.instrument.InstrumentalInstrument;
import model.instrument.PercussionInstrument;

public class TestInstrument {

    @Test
    void testGetters() {
        int num = 0;
        for (Instrument i : InstrumentalInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(num, 128);

        num = 35;
        for (Instrument i : PercussionInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(num, 82);
        assertEquals(InstrumentalInstrument.ALTO_SAX.getName(), "Alto Sax");
        assertEquals(PercussionInstrument.ACOUSTIC_BASS_DRUM.getName(), "Acoustic Bass Drum");

        assertEquals(InstrumentalInstrument.BARITONE_SAX.getName(), 
                     InstrumentalInstrument.BARITONE_SAX.toString());

        assertEquals(PercussionInstrument.CHINESE_CYMBAL.getName(), 
        PercussionInstrument.CHINESE_CYMBAL.toString());
    }
}
