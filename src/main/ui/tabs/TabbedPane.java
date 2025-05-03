package ui.tabs;

import javax.swing.JTabbedPane;

import model.TimelineController;
import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    private TimelineViewPanel timelineViewPanel;
    private PianoRollPanel pianoRollPanel;

    // EFFECTS: creates a JTabbedPane with a timeline and piano roll tab
    public TabbedPane(TimelineController tc) {
        timelineViewPanel = new TimelineViewPanel(tc);
        pianoRollPanel = new PianoRollPanel(tc);
        
        this.addTab("Timeline", timelineViewPanel);
        this.addTab("Piano Roll", pianoRollPanel);
    }
}
