package ui.menubar.menus;

import java.awt.event.ActionEvent;

import model.TimelineController;
import ui.menubar.dialog.BlockInputDialog;
import ui.menubar.dialog.NoteInputDialog;
import ui.menubar.dialog.TrackInputDialog;

/**
 * Track menu providing commands to create tracks, blocks, and notes.
 */
public class TrackMenu extends Menu {

    private final MenuItem newTrack;
    private final MenuItem addBlock;
    private final MenuItem addNote;

    private final TrackInputDialog trackInputDialog;
    private final BlockInputDialog blockInputDialog;
    private final NoteInputDialog noteInputDialog;

    /**
     * Constructs the Track menu and initializes dialogs and menu items.
     *
     * @param timelineController the controller used to carry out menu actions
     */
    public TrackMenu(TimelineController timelineController) {
        super("Track", timelineController);

        newTrack = new MenuItem("New Track", this);
        addBlock = new MenuItem("Add block", this);
        addNote = new MenuItem("Add note", this);

        trackInputDialog = new TrackInputDialog(this, timelineController);
        blockInputDialog = new BlockInputDialog(this, timelineController);
        noteInputDialog = new NoteInputDialog(this, timelineController);
    }

    /**
     * Opens a dialog to create a new track.
     */
    private void createTrack() {
        trackInputDialog.display();
    }

    /**
     * Opens a dialog to add a block to a selected track.
     */
    private void addBlock() {
        blockInputDialog.display();
    }

    /**
     * Opens a dialog to add a note to a selected block.
     */
    private void addNote() {
        noteInputDialog.display();
    }

    /**
     * Routes menu actions to the appropriate dialog.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(newTrack)) {
            createTrack();
        } else if (source.equals(addBlock)) {
            addBlock();
        } else if (source.equals(addNote)) {
            addNote();
        }
    }

}
