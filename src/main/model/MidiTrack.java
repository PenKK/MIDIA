package model;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import persistance.Writable;

// A high level representation of a track, which is a single layer/instrument of the project.
// For playback the MidiTrack will be converted to a javax.sound.midi.Track
// See https://midi.org/expanded-midi-1-0-messages-list for midi message correspondence
// See https://en.wikipedia.org/wiki/General_MIDI for more midi information
public class MidiTrack implements Writable {

    private static final int DEFAULT_VOLUME = 100;

    private boolean muted;
    private int instrument; // 0 to 127 inclusive if not percussive, else 35 to 81 inclusive
    private int volume; // 0 to 127 inclusive
    private String name;
    private ArrayList<Block> blocks;
    private final int channel;

    // REQUIRES: 0 <= instrument <= 127 if percussive is false, else 35 <= instrument <= 81
    // EFFECTS: Creates a single track that initially: is not muted,
    //          has no blocks, set to a specified instrument, a default volume,
    //          percussive according to parameter, and a name.
    public MidiTrack(String name, int instrument, int channel) {
        this.muted = false;
        this.blocks = new ArrayList<Block>();
        this.instrument = instrument;
        this.volume = DEFAULT_VOLUME;
        this.channel = channel;
        this.name = name;
    }

    // MODIFIES: this
    // EFFECTS: Adds a block to the list of blocks, returns the index it was created it
    public int addBlock(Block block) {
        blocks.add(block);
        return blocks.size() - 1;
    }

    // REQUIRES: index >= 0
    // MODIFIES: this 
    // EFFECTS: removes the block at the given index in this track and returns it
    public Block removeBlock(int index) {
        return blocks.remove(index);
    }

    // MODIFIES: track
    // EFFECTS: Converts MidiTrack to Java Track data. First creates volume and instrument (program change) messages.
    //          Then converts each block from blocks to individual notes to MIDI events.    
    //          Creates one event for the note on event, and one for the end not event (per note).
    //          All created events are applied to the track.
    public void applyToTrack(Track track) {
        ShortMessage programChangeMessage = new ShortMessage();
        ShortMessage volMessage = new ShortMessage();

        try {
            programChangeMessage.setMessage(ShortMessage.PROGRAM_CHANGE, getChannel(), instrument, 0);
            volMessage.setMessage(ShortMessage.CONTROL_CHANGE, getChannel(), 7, volume);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Failed to update sequence in track: " 
                                        + name + "due to invalid MidiData", e);
        }

        if (!isPercussive()) { // Tracks on channel 10 ignore program change
            track.add(new MidiEvent(programChangeMessage, 0));
        }
        track.add(new MidiEvent(volMessage, 0));

        for (Block currentBlock : blocks) {
            for (Note note : currentBlock.getNotesTimeline()) {
                applyNoteToTrack(note, track);
            }
        }
    }

    // MODIFIES: track
    // EFFECTS: Helper method for applyToTrack; converts note to MIDI event on and off
    //          and applies it to the specified track.
    private void applyNoteToTrack(Note note, Track track) {
        try {
            ShortMessage onMessage = new ShortMessage();
            ShortMessage offMessage = new ShortMessage();
            // Percussive tracks use data1 for the instrument as they have no pitch
            int data1 = isPercussive() ? instrument : note.getPitch();

            onMessage.setMessage(ShortMessage.NOTE_ON, getChannel(), data1, note.getVelocity());
            offMessage.setMessage(ShortMessage.NOTE_OFF, getChannel(), data1, 0); 

            MidiEvent noteOnEvent = new MidiEvent(onMessage, note.getStartTick());
            MidiEvent noteOffEvent = new MidiEvent(offMessage, note.getStartTick() + note.getDurationTicks());

            track.add(noteOnEvent);
            track.add(noteOffEvent);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Failed to apply track", e);
        }
    }

    // REQUIRES: 0 <= newVolume <= 127
    public void setVolume(int newVolume) {
        volume = newVolume;
    }

    // REQUIRES: 0 <= newVolume <= 100
    // MODFIES: this
    // EFFECTS: sets the volume in a range 0 - 100 and scales it to 0 - 127
    public void setVolumeScaled(int newVolume) {
        volume = (int) Math.round(newVolume * 1.27);
    }

    public int getChannel() {
        return channel;
    }

    public void setMuted(boolean mutedValue) {
        muted = mutedValue;
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

    // EFFECTS: returns volume scaled down from 127 to 100
    public int getVolumeScaled() {
        return (int) Math.round(volume / 1.27);
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

    // EFFECTS: returns true if the track is on channel 9, which is reserved for percussion
    public boolean isPercussive() {
        return channel == 9;
    }

    // EFFECTS: returns a JSON object representation of the MidiTrack
    @Override
    public JSONObject toJson() {
        JSONObject midiTrackJson = new JSONObject();
        
        midiTrackJson.put("channel", channel);
        midiTrackJson.put("instrument", instrument);
        midiTrackJson.put("volume", volume);
        midiTrackJson.put("name", name);
        midiTrackJson.put("blocks", blocksToJson());

        return midiTrackJson;
    }

    // EFFECTS: returns JSON Array representation of blocks in this midiTrack
    private JSONArray blocksToJson() {
        JSONArray blocksJson = new JSONArray();

        for (Block block : blocks) {
            blocksJson.put(block.toJson());
        }

        return blocksJson;
    }
}
