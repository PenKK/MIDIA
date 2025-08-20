package model;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import model.event.Event;
import model.event.EventLog;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BlockPlayer extends Player {

    private final Block block;
    private final MidiTrack parentMidiTrack;
    private final PropertyChangeSupport propertyChangeSupport;
    private int volume;

    public BlockPlayer(Block block, MidiTrack parentMidiTrack, float initialBpm) {
        super();
        propertyChangeSupport = new PropertyChangeSupport(this);
        this.block = block;
        this.parentMidiTrack = parentMidiTrack;
        this.volume = parentMidiTrack.getVolume();

        setBPM(initialBpm);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        Track track = sequence.createTrack();

        ShortMessage programChangeMessage = new ShortMessage(ShortMessage.PROGRAM_CHANGE,
                parentMidiTrack.getChannel(), parentMidiTrack.getInstrument().getProgramNumber(),0);
        ShortMessage volMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, parentMidiTrack.getChannel(),
                7, volume);

        track.add(new MidiEvent(programChangeMessage, 0));
        track.add(new MidiEvent(volMessage, 0));
        
        for (Note note : block.getNotes()) {
            MidiTrack.applyNoteToTrack(track, note, parentMidiTrack.isPercussive(), 
                                       parentMidiTrack.getInstrument(), parentMidiTrack.getChannel());
        }

        sequencer.setSequence(sequence);

        Event e = new Event(String.format("Playback sequence was updated in Block [Piano Roll] with instrument %s",
                                          parentMidiTrack.getInstrument()));
        EventLog.getInstance().logEvent(e);
    }

    @Override
    public long setPositionTick(long newPositionTick) {
        long oldPositionTick = super.setPositionTick(newPositionTick);
        propertyChangeSupport.firePropertyChange("positionTick", oldPositionTick, newPositionTick);
        return oldPositionTick;
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
}
