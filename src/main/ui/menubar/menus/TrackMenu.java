package ui.menubar.menus;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import model.MidiTrack;
import model.Timeline;
import model.instrument.Instrument;
import ui.menubar.dialog.BlockInputDialog;
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

    // EFFECTS: Prompts user for track information and creates it on the timeline singleton instance
    private void createTrack() {
        TrackInputDialog input = new TrackInputDialog(getParent().getParent());

        String trackName = input.getInputName();
        boolean percussive = input.isPercussive();
        Instrument instrument = input.getInstrument();

        if (trackName == null) {
            return;
        }

        MidiTrack midiTrack = Timeline.getInstance().createMidiTrack(trackName, instrument, percussive);

        if (midiTrack == null) {
            JOptionPane.showMessageDialog(this, "You have already reached the maximum number of instrumental tracks," 
                                              + "15.\n Track was not created", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }


    // EFFECTS: listens for button actions on menu items and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(newTrack)) {
            createTrack();
        } else if (source.equals(addBlock)) {
            addBlock();
        }
    }

    private void addBlock() {
        BlockInputDialog inputDialog = new BlockInputDialog(getParent().getParent());
    }

}
