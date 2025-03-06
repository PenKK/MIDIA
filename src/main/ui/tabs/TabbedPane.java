package ui.tabs;

import javax.swing.JTabbedPane;

import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelinePanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    TimelinePanel timelinePanel;
    PianoRollPanel pianoRollPanel;

    // EFFECTS: Initialized the pane with the timeline and pianoroll tabs
    public TabbedPane() {
        timelinePanel = new TimelinePanel();
        pianoRollPanel = new PianoRollPanel();
        this.add(timelinePanel);
        this.add(pianoRollPanel);
    }
}
