package ui.menubar;

import javax.swing.JMenuBar;

// The Menu bar at the top of the UI, holds many dropdown Menu's
public class MenuBar extends JMenuBar {

    private FileMenu fileMenu;
    private TrackMenu trackMenu;

    // EFFECTS: Initializes all menus and adds them to the menu bar
    public MenuBar() {
        fileMenu = new FileMenu();
        trackMenu = new TrackMenu();
        this.add(fileMenu);
        this.add(trackMenu);
    }
}
