package ui.menubar.menus;

import java.awt.event.ActionListener;

import javax.swing.JMenu;

import model.TimelineController;

// Represents a Menu in the MenuBar
public abstract class Menu extends JMenu implements ActionListener {

    protected TimelineController timelineController;
    // EFFECTS: Creates menu with the specified name
    Menu(String labelText, TimelineController timelineController) {
        this.timelineController = timelineController;
        this.setText(labelText);
    }
}
