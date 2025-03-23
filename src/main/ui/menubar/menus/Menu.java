package ui.menubar.menus;

import java.awt.event.ActionListener;

import javax.swing.JMenu;

// Represents a Menu in the MenuBar
public abstract class Menu extends JMenu implements ActionListener {

    // EFFECTS: Creates menu with the specified name
    Menu(String labelText) {
        this.setText(labelText);
    }
}
