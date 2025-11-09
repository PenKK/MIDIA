package ui.menubar.menus;

import javax.swing.JMenuItem;

/**
 * A menu item that registers its parent menu as an action listener.
 */
public class MenuItem extends JMenuItem {

    /**
     * Creates a JMenuItem with the specified text and adds it to the parent menu.
     *
     * @param text       the menu item text
     * @param parentMenu the parent menu which will receive action events
     */
    MenuItem(String text, Menu parentMenu) {
        super(text);
        this.addActionListener(parentMenu);
        parentMenu.add(this);
    }
}
