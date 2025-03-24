package ui.menubar;

import javax.swing.JMenuBar;

import ui.menubar.menus.FileMenu;
import ui.menubar.menus.TrackMenu;
import ui.menubar.menus.ViewMenu;

// The Menu bar at the top of the UI, holds many dropdown Menu's
public class MenuBar extends JMenuBar {

    private FileMenu fileMenu;
    private TrackMenu trackMenu;
    private ViewMenu viewMenu;

    // EFFECTS: Initializes all menus and adds them to the menu bar
    public MenuBar() {
        fileMenu = new FileMenu();
        trackMenu = new TrackMenu();
        viewMenu = new ViewMenu();
        
        this.add(fileMenu);
        this.add(trackMenu);
        this.add(viewMenu);
    }
}
