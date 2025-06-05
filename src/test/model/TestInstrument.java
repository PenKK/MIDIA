package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;

public class TestInstrument {

    @Test
    void testGetters() {
        int num = 0;
        for (Instrument i : TonalInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(num, 128);

        num = 35;
        for (Instrument i : PercussiveInstrument.values()) {
            assertEquals(i.getProgramNumber(), num);
            num++;
        }
        
        assertEquals(num, 82);
        assertEquals(TonalInstrument.ALTO_SAX.getName(), "Alto Sax");
        assertEquals(PercussiveInstrument.ACOUSTIC_BASS_DRUM.getName(), "Acoustic Bass Drum");

        assertEquals(TonalInstrument.BARITONE_SAX.getName(), 
                     TonalInstrument.BARITONE_SAX.toString());

        assertEquals(PercussiveInstrument.CHINESE_CYMBAL.getName(), 
        PercussiveInstrument.CHINESE_CYMBAL.toString());
    }
}
