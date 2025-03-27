package ui.tabs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ui.tabs.piano.roll.PianoRollPanel;
import ui.tabs.timeline.TimelineViewPanel;

// TabbedPane holds the tabs of the TimelinePanel and PianoRollPanel
public class TabbedPane extends JTabbedPane {

    // EFFECTS: creates a JTabbedPane with a timeline and piano roll t ab
    public TabbedPane() {
        this.addTab("Timeline", new TimelineViewPanel());
        this.addTab("Piano Roll", new PianoRollPanel());
    }
}
