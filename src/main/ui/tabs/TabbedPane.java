package ui.tabs;

import javax.swing.JTabbedPane;

import model.TimelineController;
import ui.tabs.piano.roll.PianoRollViewPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    private TimelineViewPanel timelineViewPanel;
    private PianoRollViewPanel pianoRollPanel;

    // EFFECTS: creates a JTabbedPane with a timeline and piano roll tab
    public TabbedPane(TimelineController tc) {
        timelineViewPanel = new TimelineViewPanel(tc);
        pianoRollPanel = new PianoRollViewPanel(tc);
        
        this.addTab("Timeline", timelineViewPanel);
        this.addTab("Piano Roll", pianoRollPanel);
    }
}
