package ui.windows.piano.roll.editor;

import model.BlockPlayer;
import model.Note;
import model.Timeline;
import model.TimelineController;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public class PianoRollEditorMouseAdapter extends MouseInputAdapter {

    private final BlockPlayer blockPlayer;
    private final TimelineController timelineController;

    private boolean draggingNote;

    public PianoRollEditorMouseAdapter(BlockPlayer blockPlayer, TimelineController timelineController) {
        this.blockPlayer = blockPlayer;
        this.timelineController = timelineController;
        this.draggingNote = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();
        long tick = timeline.scalePixelToTick(e.getX());
        int pitch = 127 - e.getY() / PianoRollNoteDisplay.KEY_HEIGHT;

        Note clickedNote = getNoteOnPosition(tick, pitch);
        if (clickedNote == null && e.getButton() == MouseEvent.BUTTON1) {
            createNote(tick, pitch);
            return;
        }
        if (clickedNote != null && e.getButton() == MouseEvent.BUTTON3) {
            removeNote(clickedNote);
            return;
        }
        if (clickedNote != null && e.getButton() == MouseEvent.BUTTON1) {
            draggingNote = true;
        }
    }

    private void createNote(long tick, int pitch) {
        if (blockPlayer.getParentMidiTrack().isPercussive() && pitch != Note.PERCUSSIVE_DEFAULT_PITCH) {
            return;
        }

        Timeline timeline = timelineController.getTimeline();
        long startTick = timeline.snapTickLowerBeat(tick);
        long durationTicks = blockPlayer.beatsToTicks(1);
        Note newNote = new Note(pitch, 100, startTick, durationTicks);
        blockPlayer.getBlock().addNote(newNote);
        blockPlayer.getPropertyChangeSupport().firePropertyChange("noteCreated", null, newNote);
        sendNoteChangeEventTimelineController();
    }

    private void removeNote(Note note) {
        boolean removeSuccessful = blockPlayer.getBlock().getNotes().remove(note);
        assert removeSuccessful : String.format("Note %s was note found when removing", note);
        blockPlayer.getPropertyChangeSupport().firePropertyChange("noteRemoved", null, null);
        sendNoteChangeEventTimelineController();
    }

    private Note getNoteOnPosition(long tick, int pitch) {
        for (Note n : blockPlayer.getBlock().getNotes()) {
            if (n.getStartTick() <= tick && n.getStartTick() + n.getDurationTicks() >= tick && pitch == n.getPitch()) {
                return n;
            }
        }

        return null;
    }

    private void sendNoteChangeEventTimelineController() {
        timelineController.getPropertyChangeSupport().firePropertyChange("pianoRollNoteEdited", null, null);
    }
}
