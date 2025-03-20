package ui.tabs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import model.Timeline;
import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.MediaControlPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JPanel {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private MediaControlPanel mediaControlPanel = new MediaControlPanel();

    public TabbedPane() {
        setLayout(new BorderLayout());
        mediaControlPanel = new MediaControlPanel();

        add(mediaControlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Timeline", new TimelineViewPanel());
        tabbedPane.addTab("Piano Roll", new PianoRollPanel());
    }
}
