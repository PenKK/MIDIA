package ui.menubar.menus;

import java.awt.event.ActionEvent;

import model.TimelineController;
import ui.menubar.dialog.BeatConfigurationInputDialog;

/**
 * View the menu for timeline-related display settings (e.g., beat configuration).
 */
public class ViewMenu extends Menu {

    private final MenuItem beatConfiguration;
    private final BeatConfigurationInputDialog beatConfigurationInputDialog;

    /**
     * Constructs the View menu and initializes its items.
     *
     * @param timelineController the controller used to handle menu actions
     */
    public ViewMenu(TimelineController timelineController) {
        super("View", timelineController);
        this.timelineController = timelineController;
        beatConfiguration = new MenuItem("Beat configuration", this);
        beatConfigurationInputDialog = new BeatConfigurationInputDialog(this, timelineController);
    }

    /**
     * Handles menu item clicks and opens corresponding dialogs.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(beatConfiguration)) {
            beatConfigurationInputDialog.display();
        }
    }

}
