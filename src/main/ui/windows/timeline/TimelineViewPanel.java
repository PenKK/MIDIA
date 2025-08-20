package ui.windows.timeline;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.ruler.RulerScrollPane;
import ui.windows.timeline.midi.TrackLabelContainer;
import ui.windows.timeline.midi.TrackLabelPanel;
import ui.windows.timeline.midi.TrackLabelScrollPane;
import ui.windows.timeline.midi.TrackScrollPane;
import ui.windows.timeline.ruler.TimelineRulerScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener {

    private final JPanel topHorizontalPanel;
    private final JPanel bottomHorizontalPanel;

    private final TrackScrollPane trackScrollPane;
    private final TimelineRulerScrollPane rulerScrollPane;
    private final TrackLabelScrollPane trackLabelScrollPane;
    
    private static final MatteBorder BLANK_BORDER = new MatteBorder(0, 0, 0, 0, Color.GRAY);

    // EFFECTS: Creates timeline view container, and initializes subcomponents
    public TimelineViewPanel(TimelineController timelineController, DawClipboard dawClipboard) {
        topHorizontalPanel = new JPanel();
        bottomHorizontalPanel = new JPanel();

        rulerScrollPane = new TimelineRulerScrollPane(timelineController);
        trackScrollPane = new TrackScrollPane(timelineController, dawClipboard);
        trackLabelScrollPane = new TrackLabelScrollPane(timelineController);

        initTopHorizontalPanel();
        initBottomHorizontalPanel();

        timelineController.addObserver(this);
        syncScrollBars(trackScrollPane.getHorizontalScrollBar(), rulerScrollPane.getHorizontalScrollBar());
        syncScrollBars(trackScrollPane.getVerticalScrollBar(), trackLabelScrollPane.getVerticalScrollBar());

        this.setLayout(new BorderLayout());
        this.setBorder(BLANK_BORDER);
        this.add(topHorizontalPanel, BorderLayout.NORTH);
        this.add(bottomHorizontalPanel, BorderLayout.CENTER);
    }

    private void initTopHorizontalPanel() {
        topHorizontalPanel.setLayout(new BoxLayout(topHorizontalPanel, BoxLayout.X_AXIS));
        topHorizontalPanel.setBorder(BLANK_BORDER);
        topHorizontalPanel.setAlignmentX(LEFT_ALIGNMENT);
        topHorizontalPanel.add(getFillerPanel());
        topHorizontalPanel.add(rulerScrollPane);
    }

    private void initBottomHorizontalPanel() {
        bottomHorizontalPanel.setLayout(new BoxLayout(bottomHorizontalPanel, BoxLayout.X_AXIS));
        bottomHorizontalPanel.add(trackLabelScrollPane);
        bottomHorizontalPanel.add(trackScrollPane);
        bottomHorizontalPanel.setBorder(BLANK_BORDER);
        bottomHorizontalPanel.setAlignmentX(LEFT_ALIGNMENT);
    }

    public static JPanel getFillerPanel() {
        JPanel rulerFillerPanel = new JPanel();
        Dimension fillerDimension = new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, RulerScrollPane.RULER_HEIGHT);
        rulerFillerPanel.setPreferredSize(fillerDimension);
        rulerFillerPanel.setMinimumSize(fillerDimension);
        rulerFillerPanel.setBackground(Color.GRAY);
        return rulerFillerPanel;
    }

    // MODIFIES: this
    // EFFECTS: triggers adjustment listener for when the midiTrackScrollPane is scrolled
    public static void syncScrollBars(JScrollBar leader, JScrollBar follower) {
        leader.addAdjustmentListener(e -> {
            if (!follower.getValueIsAdjusting()) {
                follower.setValue(leader.getValue());
            }
        });
    }

    // MODIFIES: this
    // EFFECTS: resizes the ruler's width to match the midiTrackScrollPane's width
    private void updateRulerDimensions() {
        int trackContentWidth = trackScrollPane.getContainerWidth();
        rulerScrollPane.updateWidth(trackContentWidth + 200); // 200 is padding
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
