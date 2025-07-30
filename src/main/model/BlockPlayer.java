package model;

import java.awt.event.ActionEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;

import model.event.Event;
import model.event.EventLog;

public class BlockPlayer extends Player {

    private final Block block;
    private final MidiTrack parentMidiTrack;

    public BlockPlayer(Block block, MidiTrack parentMidiTrack, float initialBpm) {
        super();
        this.block = block;
        this.parentMidiTrack = parentMidiTrack;

        setBPM(initialBpm);
    }

    @Override
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        Track track = sequence.createTrack();
        
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
    public double getLengthBeats() {
        return ticksToBeats(block.getDurationTicks());
    }

    @Override
    public double getLengthMs() {
        return ticksToMs(block.getDurationTicks());
    }
}
