package ui.menubar.menus;

import javax.swing.JMenuItem;

// A menu item that has a parent menu that listens for actions
public class MenuItem extends JMenuItem {
    
    // Create JMenuItem with specified text and a parent menu that listens foractions
    MenuItem(String text, Menu parentMenu) {
        super(text);
        this.addActionListener(parentMenu);
        parentMenu.add(this);
    }
}
