package ui.tabs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ui.MediaControlPanel;
import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JPanel {
    private JTabbedPane tabbedPane = new JTabbedPane();
    private MediaControlPanel mediaControlPanel = new MediaControlPanel();

    // EFFECTS: creates a JPanel with media Controls at the top, and a JTabbedPane below
    public TabbedPane() {
        setLayout(new BorderLayout());
        mediaControlPanel = new MediaControlPanel();

        add(mediaControlPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("Timeline", new TimelineViewPanel());
        tabbedPane.addTab("Piano Roll", new PianoRollPanel());
    }
}
