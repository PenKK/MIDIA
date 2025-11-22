package ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import model.Timeline;
import model.TimelineController;
import model.editing.DawClipboard;
import model.event.Event;
import model.event.EventLog;
import persistance.JsonWriter;
import ui.media.MediaControlPanel;
import ui.menubar.MenuBar;
import ui.menubar.menus.FileMenu;
import ui.windows.timeline.TimelineViewPanel;

/**
 * The main application frame for the graphical UI, containing all UI components.
 */
public class DawFrame extends JFrame implements PropertyChangeListener {

    private final MenuBar menuBar;
    private final TimelineViewPanel timelineViewPanel;
    private final MediaControlPanel mediaControlPanel;
    private final TimelineController timelineController;
    private final DawClipboard dawClipboard;

    /**
     * Creates the main application frame and initializes UI components.
     *
     * @throws IOException              if UI resources cannot be loaded
     */
    DawFrame() throws IOException {
        timelineController = new TimelineController();
        menuBar = new MenuBar(timelineController);
        mediaControlPanel = new MediaControlPanel(timelineController);
        dawClipboard = new DawClipboard();
        timelineViewPanel = new TimelineViewPanel(timelineController, dawClipboard);

        timelineController.addObserver(this);

        this.setLayout(new BorderLayout());
        this.setIconImage(ImageIO.read(Objects
                .requireNonNull(getClass().getResourceAsStream("/images/logo.png"))));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));
        this.addWindowListener(onCloseWindowAdapter());

        this.setJMenuBar(menuBar);
        this.add(mediaControlPanel, BorderLayout.NORTH);
        this.add(timelineViewPanel, BorderLayout.CENTER);

        updateTitle();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    /**
     * Handles timeline-related property changes to update the frame UI.
     *
     * @param evt the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timelineReplaced") || propertyName.equals("projectName")) {
            updateTitle();
        }

        if (propertyName.equals("timelineAboutToBeReplaced")) {
            autoSave();
        }
    }

    /**
     * Updates the window title to reflect the current timeline's project name.
     */
    private void updateTitle() {
        String newTitle = timelineController.getTimeline().getProjectName().concat(" - MIDIA");
        this.setTitle(newTitle);
    }

    /**
     * Creates a window listener that auto-saves and prints the event log on close.
     *
     * @return a WindowAdapter that handles windowClosing to auto-save and dump the event log
     */
    private WindowAdapter onCloseWindowAdapter() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                autoSave();
                for (Event event : EventLog.getInstance()) {
                    System.out.printf("[%s] %s%n", event.getDate(), event.getDescription());
                }
            }
        };
    }

    /**
     * Auto-saves the current timeline to the auto-save directory.
     */
    public void autoSave() {
        Timeline t = timelineController.getTimeline();
        JsonWriter writer = new JsonWriter(FileMenu.AUTO_SAVE_FILE_DIRECTORY.concat(t.getProjectName()));

        try {
            writer.open();
            writer.write(t);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to auto save, invalid path");
        }
    }
}
