package model;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import model.editing.Copyable;
import model.editing.Pastable;
import model.event.Event;
import model.event.EventLog;
import model.instrument.Instrument;
import persistance.Writable;

/**
 * A high-level representation of a track, which is a single layer/instrument of the project.
 * <p>
 * For playback this MidiTrack is converted to a {@code javax.sound.midi.Track}.
 * See <a href="https://midi.org/expanded-midi-1-0-messages-list">...</a> for MIDI messages and
 * <a href="https://en.wikipedia.org/wiki/General_MIDI">...</a> for instrument information.
 */
public class MidiTrack implements Writable, Pastable {

    private static final int DEFAULT_VOLUME = 100;

    private boolean muted;
    private Instrument instrument; // 0 to 127 inclusive if not percussive, else 35 to 81 inclusive
    private int volume; // 0 to 127 inclusive
    private String name;
    private final ArrayList<Block> blocks;
    private final int channel;

    /**
     * Constructs a MidiTrack.
     * <p>
     * Preconditions:
     * - If the track is not percussive, {@code instrument.getProgramNumber()} must be in [0, 127].
     * - If the track is percussive, {@code instrument.getProgramNumber()} should match the percussive range (35-81).
     * The track is initialized as unmuted, with no blocks, the given instrument, a default volume, the given channel,
     * and name.
     *
     * @param name       the display name of the track
     * @param instrument the instrument for this track
     * @param channel    the MIDI channel (9 for percussive; otherwise a free channel)
     */
    public MidiTrack(String name, Instrument instrument, int channel) {
        this.muted = false;
        this.blocks = new ArrayList<>();
        this.instrument = instrument;
        this.volume = DEFAULT_VOLUME;
        this.channel = channel;
        this.name = name;
    }

    /**
     * Adds a block to this track.
     *
     * @param block the block to add
     * @return the index at which the block was added
     */
    public int addBlock(Block block) {
        blocks.add(block);
        Event e = new Event(String.format("Added Block with %d notes to MidiTrack %s", 
                                          block.getNotes().size(), name));
        EventLog.getInstance().logEvent(e);
        return blocks.size() - 1;
    }

    /**
     * Removes and returns the block at the given index.
     * <p>
     * Preconditions: {@code index >= 0}
     *
     * @param index the index of the block to remove
     * @return the removed block
     */
    public Block removeBlock(int index) {
        Block b = blocks.remove(index);

        Event e = new Event(String.format("Removed Block with %d notes in MidiTrack %s", 
                                          b.getNotes().size(), name));
        EventLog.getInstance().logEvent(e);
        return b;
    }

    /**
     * Applies this MidiTrack's data to the given {@link Track}.
     * <p>
     * Creates initial volume and (if applicable) program change messages,
     * then converts each block's notes to NOTE_ON and NOTE_OFF events and adds them to the track.
     *
     * @param track the Java MIDI track to populate
     * @throws InvalidMidiDataException if invalid MIDI data is encountered
     */
    public void applyToTrack(Track track) throws InvalidMidiDataException {
        ShortMessage volMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, getChannel(), 7, volume);

