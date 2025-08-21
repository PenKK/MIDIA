package ui.windows.timeline.midi;

import model.TimelineController;
import ui.common.BlankScrollPane;

import javax.swing.*;
import java.awt.*;

public class TrackLabelScrollPane extends BlankScrollPane {

    private TrackLabelContainer trackLabelContainer;

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
