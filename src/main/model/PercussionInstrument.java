package model;

public enum PercussionInstrument implements Instrument {

    ACOUSTIC_BASS_DRUM(0, "Acoustic Bass Drum"),
    BASS_DRUM_1(1, "Bass Drum 1"),
    SIDE_STICK(2, "Side Stick"),
    ACOUSTIC_SNARE(3, "Acoustic Snare"),
    HAND_CLAP(4, "Hand Clap"),
    ELECTRIC_SNARE(5, "Electric Snare"),
    LOW_FLOOR_TOM(6, "Low Floor Tom"),
    CLOSED_HI_HAT(7, "Closed Hi-Hat"),
    HIGH_FLOOR_TOM(8, "High Floor Tom"),
    PEDAL_HI_HAT(9, "Pedal Hi-Hat"),
    LOW_TOM(10, "Low Tom"),
    OPEN_HI_HAT(11, "Open Hi-Hat"),
    LOW_MID_TOM(12, "Low-Mid Tom"),
    HI_MID_TOM(13, "Hi-Mid Tom"),
    CRASH_CYMBAL_1(14, "Crash Cymbal 1"),
    HIGH_TOM(15, "High Tom"),
    RIDE_CYMBAL_1(16, "Ride Cymbal 1"),
    CHINESE_CYMBAL(17, "Chinese Cymbal"),
    RIDE_BELL(18, "Ride Bell"),
    TAMBOURINE(19, "Tambourine"),
    SPLASH_CYMBAL(20, "Splash Cymbal"),
    COWBELL(21, "Cowbell"),
    CRASH_CYMBAL_2(22, "Crash Cymbal 2"),
    VIBRASLAP(23, "Vibraslap"),
    RIDE_CYMBAL_2(24, "Ride Cymbal 2"),
    HI_BONGO(25, "Hi Bongo"),
    LOW_BONGO(26, "Low Bongo"),
    MUTE_HI_CONGA(27, "Mute Hi Conga"),
    OPEN_HI_CONGA(28, "Open Hi Conga"),
    LOW_CONGA(29, "Low Conga"),
    HIGH_TIMBALE(30, "High Timbale"),
    LOW_TIMBALE(31, "Low Timbale"),
    HIGH_AGOGO(32, "High Agogo"),
    LOW_AGOGO(33, "Low Agogo"),
    CABASA(34, "Cabasa"),
    MARACAS(35, "Maracas"),
    SHORT_WHISTLE(36, "Short Whistle"),
    LONG_WHISTLE(37, "Long Whistle"),
    SHORT_GUIRO(38, "Short Guiro"),
    LONG_GUIRO(39, "Long Guiro"),
    CLAVES(40, "Claves"),
    HI_WOOD_BLOCK(41, "Hi Wood Block"),
    LOW_WOOD_BLOCK(42, "Low Wood Block"),
    MUTE_CUICA(43, "Mute Cuica"),
    OPEN_CUICA(44, "Open Cuica"),
    MUTE_TRIANGLE(45, "Mute Triangle"),
    OPEN_TRIANGLE(46, "Open Triangle");

    private final int programNumber;
    private final String name;

    // EFFECTS: creates a percussive instrument enum with a programNumber and name
    PercussionInstrument(int programNumber, String name) {
        this.programNumber = programNumber;
        this.name = name;
    }
    
    @Override
    public int getProgramNumber() {
        return programNumber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