        if (!isPercussive()) { // Tracks on channel 10 ignore program change
            ShortMessage programChangeMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, getChannel(),
                    instrument.getProgramNumber(),0);
            track.add(new MidiEvent(programChangeMessage, 0));
        }
        track.add(new MidiEvent(volMessage, 0));

        for (Block currentBlock : blocks) {
            for (Note note : currentBlock.getNotesTimeline()) {
                applyNoteToTrack(track, note, isPercussive(), instrument, getChannel());
            }
        }
    }

    /**
     * Helper for {@link #applyToTrack(Track)} to add NOTE_ON and NOTE_OFF events for a note.
     *
     * @param track        the track to add events to
     * @param note         the note to apply
     * @param isPercussive whether the track is percussive (channel 9 semantics)
     * @param instrument   the instrument to use (used for percussive data1)
     * @param channel      the MIDI channel
     * @throws RuntimeException if invalid, MIDI data prevents applying the note
     */
    public static void applyNoteToTrack(Track track, Note note, boolean isPercussive,
                                        Instrument instrument, int channel) {
        try {
            ShortMessage onMessage = new ShortMessage();
            ShortMessage offMessage = new ShortMessage();
            // Percussive tracks use data1 for the instrument as they have no pitch
            int data1 = isPercussive ? instrument.getProgramNumber() : note.getPitch();

            onMessage.setMessage(ShortMessage.NOTE_ON, channel, data1, note.getVelocity());
            offMessage.setMessage(ShortMessage.NOTE_OFF, channel, data1, 0); 

            MidiEvent noteOnEvent = new MidiEvent(onMessage, note.getStartTick());
            MidiEvent noteOffEvent = new MidiEvent(offMessage, note.getStartTick() + note.getDurationTicks());

            track.add(noteOnEvent);
            track.add(noteOffEvent);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Failed to apply track", e);
        }
    }

    /**
     * Sets the raw MIDI volume.
     *
     * @param newVolume the new volume (0-127)
     */
    public void setVolume(int newVolume) {
        volume = newVolume;
    }

    /**
     * Sets the volume using a 0-100 scale and converts it to the MIDI 0-127 range.
     *
     * @param newVolume the scaled volume (0-100)
     */
    public void setVolumeScaled(int newVolume) {
        volume = (int) Math.round(newVolume * 1.27);
    }

    public int getChannel() {
        return channel;
    }

    public void setMuted(boolean mutedValue) {
        muted = mutedValue;
    }

    /**
     * Sets the instrument for this track.
     * <p>
     * Preconditions:
     * - For non-percussive tracks, program number in [0, 127].
     * - For percussive tracks, use percussive mapping (35-81).
     *
     * @param instrument the new instrument
     */
    public void setInstrument(Instrument instrument) {
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

    /**
     * Returns the volume scaled from the MIDI range (0-127) to 0-100.
     *
     * @return the scaled volume (0-100)
     */
    public int getVolumeScaled() {
        return (int) Math.round(volume / 1.27);
    }

    public boolean isMuted() {
        return muted;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns whether this track is percussive (MIDI channel 9).
     *
     * @return true if a channel is 9; false otherwise
     */
    public boolean isPercussive() {
        return channel == 9;
    }

    /**
     * Returns a JSON object representation of this MidiTrack.
     *
     * @return the JSON representation of this track
     */
    @Override
    public JSONObject toJson() {
        JSONObject midiTrackJson = new JSONObject();
        
        midiTrackJson.put("channel", channel);
        midiTrackJson.put("instrument", instrument.toJson());
        midiTrackJson.put("volume", volume);
        midiTrackJson.put("name", name);
        midiTrackJson.put("blocks", blocksToJson());

        return midiTrackJson;
    }

    /**
     * Returns a string with general information about the MidiTrack.
     *
     * @return formatted string with name, channel, instrument, and block count
     */
    public String info() {
        return String.format("name: %s, channel: %d, instrument: %s, block count: %d",
                              name, channel, instrument, blocks.size());
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a JSON array representation of the blocks in this track.
     *
     * @return JSON array containing block JSON
     */
    private JSONArray blocksToJson() {
        JSONArray blocksJson = new JSONArray();

        for (Block block : blocks) {
            blocksJson.put(block.toJson());
        }

        return blocksJson;
    }

    @Override
    public void paste(List<Copyable> copiedItems, long position) {
        ArrayList<Block> copiedBlocks = new ArrayList<>();
        long minBlockStartTick = -1;

        for (Copyable c : copiedItems) {
            if (c.getClass().equals(Block.class)) {
                Block block = ((Block) c).clone();
                copiedBlocks.add(block);
                long startTick = block.getStartTick();

                if (startTick < minBlockStartTick || minBlockStartTick == -1) {
                    minBlockStartTick = startTick;
                }
            }
        }

        for (Block b : copiedBlocks) {
            b.setStartTick(b.getStartTick() - minBlockStartTick + position);
            this.addBlock(b);
        }
    }
}
