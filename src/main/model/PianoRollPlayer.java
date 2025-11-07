package model;

import javax.sound.midi.*;

import model.event.Event;
import model.event.EventLog;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Plays back a single Block in isolation (e.g., a piano roll preview).
 * <p>
 * Builds a temporary sequence for the given block, manages looping, and exposes
 * property change notifications for UI synchronization (such as tick position updates).
 * Uses the parent track's instrument, channel, and volume settings for accurate playback.
 */
public class PianoRollPlayer extends Player implements MetaEventListener {

    private static final int NOTE_CREATION_DURATION_MS = 500;

    private final Block block;
    private final MidiTrack parentMidiTrack;
    private final PropertyChangeSupport propertyChangeSupport;
    private int volume;
    private boolean loop;

    /**
     * Constructs a BlockPlayer for the given block using the parent track's configuration.
     * <p>
     * Registers as a MetaEventListener on the sequencer and initializes tempo.
     *
     * @param block            the block to preview/play
     * @param parentMidiTrack  the parent track providing instrument, channel, and initial volume
     * @param initialBpm       the initial tempo in BPM
     */
    public PianoRollPlayer(Block block, MidiTrack parentMidiTrack, float initialBpm) {
        super();
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.block = block;
        this.parentMidiTrack = parentMidiTrack;
        this.volume = parentMidiTrack.getVolume();
        this.loop = false;

        sequencer.addMetaEventListener(this);

        setBPM(initialBpm);
    }

    /**
     * Toggles looping playback for the current block.
     * <p>
     * When enabled, playback restarts automatically upon reaching the end of the block;
     * when disabled, playback is paused.
     */
    public void toggleLoop() {
        this.loop = !this.loop;

        if (loop) {
            try {
                play();
            } catch (InvalidMidiDataException e) {
                throw new RuntimeException("Failed to play sequencer on loop toggle", e);
            }
        } else {
            pause();
        }
    }

    public boolean isLooping() {
        return loop;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * Rebuilds the playback sequence for this block.
     * <p>
     * Creates a single MIDI track, applies the parent track's program change and volume,
     * converts each note in the block to NOTE_ON/NOTE_OFF events, adjusts the system reset,
     * and sets the sequence on the sequencer.
     *
     * @throws InvalidMidiDataException if invalid MIDI data is encountered
     */
    @Override
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        Track track = sequence.createTrack();

        ShortMessage programChangeMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                parentMidiTrack.getChannel(), parentMidiTrack.getInstrument().getProgramNumber(), 0);
        ShortMessage volMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, parentMidiTrack.getChannel(),
                7, volume);

        track.add(new MidiEvent(programChangeMessage, 0));
        track.add(new MidiEvent(volMessage, 0));

        for (Note note : block.getNotes()) {
            MidiTrack.applyNoteToTrack(track, note, parentMidiTrack.isPercussive(),
                    parentMidiTrack.getInstrument(), parentMidiTrack.getChannel());
        }

        modifySystemReset(track);

        sequencer.setSequence(sequence);

        Event e = new Event(String.format("Playback sequence was updated in Block [Piano Roll] with instrument %s",
                parentMidiTrack.getInstrument()));
        EventLog.getInstance().logEvent(e);
    }

    /**
     * Moves the system reset (end-of-track) event to the end of the block.
     * <p>
     * Ensures playback stops (or loops) exactly at the block's duration.
     *
     * @param track the MIDI track to adjust
     * @throws RuntimeException if no system reset meta event is present
     */
    private void modifySystemReset(Track track) {
        for (int i = track.size() - 1; i >= 0; i--) {
            MidiEvent event = track.get(i);

            if ((event.getMessage().getMessage()[0] & 0xFF) == 255) {
                event.setTick(getBlock().getDurationTicks());
                return;
            }
        }

        throw new RuntimeException("No system reset found");
    }

    @Override
    public void incrementBeatDivision() {
        int old = getBeatDivision();
        super.incrementBeatDivision();
        propertyChangeSupport.firePropertyChange("beatDivision", old, beatDivision);
    }

    @Override
    public void decrementBeatDivision() {
        int old = getBeatDivision();
        super.decrementBeatDivision();
        propertyChangeSupport.firePropertyChange("beatDivision", old, beatDivision);
    }

    @Override
    public void incrementBeatsPerMeasure() {
        int old = getBeatsPerMeasure();
        super.incrementBeatsPerMeasure();
        propertyChangeSupport.firePropertyChange("beatsPerMeasure", old, beatsPerMeasure);
    }

    @Override
    public void decrementBeatsPerMeasure() {
        int old = getBeatsPerMeasure();
        super.decrementBeatsPerMeasure();
        propertyChangeSupport.firePropertyChange("beatsPerMeasure", old, beatsPerMeasure);
    }

    /**
     * Plays a short preview note using the parent track's channel and instrument.
     * <p>
     * For percussive tracks, the instrument program number is used instead of pitch.
     *
     * @param pitch the MIDI pitch to preview (ignored for percussive tracks)
     */
    public void playNote(int pitch) {
        resetTracks();
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            Receiver receiver = synthesizer.getReceiver();
            synthesizer.open();

            int data1 = parentMidiTrack.isPercussive() ? parentMidiTrack.getInstrument().getProgramNumber() : pitch;

            ShortMessage onMessage = new ShortMessage(ShortMessage.NOTE_ON, parentMidiTrack.getChannel(), data1, 127);
            ShortMessage offMessage = new ShortMessage(ShortMessage.NOTE_OFF, parentMidiTrack.getChannel(), data1, 127);
            ShortMessage programMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE, parentMidiTrack.getChannel(),
                    parentMidiTrack.getInstrument().getProgramNumber(), 0);

            receiver.send(programMessage, 0);
            receiver.send(onMessage, 0);
            receiver.send(offMessage, NOTE_CREATION_DURATION_MS * 1000);
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(synthesizer::close, NOTE_CREATION_DURATION_MS + 1000, TimeUnit.MILLISECONDS);
        } catch (MidiUnavailableException e) {
            throw new RuntimeException("Synthesizer unavailable", e);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid midi in synthesizer", e);
        }
    }

    /**
     * Sets the tick position and notifies listeners via a property change event.
     *
     * @param newTickPosition the new tick position
     * @return the previous tick position
     */
    @Override
    public long setTickPosition(long newTickPosition) {
        long oldTickPosition = super.setTickPosition(newTickPosition);
        propertyChangeSupport.firePropertyChange("tickPosition", oldTickPosition, newTickPosition);
        return oldTickPosition;
    }


    @Override
    public double getLengthBeats() {
        return ticksToBeats(block.getDurationTicks());
    }

    @Override
    public double getLengthMs() {
        return ticksToMs(block.getDurationTicks());
    }

    @Override
    public long getLengthTicks() { return block.getDurationTicks(); };

    public MidiTrack getParentMidiTrack() {
        return parentMidiTrack;
    }

    public Block getBlock() {
        return block;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * Handles end-of-track meta events to implement loop playback.
     * <p>
     * When looping is enabled and the end of the block is reached, resets the tick position and restarts playback.
     *
     * @param meta the meta event received from the sequencer
     */
    @Override
    public void meta(MetaMessage meta) {
        if (meta.getType() == TimelineController.PLAYER_END_META_TYPE) {
            if (loop) {
                setTickPosition(0);
                try {
                    play();
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException("Failed to loop sequencer upon reaching end of block", e);
                }
            }
        }
    }
}
