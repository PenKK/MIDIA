package ui.windows.timeline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.windows.timeline.midi.TrackLabelContainer;
import ui.windows.timeline.midi.TrackLabelPanel;
import ui.windows.timeline.midi.TrackScrollPane;
import ui.windows.timeline.ruler.TimelineRulerScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener {

    private final TrackScrollPane midiTrackScrollPane;
    private final TimelineRulerScrollPane rulerScrollPane;
    private final TrackLabelContainer trackLabelContainer;

    // EFFECTS: Creates timeline view container, and initializes subcomponents
    public TimelineViewPanel(TimelineController timelineController, DawClipboard dawClipboard) {
        rulerScrollPane = new TimelineRulerScrollPane(timelineController);
        JPanel horizontalPanel = new JPanel();
        midiTrackScrollPane = new TrackScrollPane(timelineController, dawClipboard);
        trackLabelContainer = new TrackLabelContainer(timelineController);

        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.add(trackLabelContainer);
        horizontalPanel.add(midiTrackScrollPane);
        horizontalPanel.setBorder(TrackLabelPanel.BORDER);
        horizontalPanel.setAlignmentX(LEFT_ALIGNMENT);

        timelineController.addObserver(this);
        syncHorizontalScrollBars();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackLabelPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(rulerScrollPane);
        this.add(horizontalPanel);
    }

    // MODIFIES: this
    // EFFECTS: triggers adjustment listener for when the midiTrackScrollPane is scrolled
    private void syncHorizontalScrollBars() {
        BoundedRangeModel tracksBar = midiTrackScrollPane.getHorizontalScrollBar().getModel();
        rulerScrollPane.getHorizontalScrollBar().setModel(tracksBar);
    }

    // MODIFIES: this
    // EFFECTS: resizes the ruler's width to match the midiTrackScrollPane's width
    private void updateRulerDimensions() {
        rulerScrollPane.updateWidth(midiTrackScrollPane.getContainerWidth());
    }

    // MODIFIES: this
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
