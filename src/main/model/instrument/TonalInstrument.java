package model.instrument;

// Constants for MIDI instrumental instruments with corresponding channel program numbers
// Taken from https://en.wikipedia.org/wiki/General_MIDI
public enum TonalInstrument implements Instrument {
    // Piano
    ACOUSTIC_GRAND_PIANO(0, "Acoustic Grand Piano"),
    BRIGHT_ACOUSTIC_PIANO(1, "Bright Acoustic Piano"),
    ELECTRIC_GRAND_PIANO(2, "Electric Grand Piano (Yamaha CP-70)"),
    HONKY_TONK_PIANO(3, "Honky-tonk Piano"),
    ELECTRIC_PIANO_1(4, "Electric Piano 1 (Rhodes/Wurlitzer)"),
    ELECTRIC_PIANO_2(5, "Electric Piano 2 (FM Piano)"),
    HARPSICHORD(6, "Harpsichord"),
    CLAVINET(7, "Clavinet"),

    // Chromatic Percussion
    CELESTA(8, "Celesta"),
    GLOCKENSPIEL(9, "Glockenspiel"),
    MUSIC_BOX(10, "Music Box"),
    VIBRAPHONE(11, "Vibraphone"),
    MARIMBA(12, "Marimba"),
    XYLOPHONE(13, "Xylophone"),
    TUBULAR_BELLS(14, "Tubular Bells"),
    DULCIMER(15, "Dulcimer (Santoor)"),

    // Organ
    DRAWBAR_ORGAN(16, "Drawbar Organ"),
    PERCUSSIVE_ORGAN(17, "Percussive Organ"),
    ROCK_ORGAN(18, "Rock Organ"),
    CHURCH_ORGAN(19, "Church Organ"),
    REED_ORGAN(20, "Reed Organ"),
    ACCORDION(21, "Accordion"),
    HARMONICA(22, "Harmonica"),
    BANDONEON(23, "Bandoneon (Tango Accordion)"),

    // Guitar
    ACOUSTIC_GUITAR_NYLON(24, "Acoustic Guitar (Nylon)"),
    ACOUSTIC_GUITAR_STEEL(25, "Acoustic Guitar (Steel)"),
    ELECTRIC_GUITAR_JAZZ(26, "Electric Guitar (Jazz)"),
    ELECTRIC_GUITAR_CLEAN(27, "Electric Guitar (Clean)"),
    ELECTRIC_GUITAR_MUTED(28, "Electric Guitar (Muted)"),
    ELECTRIC_GUITAR_OVERDRIVE(29, "Electric Guitar (Overdrive)"),
    ELECTRIC_GUITAR_DISTORTION(30, "Electric Guitar (Distortion)"),
    ELECTRIC_GUITAR_HARMONICS(31, "Electric Guitar (Harmonics)"),

    // Bass
    ACOUSTIC_BASS(32, "Acoustic Bass"),
    ELECTRIC_BASS_FINGER(33, "Electric Bass (Finger)"),
    ELECTRIC_BASS_PICKED(34, "Electric Bass (Picked)"),
    ELECTRIC_BASS_FRETLESS(35, "Electric Bass (Fretless)"),
    SLAP_BASS_1(36, "Slap Bass 1"),
    SLAP_BASS_2(37, "Slap Bass 2"),
    SYNTH_BASS_1(38, "Synth Bass 1"),
    SYNTH_BASS_2(39, "Synth Bass 2"),

    // Strings
    VIOLIN(40, "Violin"),
    VIOLA(41, "Viola"),
    CELLO(42, "Cello"),
    CONTRABASS(43, "Contrabass"),
    TREMULO_STRINGS(44, "Tremolo Strings"),
    PIZZICATO_STRINGS(45, "Pizzicato Strings"),
    ORCHESTRAL_HARP(46, "Orchestral Harp"),
    TIMPANI(47, "Timpani"),

    // Ensemble
    STRING_ENSEMBLE_1(48, "String Ensemble 1"),
    STRING_ENSEMBLE_2(49, "String Ensemble 2"),
    SYNTH_STRINGS_1(50, "Synth Strings 1"),
    SYNTH_STRINGS_2(51, "Synth Strings 2"),
    CHOIR_AAHS(52, "Choir Aahs"),
    VOICE_OOHS(53, "Voice Oohs"),
    SYNTH_VOICE(54, "Synth Voice"),
    ORCHESTRA_HIT(55, "Orchestra Hit"),

