package ui.menubar;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

// Represents a Menu in the MenuBar
public class Menu extends JMenu {

    // EFFECTS: Creates menu with the specified name
    Menu(String labelText) {
        this.setText(labelText);
    }
}
