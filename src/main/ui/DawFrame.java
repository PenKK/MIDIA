package ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
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
import ui.tabs.TabbedPane;

// The frame of the graphical UI. Contains the entirety of the UI.
public class DawFrame extends JFrame implements PropertyChangeListener {

    private MenuBar menuBar;
    private TabbedPane tabbedPane;
    private MediaControlPanel mediaControlPanel;
    private TimelineController timelineController;
    private DawClipboard dawClipboard;

    // EFFECTS: Creates the frame for the application and initializes the tabs and menu bar
    DawFrame() throws MidiUnavailableException, IOException, InvalidMidiDataException {
        timelineController = new TimelineController();
        menuBar = new MenuBar(timelineController);
        mediaControlPanel = new MediaControlPanel(timelineController);
        dawClipboard = new DawClipboard();
        tabbedPane = new TabbedPane(timelineController, dawClipboard);
        
        timelineController.addObserver(this);

        this.setLayout(new BorderLayout());
        this.setIconImage(ImageIO.read(getClass().getResource("/resources/images/logo.png")));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));
        this.addWindowListener(onCloseWindowAdapter());

        this.setJMenuBar(menuBar);
        this.add(mediaControlPanel, BorderLayout.NORTH);
        this.add(tabbedPane, BorderLayout.CENTER);

        updateTitle();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    // MOFIES: this
    // EFFECTS: listens for certain property updates and runs methods accordingly
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

    // MODIFIES: this
    // EFFECTS: updates the title of the JFrame to timeline instance name
    private void updateTitle() {
        String newTitle = timelineController.getTimeline().getProjectName().concat(" - MIDIA");
        this.setTitle(newTitle);
    }

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

    // EFFECTS: auto saves the currently timeline into the auto save directory
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
