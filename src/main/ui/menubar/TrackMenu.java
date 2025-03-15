package ui.menubar;

import java.awt.event.ActionEvent;
import model.Timeline;
import ui.menubar.dialog.TrackInputDialog;

public class TrackMenu extends Menu {

    private MenuItem newTrack;


    // EFFECTS: creates a track menu and initializes menu items
    TrackMenu() {
        super("Track");
        newTrack = new MenuItem("New Track", this);
    }

    // EFFECTS: Prompts user for track information and creates it on the timeline singleton instance
    private void createTrack() {
        TrackInputDialog input = new TrackInputDialog(getParent().getParent(), "Create Track");
        input.setVisible(true);

        String trackName = input.getInputName();
        boolean percussive = input.isPercussive();
        int programNumber = input.getProgramNumber();

        Timeline.getInstance().createMidiTrack(trackName, programNumber, percussive);
    }


    // EFFECTS: listens for button actions on menu items and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(newTrack)) {
            createTrack();
        }
    }

}
