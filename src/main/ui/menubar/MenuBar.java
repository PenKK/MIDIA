package ui.menubar;

import javax.swing.JMenuBar;

import model.TimelineController;
import ui.menubar.menus.FileMenu;
import ui.menubar.menus.TrackMenu;
import ui.menubar.menus.ViewMenu;

// The Menu bar at the top of the UI, holds many dropdown Menu's
public class MenuBar extends JMenuBar {

    private FileMenu fileMenu;
    private TrackMenu trackMenu;
    private ViewMenu viewMenu;

    // EFFECTS: Initializes all menus and adds them to the menu bar
    public MenuBar(TimelineController timelineController) {
        fileMenu = new FileMenu(timelineController);
        trackMenu = new TrackMenu(timelineController);
        viewMenu = new ViewMenu(timelineController);
        
        this.add(fileMenu);
        this.add(trackMenu);
        this.add(viewMenu);
    }
}
