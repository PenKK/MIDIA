package ui;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JFrame;

import model.Timeline;
import ui.menubar.MenuBar;
import ui.tabs.TabbedPane;

// The frame of the graphical UI. Contains the entirety of the UI.
public class DawFrame extends JFrame implements PropertyChangeListener {

    private MenuBar menuBar;
    private TabbedPane tabbedPane;

    // EFFECTS: Creates the frame for the application and initializes the tabs and menu bar
    DawFrame() throws MidiUnavailableException, IOException {
        tabbedPane = new TabbedPane();
        menuBar = new MenuBar();
        Timeline.addObserver(this);

        this.setIconImage(ImageIO.read(new File("lib/images/logo.png")));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(new Rectangle(800, 600));

        this.setJMenuBar(menuBar);
        this.add(tabbedPane);

        updateTitle();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    // MOFIES: this
    // EFFECTS: listens for certain property updates and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timeline") || propertyName.equals("projectName")) {
            updateTitle();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the title of the JFrame to timeline instance name
    private void updateTitle() {
        String newTitle = Timeline.getInstance().getProjectName().concat(" - Digital Audio Workstation");
        this.setTitle(newTitle);
    }
}
