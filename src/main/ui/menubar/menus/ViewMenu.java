package ui.menubar.menus;

import java.awt.event.ActionEvent;

import model.TimelineController;
import ui.menubar.dialog.BeatConfigurationInputDialog;

// A menubar with items that control how the timeline is scaled / viewed
public class ViewMenu extends Menu {

    private MenuItem beatConfiguration;
    private BeatConfigurationInputDialog beatConfigurationInputDialog;

    // EFFECTS: creates a view menu with a title and MenuItems
    public ViewMenu(TimelineController timelineController) {
        super("View", timelineController);
        this.timelineController = timelineController;
        beatConfiguration = new MenuItem("Beat configuration", this);
        beatConfigurationInputDialog = new BeatConfigurationInputDialog(this, timelineController);
    }
    
    // EFFECTS: listens for clicks on menu items and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(beatConfiguration)) {
            beatConfiguration();
        }
    }

    // EFFECTS: creates a new BeatConfigurationInputDialog for user input in changing ruler variables 
    private void beatConfiguration() {
        beatConfigurationInputDialog.display();
    }

}
