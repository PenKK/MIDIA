package ui.windows.piano.roll.editor;

import model.PianoRollPlayer;
import model.Note;
import model.Timeline;
import model.TimelineController;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

/**
 * Mouse adapter for adding, removing, and dragging notes in the piano roll editor.
 */
public class PianoRollEditorMouseAdapter extends MouseInputAdapter {

    private final PianoRollPlayer pianoRollPlayer;
    private final TimelineController timelineController;
    private Note draggedNote;

    private boolean draggingNote;

    /**
     * Creates a mouse adapter bound to a block player and timeline controller.
     */
    public PianoRollEditorMouseAdapter(PianoRollPlayer pianoRollPlayer, TimelineController timelineController) {
        this.pianoRollPlayer = pianoRollPlayer;
        this.timelineController = timelineController;
        this.draggingNote = false;
    }

    /**
     * Handles note creation on left click, deletion on right click, or starts dragging.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();
        long tick = timeline.scalePixelToTick(e.getX());
        int pitch = 127 - e.getY() / PianoRollNoteDisplay.KEY_HEIGHT;

        Note clickedNote = getNoteOnPosition(tick, pitch);
        if (clickedNote == null && e.getButton() == MouseEvent.BUTTON1) {
            draggedNote = createNote(tick, pitch);
            draggingNote = true;
            return;
        }
        if (clickedNote != null && e.getButton() == MouseEvent.BUTTON3) {
            removeNote(clickedNote);
            return;
        }
        if (clickedNote != null && e.getButton() == MouseEvent.BUTTON1) {
            draggedNote = clickedNote;
            draggingNote = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!draggingNote) {
            return;
        }

        Timeline timeline = timelineController.getTimeline();
        long snappedTick = pianoRollPlayer.snapTickLowerBeatDivision(timeline.scalePixelToTick(e.getX()));
        int pitch = 127 - e.getY() / PianoRollNoteDisplay.KEY_HEIGHT;

        if (pitch < 0 || pitch > 127 || snappedTick < 0 || snappedTick >= pianoRollPlayer.getLengthTicks())
            return;


        if (draggedNote.getPitch() != pitch || draggedNote.getStartTick() != snappedTick) {
            removeNote(draggedNote);
            draggedNote = createNote(snappedTick, pitch);
        }
    }

    /**
     * Ends note dragging when the mouse is released.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggingNote)
            notifyControllerNoteChange();
        draggingNote = false;
    }

    /**
     * Creates and adds a new note at the snapped tick and given pitch.
     * For percussive tracks, only the default pitch is allowed.
     */
    private Note createNote(long tick, int pitch) {
        if (pianoRollPlayer.getParentMidiTrack().isPercussive() && pitch != Note.PERCUSSIVE_DEFAULT_PITCH) {
            return null;
        }

        long startTick = pianoRollPlayer.snapTickLowerBeatDivision(tick);
        long durationTicks = pianoRollPlayer.beatsToTicks((double) 1 / pianoRollPlayer.getBeatDivision());
        Note newNote = new Note(pitch, 127, startTick, durationTicks);
        pianoRollPlayer.getBlock().addNote(newNote);

        if (!pianoRollPlayer.isPlaying()) {
            pianoRollPlayer.playNote(pitch);
        }

        pianoRollPlayer.getPropertyChangeSupport().firePropertyChange("noteCreated", null, newNote);
        notifyControllerNoteChange();

        return newNote;
    }

    /**
     * Removes the specified note from the block and notifies listeners.
     */
    private void removeNote(Note note) {
        boolean removeSuccessful = pianoRollPlayer.getBlock().getNotes().remove(note);
        assert removeSuccessful : String.format("Note %s was note found when removing", note);
        pianoRollPlayer.getPropertyChangeSupport().firePropertyChange("noteRemoved", null, null);
        notifyControllerNoteChange();
    }

    /**
     * Returns the note under the given tick and pitch, or null if none.
     */
    private Note getNoteOnPosition(long tick, int pitch) {
        for (Note n : pianoRollPlayer.getBlock().getNotes()) {
            if (n.getStartTick() <= tick && n.getStartTick() + n.getDurationTicks() >= tick && pitch == n.getPitch()) {
                return n;
            }
        }

        return null;
    }

    /**
     * Notifies the controller that a note edit occurred, triggering UI updates.
     */
    private void notifyControllerNoteChange() {
        timelineController.getPropertyChangeSupport().firePropertyChange("pianoRollNoteEdited", null, null);
    }
}
