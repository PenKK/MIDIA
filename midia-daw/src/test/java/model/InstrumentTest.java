package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;

public class InstrumentTest {

    @Test
    void testGetters() {
        int num = 0;
        for (Instrument i : TonalInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(128, num);

        num = 35;
        for (Instrument i : PercussiveInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(82, num);
        assertEquals("Alto Sax", TonalInstrument.ALTO_SAX.getName());
        assertEquals("Acoustic Bass Drum", PercussiveInstrument.ACOUSTIC_BASS_DRUM.getName());

        assertEquals(TonalInstrument.BARITONE_SAX.getName(), 
                     TonalInstrument.BARITONE_SAX.toString());

        assertEquals(PercussiveInstrument.CHINESE_CYMBAL.getName(), 
        PercussiveInstrument.CHINESE_CYMBAL.toString());
    }
}