    // Brass
    TRUMPET(56, "Trumpet"),
    TROMBONE(57, "Trombone"),
    TUBA(58, "Tuba"),
    MUTED_TRUMPET(59, "Muted Trumpet"),
    FRENCH_HORN(60, "French Horn"),
    BRASS_SECTION(61, "Brass Section"),
    SYNTH_BRASS_1(62, "Synth Brass 1"),
    SYNTH_BRASS_2(63, "Synth Brass 2"),

    // Reed
    SOPRANO_SAX(64, "Soprano Sax"),
    ALTO_SAX(65, "Alto Sax"),
    TENOR_SAX(66, "Tenor Sax"),
    BARITONE_SAX(67, "Baritone Sax"),
    OBOE(68, "Oboe"),
    ENGLISH_HORN(69, "English Horn"),
    BASSOON(70, "Bassoon"),
    CLARINET(71, "Clarinet"),

    // Pipe
    PICCOLO(72, "Piccolo"),
    FLUTE(73, "Flute"),
    RECORDER(74, "Recorder"),
    PAN_FLUTE(75, "Pan Flute"),
    BLOWN_BOTTLE(76, "Blown Bottle"),
    SHAKUHACHI(77, "Shakuhachi"),
    WHISTLE(78, "Whistle"),
    OCARINA(79, "Ocarina"),

    // Synth Lead
    LEAD_1(80, "Lead 1 (Square)"),
    LEAD_2(81, "Lead 2 (Sawtooth)"),
    LEAD_3(82, "Lead 3 (Calliope)"),
    LEAD_4(83, "Lead 4 (Chiff)"),
    LEAD_5(84, "Lead 5 (Charang)"),
    LEAD_6(85, "Lead 6 (Voice)"),
    LEAD_7(86, "Lead 7 (Fifths)"),
    LEAD_8(87, "Lead 8 (Bass and Lead)"),

    // Synth Pad
    PAD_1(88, "Pad 1 (New Age)"),
    PAD_2(89, "Pad 2 (Warm)"),
    PAD_3(90, "Pad 3 (Polysynth)"),
    PAD_4(91, "Pad 4 (Choir)"),
    PAD_5(92, "Pad 5 (Bowed Glass)"),
    PAD_6(93, "Pad 6 (Metallic)"),
    PAD_7(94, "Pad 7 (Halo)"),
    PAD_8(95, "Pad 8 (Sweep)"),

    // Synth Effects
    FX_1(96, "FX 1 (Rain)"),
    FX_2(97, "FX 2 (Soundtrack)"),
    FX_3(98, "FX 3 (Crystal)"),
    FX_4(99, "FX 4 (Atmosphere)"),
    FX_5(100, "FX 5 (Brightness)"),
    FX_6(101, "FX 6 (Goblins)"),
    FX_7(102, "FX 7 (Echoes)"),
    FX_8(103, "FX 8 (Sci-Fi)"),

    // Ethnic
    SITAR(104, "Sitar"),
    BANJO(105, "Banjo"),
    SHAMISEN(106, "Shamisen"),
    KOTO(107, "Koto"),
    KALIMBA(108, "Kalimba"),
    BAG_PIPE(109, "Bag Pipe"),
    FIDDLE(110, "Fiddle"),
    SHANAI(111, "Shanai"),

    // Percussive
    TINKLE_BELL(112, "Tinkle Bell"),
    AGOGO(113, "Agogo"),
    STEEL_DRUMS(114, "Steel Drums"),
    WOODBLOCK(115, "Woodblock"),
    TAIKO_DRUM(116, "Taiko Drum"),
    MELODIC_TOM(117, "Melodic Tom"),
    SYNTH_DRUM(118, "Synth Drum"),
    REVERSE_CYM_BAL(119, "Reverse Cymbal"),

    // Sound Effects
    GUITAR_FRET_NOISE(120, "Guitar Fret Noise"),
    BREATH_NOISE(121, "Breath Noise"),
    SEASHORE(122, "Seashore"),
    BIRD_TWEET(123, "Bird Tweet"),
    TELEPHONE_RING(124, "Telephone Ring"),
    HELICOPTER(125, "Helicopter"),
    APPLAUSE(126, "Applause"),
    GUNSHOT(127, "Gunshot");

    private final int programNumber;
    private final String name;

     // EFFECTS: creates an instrument enum with a programNumber and name
    TonalInstrument(int programNumber, String name) {
        this.programNumber = programNumber;
        this.name = name;
    }

    @Override
    public int getProgramNumber() {
        return programNumber;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
