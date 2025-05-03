package ui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;

import model.Event;
import model.EventLog;
import model.Timeline;
import model.TimelineController;
import persistance.JsonWriter;
import ui.menubar.MenuBar;
import ui.menubar.menus.FileMenu;
import ui.tabs.TabbedPane;

// The frame of the graphical UI. Contains the entirety of the UI.
public class DawFrame extends JFrame implements PropertyChangeListener, WindowListener {

    private MenuBar menuBar;
    private TabbedPane tabbedPane;
    private MediaControlPanel mediaControlPanel;
    private TimelineController timelineController;

    // EFFECTS: Creates the frame for the application and initializes the tabs and menu bar
    DawFrame() throws MidiUnavailableException, IOException, InvalidMidiDataException {
        timelineController = new TimelineController();
        tabbedPane = new TabbedPane(timelineController);
        menuBar = new MenuBar(timelineController);
        mediaControlPanel = new MediaControlPanel(timelineController);
        timelineController.addObserver(this);

        this.setLayout(new BorderLayout());
        this.setIconImage(ImageIO.read(new File("lib/images/logo.png")));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));
        this.addWindowListener(this);

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
    }

    // MODIFIES: this
    // EFFECTS: updates the title of the JFrame to timeline instance name
    private void updateTitle() {
        String newTitle = timelineController.getTimeline().getProjectName().concat(" - Digital Audio Workstation");
        this.setTitle(newTitle);
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

    @Override
    public void windowClosing(WindowEvent e) {
        autoSave();
        for (Event event : EventLog.getInstance()) {
            System.out.printf("[%s] %s%n", event.getDate(), event.getDescription());
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }
}
