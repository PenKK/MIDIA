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

    /**
     * Ends note dragging when the mouse is released.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        draggingNote = false;
    }

    /**
     * Creates and adds a new note at the snapped tick and given pitch.
     * For percussive tracks, only the default pitch is allowed.
     */
    private void createNote(long tick, int pitch) {
        if (pianoRollPlayer.getParentMidiTrack().isPercussive() && pitch != Note.PERCUSSIVE_DEFAULT_PITCH) {
            return;
        }

        Timeline timeline = timelineController.getTimeline();
        long startTick = timeline.snapTickLowerBeat(tick);
        long durationTicks = pianoRollPlayer.beatsToTicks(1);
        Note newNote = new Note(pitch, 127, startTick, durationTicks);
        pianoRollPlayer.getBlock().addNote(newNote);

        if (!pianoRollPlayer.isPlaying()) {
            pianoRollPlayer.playNote(pitch);
        }

        pianoRollPlayer.getPropertyChangeSupport().firePropertyChange("noteCreated", null, newNote);
        notifyControllerNoteChange();
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
