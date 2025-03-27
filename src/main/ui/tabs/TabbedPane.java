package ui.tabs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    private TimelineViewPanel timelineViewPanel;
    private PianoRollPanel pianoRollPanel;

    // EFFECTS: creates a JTabbedPane with a timeline and piano roll tab
    public TabbedPane() {
        timelineViewPanel = new TimelineViewPanel();
        pianoRollPanel = new PianoRollPanel();
        
        this.addTab("Timeline", timelineViewPanel);
        this.addTab("Piano Roll", pianoRollPanel);
    }
}
