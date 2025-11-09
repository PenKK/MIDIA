package ui.menubar.menus;

import java.awt.event.ActionListener;

import javax.swing.JMenu;

import model.TimelineController;

/**
 * Base class for menus in the application menu bar.
 */
public abstract class Menu extends JMenu implements ActionListener {

    protected TimelineController timelineController;

    /**
     * Creates a menu with the specified label and controller wiring.
     *
     * @param labelText          the displayed text for the menu
     * @param timelineController the controller to handle menu actions
     */
    Menu(String labelText, TimelineController timelineController) {
        this.timelineController = timelineController;
        this.setText(labelText);
    }
}
