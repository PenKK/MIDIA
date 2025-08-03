package ui.windows.timeline;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.ruler.RulerScrollPane;
import ui.windows.timeline.midi.TrackLabelContainer;
import ui.windows.timeline.midi.TrackLabelPanel;
import ui.windows.timeline.midi.TrackScrollPane;
import ui.windows.timeline.ruler.TimelineRulerScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener {

    private final TrackScrollPane trackScrollPane;
    private final TimelineRulerScrollPane rulerScrollPane;
    private final TrackLabelContainer trackLabelContainer;

    // EFFECTS: Creates timeline view container, and initializes subcomponents
    public TimelineViewPanel(TimelineController timelineController, DawClipboard dawClipboard) {
        JPanel topHorizontalPanel = new JPanel();
        JPanel bottomHorizontalPanel = new JPanel();
        JPanel rulerFillerPanel = new JPanel();

        rulerScrollPane = new TimelineRulerScrollPane(timelineController);
        trackScrollPane = new TrackScrollPane(timelineController, dawClipboard);
        trackLabelContainer = new TrackLabelContainer(timelineController);

        Dimension fillerDimension = new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, RulerScrollPane.RULER_HEIGHT);
        MatteBorder blankBorder = new MatteBorder(0, 0, 0, 0, Color.GRAY);
        rulerFillerPanel.setPreferredSize(fillerDimension);
        rulerFillerPanel.setMinimumSize(fillerDimension);
        topHorizontalPanel.setLayout(new BoxLayout(topHorizontalPanel, BoxLayout.X_AXIS));
        topHorizontalPanel.setBorder(blankBorder);
        topHorizontalPanel.setAlignmentX(LEFT_ALIGNMENT);
        topHorizontalPanel.add(rulerFillerPanel);
        topHorizontalPanel.add(rulerScrollPane);

        bottomHorizontalPanel.setLayout(new BoxLayout(bottomHorizontalPanel, BoxLayout.X_AXIS));
        bottomHorizontalPanel.add(trackLabelContainer);
        bottomHorizontalPanel.add(trackScrollPane);
        bottomHorizontalPanel.setBorder(blankBorder);
        bottomHorizontalPanel.setAlignmentX(LEFT_ALIGNMENT);

        timelineController.addObserver(this);
        syncHorizontalScrollBars();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(blankBorder);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(topHorizontalPanel);
        this.add(bottomHorizontalPanel);
    }

    // MODIFIES: this
    // EFFECTS: triggers adjustment listener for when the midiTrackScrollPane is scrolled
    private void syncHorizontalScrollBars() {
        BoundedRangeModel tracksBar = trackScrollPane.getHorizontalScrollBar().getModel();
        rulerScrollPane.getHorizontalScrollBar().setModel(tracksBar);
    }

    // MODIFIES: this
    // EFFECTS: resizes the ruler's width to match the midiTrackScrollPane's width
    private void updateRulerDimensions() {
        // I have no idea why the width is 10 larger than it should be
        rulerScrollPane.updateWidth(trackScrollPane.getContainerWidth() - 10);
    }

    // MODIFIES: this
    // EFFECTS: listens for changes in the timeline and executes methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "TrackScrollPaneWidth":
            case "timelineReplaced":
            case "horizontalScaleFactor":
                updateRulerDimensions();
                break;
        }
    }
}
