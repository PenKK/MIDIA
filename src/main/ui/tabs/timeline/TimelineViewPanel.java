package ui.tabs.timeline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.ruler.RulerScrollPane;
import ui.tabs.timeline.midi.TrackPanel;
import ui.tabs.timeline.midi.TrackScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener {

    private TrackScrollPane midiTrackScrollPane;
    private RulerScrollPane rulerScrollPane;

    // EFFECTS: Creates timeline view container, and initializes sub components
    public TimelineViewPanel(TimelineController timelineController, DawClipboard dawClipboard) {
        midiTrackScrollPane = new TrackScrollPane(timelineController, dawClipboard);
        rulerScrollPane = new RulerScrollPane(timelineController);

        timelineController.addObserver(this);
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
        BoundedRangeModel tracksBar = midiTrackScrollPane.getHorizontalScrollBar().getModel();
        rulerScrollPane.getHorizontalScrollBar().setModel(tracksBar);
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
            case "timelineReplaced":
            case "horizontalScaleFactor":
                updateRulerDimensions();
            default:
                break;
        }
    }
}
