package ui.windows.timeline.midi;

import model.TimelineController;
import ui.common.BlankScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Scroll pane for the vertical list of track labels, with fixed width.
 */
public class TrackLabelScrollPane extends BlankScrollPane {

    private final TrackLabelContainer trackLabelContainer;

    /**
     * Constructs the scroll pane and embeds the track label container.
     */
    public TrackLabelScrollPane(TimelineController timelineController) {
        super();
        trackLabelContainer = new TrackLabelContainer(timelineController);
        setPreferredSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, 0));
        setMaximumSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH,Integer.MAX_VALUE));
        setMinimumSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH,0));
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setViewportView(trackLabelContainer);
    }
}
