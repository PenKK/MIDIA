package ui.menubar;

import javax.swing.JMenu;

// Represents a Menu in the MenuBar
public class Menu extends JMenu {

    // EFFECTS: Creates menu with the specified name
    Menu(String labelText) {
        this.setText(labelText);
    }
}
