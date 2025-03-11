package ui.tabs;

import javax.swing.JTabbedPane;

import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    TimelineViewPanel timelineViewPanel;
    PianoRollPanel pianoRollPanel;

    // EFFECTS: Initialized the pane with the timeline and pianoroll tabs
    public TabbedPane() {
        timelineViewPanel = new TimelineViewPanel();
        pianoRollPanel = new PianoRollPanel();

        this.add(timelineViewPanel);
        this.add(pianoRollPanel);
    }
}
