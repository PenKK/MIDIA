package model;

import javax.sound.midi.*;

import model.event.Event;
import model.event.EventLog;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlockPlayer extends Player implements MetaEventListener {

    private final Block block;
    private final MidiTrack parentMidiTrack;
    private final PropertyChangeSupport propertyChangeSupport;
    private int volume;
    private boolean loop;

    public BlockPlayer(Block block, MidiTrack parentMidiTrack, float initialBpm) {
        super();
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.block = block;
        this.parentMidiTrack = parentMidiTrack;
        this.volume = parentMidiTrack.getVolume();
        this.loop = false;

        sequencer.addMetaEventListener(this);

        setBPM(initialBpm);
    }

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
            receiver.send(offMessage, PULSES_PER_QUARTER_NOTE * 2);

        } catch (MidiUnavailableException e) {
            throw new RuntimeException("Synthesizer unavailable", e);
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException("Invalid midi in synthesizer", e);
        }
    }

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
