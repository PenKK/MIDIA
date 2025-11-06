package ui.windows.timeline.midi;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import model.MidiTrack;
import model.TimelineController;
import model.editing.DawClipboard;
import ui.common.BlankScrollPane;
import ui.common.LineContainerPanel;
import ui.common.RulerDimensionHelper;
import ui.windows.timeline.TimelineLineContainerPanel;

/**
 * Scroll pane for the interactive timeline track area, including track renders and the playhead overlay.
 */
public class TrackScrollPane extends BlankScrollPane
        implements PropertyChangeListener, RulerDimensionHelper.ContainerWidthProvider {

    private final TimelineController timelineController;
    private final LineContainerPanel lineContainer;
    private final DawClipboard dawClipboard;

    /**
     * Constructs the track area, initializes scroll policies, and populates render panels.
     */
    public TrackScrollPane(TimelineController timelineController, DawClipboard dawClipboard) {
        this.timelineController = timelineController;
        this.dawClipboard = dawClipboard;
        lineContainer = new TimelineLineContainerPanel(timelineController,
                timelineController.getTimeline().getPlayer());

        this.setViewportView(lineContainer);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setMinimumSize(new Dimension(0, 0));
        timelineController.addObserver(this);

        updateTrackRenderPanels();
    }

    /**
     * Rebuilds the set of track render panels to match the current timeline content.
     */
    private void updateTrackRenderPanels() {
        lineContainer.removeAll();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            lineContainer.add(new TrackRenderPanel(track, timelineController, dawClipboard));
        }

        revalidate();
        repaint();
    }

    /**
     * Listens for timeline changes to adjust rendered tracks and container width.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timelineReplaced":
                updateTrackRenderPanels();
            case "blockCreated":
            case "blockDeleted":
            case "blockPasted":
            case "blockUpdated":
                updateWidth();
                break;
            case "midiTracks":
            case "horizontalScaleFactor":
                updateTrackRenderPanels();
                break;
            default:
                break;
        }

    }

    /**
     * Notifies listeners when the content width changes, enabling ruler resizes.
     */
    public void updateWidth() {
        lineContainer.revalidate();
        int newWidth = lineContainer.getPreferredSize().width;
        timelineController.getPropertyChangeSupport().firePropertyChange("TrackScrollPaneWidth", null, newWidth);
    }

    /**
     * Returns the preferred width of the track container (for ruler sizing).
     */
    @Override
    public int getContainerWidth() {
        return lineContainer.getPreferredSize().width;
    }
}
