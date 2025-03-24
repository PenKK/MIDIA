package ui.tabs.timeline;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import model.Timeline;
import ui.ruler.RulerScrollPane;
import ui.tabs.timeline.midi.TrackPanel;
import ui.tabs.timeline.midi.TrackScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener, AdjustmentListener {

    private TrackScrollPane midiTrackScrollPane;
    private RulerScrollPane rulerScrollPane;

    // EFFECTS: Creates timeline view container, and initializes sub components
    public TimelineViewPanel() {
        midiTrackScrollPane = new TrackScrollPane();
        rulerScrollPane = new RulerScrollPane();

        Timeline.addObserver(this);
        syncHorizontalScrollBars();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(rulerScrollPane);
        this.add(midiTrackScrollPane);
    }

    // MODIFIES: this
    // EFFECTS: triggers adjustment listener for when the midiTrackScrollPane is scrolled
    private void syncHorizontalScrollBars() {
        JScrollBar tracksBar = midiTrackScrollPane.getHorizontalScrollBar();
        tracksBar.addAdjustmentListener(this);
    }

    // MODFIES: this
    // EFFECTS: resizes the ruler's width to match the midiTrackScrollPane's width
    private void updateRulerDimensions() {
        rulerScrollPane.updateWidth(midiTrackScrollPane.getContainerWidth());
    }

    // MODFIES: this
    // EFFECTS: listens for changes in the timeline and executes methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timeline":
            case "horizontalScale":
                updateRulerDimensions();
            default:
                break;
        }
    }

    // MODIFIES: this
    // EFFECTS: adjusts the (invisible) scrollBar of the ruler to copy the midiTrackScrollPane scrollbar
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        if (e.getSource().equals(midiTrackScrollPane.getHorizontalScrollBar())) {
            JScrollBar rulerBar = rulerScrollPane.getHorizontalScrollBar();
            rulerBar.setValue(e.getValue());
        }
    }
}
