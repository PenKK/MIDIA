package ui.menubar.menus;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import model.MidiTrack;
import model.Timeline;
import model.instrument.Instrument;
import ui.menubar.dialog.BlockInputDialog;
import ui.menubar.dialog.NoteInputDialog;
import ui.menubar.dialog.TrackInputDialog;

// The track menu option in the menu bar
public class TrackMenu extends Menu {

    private MenuItem newTrack;
    private MenuItem addBlock;
    private MenuItem addNote;

    // EFFECTS: creates a track menu and initializes menu items
    public TrackMenu() {
        super("Track");
        newTrack = new MenuItem("New Track", this);
        addBlock = new MenuItem("Add block", this);
        addNote = new MenuItem("Add note", this);
    }

    // EFFECTS: Prompts user to create a new track
    private void createTrack() {
        new TrackInputDialog(getParent().getParent());
    }

    // EFFECTS: Prompts user to create a new block
    private void addBlock() {
        new BlockInputDialog(getParent().getParent());
    }

    // EFFECTS: Prompts user to create a new note
    private void addNote() {
        new NoteInputDialog(getParent().getParent());
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
