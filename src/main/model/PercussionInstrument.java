package model;

public enum PercussionInstrument implements Instrument {

    ACOUSTIC_BASS_DRUM(35, "Acoustic Bass Drum"),
    ELECTRIC_BASS_DRUM(36, "Electric Bass Drum"),
    SIDE_STICK(37, "Side Stick"),
    ACOUSTIC_SNARE(38, "Acoustic Snare"),
    HAND_CLAP(39, "Hand Clap"),
    ELECTRIC_SNARE(40, "Electric Snare (Rimshot)"),
    LOW_FLOOR_TOM(41, "Low Floor Tom"),
    CLOSED_HI_HAT(42, "Closed Hi-hat"),
    HIGH_FLOOR_TOM(43, "High Floor Tom"),
    PEDAL_HI_HAT(44, "Pedal Hi-hat"),
    LOW_TOM(45, "Low Tom"),
    OPEN_HI_HAT(46, "Open Hi-hat"),
    LOW_MID_TOM(47, "Low-Mid Tom"),
    HIGH_MID_TOM(48, "High-Mid Tom"),
    CRASH_CYMBAL_1(49, "Crash Cymbal 1"),
    HIGH_TOM(50, "High Tom"),
    RIDE_CYMBAL_1(51, "Ride Cymbal 1"),
    CHINESE_CYMBAL(52, "Chinese Cymbal"),
    RIDE_BELL(53, "Ride Bell"),
    TAMBOURINE(54, "Tambourine"),
    SPLASH_CYMBAL(55, "Splash Cymbal"),
    COWBELL(56, "Cowbell"),
    CRASH_CYMBAL_2(57, "Crash Cymbal 2"),
    VIBRASLAP(58, "Vibraslap"),
    RIDE_CYMBAL_2(59, "Ride Cymbal 2"),
    HIGH_BONGO(60, "High Bongo"),
    LOW_BONGO(61, "Low Bongo"),
    MUTE_HIGH_CONGA(62, "Mute High Conga"),
    OPEN_HIGH_CONGA(63, "Open High Conga"),
    LOW_CONGA(64, "Low Conga"),
    HIGH_TIMBALE(65, "High Timbale"),
    LOW_TIMBALE(66, "Low Timbale"),
    HIGH_AGOGO(67, "High Agogô"),
    LOW_AGOGO(68, "Low Agogô"),
    CABASA(69, "Cabasa"),
    MARACAS(70, "Maracas"),
    SHORT_WHISTLE(71, "Short Whistle"),
    LONG_WHISTLE(72, "Long Whistle"),
    SHORT_GUIRO(73, "Short Güiro"),
    LONG_GUIRO(74, "Long Güiro"),
    CLAVES(75, "Claves"),
    HIGH_WOODBLOCK(76, "High Woodblock"),
    LOW_WOODBLOCK(77, "Low Woodblock"),
    MUTE_CUICA(78, "Mute Cuíca"),
    OPEN_CUICA(79, "Open Cuíca"),
    MUTE_TRIANGLE(80, "Mute Triangle"),
    OPEN_TRIANGLE(81, "Open Triangle");

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
