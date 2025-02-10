package model;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

// A high level representation of a track, which is a single layer/instrument of the project
public class MidiTrack {

    private static final int DEFAULT_VOLUME = 100;
    private static final int DEFAULT_PERCUSSIVE_INSTRUMENT = 35;
    private static final int DEFAULT_NON_PERCUSSIVE_INSTRUMENT = 0;

    private boolean percussive;
    private boolean muted;
    private int instrument; // 0 to 127 inclusive if not percussive, else 35 to 81 inclusive
    private int volume; // 0 to 127 inclusive
    private String name;
    private ArrayList<Block> blocks;

    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, a default instrument depending on percussive or not,
    //          a default volume, percussive according to parameter, a name.
    public MidiTrack(String name, boolean percussive) {
        this.muted = false;
        this.blocks = new ArrayList<Block>();
        this.instrument = percussive ? DEFAULT_PERCUSSIVE_INSTRUMENT : DEFAULT_NON_PERCUSSIVE_INSTRUMENT;
        this.volume = DEFAULT_VOLUME;
        this.percussive = percussive;
        this.name = name;
    }

    // REQUIRES: 0 <= instrument <= 127 if percussive is false, else 35 <= instrument <= 81
    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, set to a specified instrument, a default volume,
    //          percussive according to parameter, and a name.
    public MidiTrack(String name, int instrument, boolean percussive) {
        this.muted = false;
        this.blocks = new ArrayList<Block>();
        this.instrument = instrument;
        this.volume = DEFAULT_VOLUME;
        this.percussive = percussive;
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: Adds a block to the list of blocks, returns the index it was created it
    public int addBlock(Block block) {
        blocks.add(block);
        return blocks.size() - 1;
    }

    // MODIFIES: this 
    // EFFECTS: removes the block at the given index in this track and returns it
    public Block removeBlock(int index) {
        return blocks.remove(index);
    }

    // MODIFIES: track
    // EFFECTS: Converts each block from blocks to individual notes to MIDI events and applies it to an actual
    //          lower level Track object. Creates one event for the play sound, and one for the end per note.
    public void applyToTrack(Track track) {
        ShortMessage programChangeMessage = new ShortMessage();
        ShortMessage volMessage = new ShortMessage();

        try {
            programChangeMessage.setMessage(ShortMessage.PROGRAM_CHANGE, getChannel(),
                    instrument, 0);
            volMessage.setMessage(ShortMessage.CONTROL_CHANGE, getChannel(), 7,
                    volume);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Failed to update sequence in track: " 
                                        + name + "due to invalid MidiData", e);
        }

        track.add(new MidiEvent(programChangeMessage, 0));
        track.add(new MidiEvent(volMessage, 0));

        for (Block currentBlock : blocks) {
            for (Note note : currentBlock.getNotesTimeline()) {
                try {
                    ShortMessage onMessage = new ShortMessage();
                    ShortMessage offMessage = new ShortMessage();

                    onMessage.setMessage(ShortMessage.NOTE_ON, getChannel(), 
                            note.getPitch(), note.getVelocity());
                    offMessage.setMessage(ShortMessage.NOTE_OFF, getChannel(), 
                            note.getPitch(), 0);

                    MidiEvent noteOnEvent = new MidiEvent(onMessage, note.getStartTick());
                    MidiEvent noteOffEvent = new MidiEvent(offMessage, note.getStartTick() + note.getDurationTicks());

                    track.add(noteOnEvent);
                    track.add(noteOffEvent);
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException("Failed to apply track", e);
                }
            }
        }
    }

    // REQUIRES: 0 <= newVolume <= 127
    public void setVolume(int newVolume) {
        volume = newVolume;
    }

    // EFFECTS: Returns correct channel corresponding to the whether or not the track is precussive
    public int getChannel() {
        return percussive ? 9 : 0;
    }

    public void setMuted(boolean mutedValue) {
        muted = mutedValue;
    }

    // REQUIRES: instrument value must be in range [35, 81]
    public void setPercussive(boolean percussiveValue) {
        percussive = percussiveValue;
    }

    // REQUIRES: 0 <= instrument <= 127 if not percussive, else 35 <= instrument <= 81
    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public Block getBlock(int index) {
        return blocks.get(index);
    }

    public int getVolume() {
        return volume;
    }

    public boolean isMuted() {
        return muted;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public int getInstrument() {
        return instrument;
    }

    public String getName() {
        return name;
    }

    public boolean isPercussive() {
        return percussive;
    }
}
