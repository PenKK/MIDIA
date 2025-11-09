package ui.menubar;

import javax.swing.JMenuBar;

import model.TimelineController;
import ui.menubar.menus.FileMenu;
import ui.menubar.menus.TrackMenu;
import ui.menubar.menus.ViewMenu;

/**
 * The application menu bar containing File, Track, and View menus.
 */
public class MenuBar extends JMenuBar {

    private final FileMenu fileMenu;
    private final TrackMenu trackMenu;
    private final ViewMenu viewMenu;

    /**
     * Constructs the menu bar and initializes its menus.
     *
     * @param timelineController the controller used to wire menu actions
     */
    public MenuBar(TimelineController timelineController) {
        fileMenu = new FileMenu(timelineController);
        trackMenu = new TrackMenu(timelineController);
        viewMenu = new ViewMenu(timelineController);
        
        this.add(fileMenu);
        this.add(trackMenu);
        this.add(viewMenu);
    }
}
