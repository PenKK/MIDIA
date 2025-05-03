package ui.menubar.menus;

import java.awt.event.ActionEvent;

import model.TimelineController;
import ui.menubar.dialog.BlockInputDialog;
import ui.menubar.dialog.NoteInputDialog;
import ui.menubar.dialog.TrackInputDialog;

// The track menu option in the menu bar
public class TrackMenu extends Menu {

    private MenuItem newTrack;
    private MenuItem addBlock;
    private MenuItem addNote;

    private TrackInputDialog trackInputDialog;
    private BlockInputDialog blockInputDialog;
    private NoteInputDialog noteInputDialog;

    // EFFECTS: creates a track menu and initializes menu items
    public TrackMenu(TimelineController timelineController) {
        super("Track", timelineController);

        newTrack = new MenuItem("New Track", this);
        addBlock = new MenuItem("Add block", this);
        addNote = new MenuItem("Add note", this);

        trackInputDialog = new TrackInputDialog(this, timelineController);
        blockInputDialog = new BlockInputDialog(this, timelineController);
        noteInputDialog = new NoteInputDialog(this, timelineController);
    }

    // EFFECTS: Prompts user to create a new track
    private void createTrack() {
        trackInputDialog.display();
    }

    // EFFECTS: Prompts user to create a new block
    private void addBlock() {
        blockInputDialog.display();
    }

    // EFFECTS: Prompts user to create a new note
    private void addNote() {
        noteInputDialog.display();
    }

    // EFFECTS: listens for button actions on menu items and runs methods accordingly
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
