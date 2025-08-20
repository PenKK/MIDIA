package ui.windows.timeline.midi;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import model.MidiTrack;
import model.TimelineController;
import model.editing.DawClipboard;
import ui.ruler.BlankScrollPane;
import ui.ruler.LineContainerPanel;
import ui.windows.timeline.TimelineLineContainerPanel;

// JPanel that holds the interactable view of the timeline, rendered using graphics
public class TrackScrollPane extends BlankScrollPane implements PropertyChangeListener {

    private final TimelineController timelineController;
    private final LineContainerPanel lineContainer;
    private final DawClipboard dawClipboard;

    // EFFECTS: initializes the timeline 
    public TrackScrollPane(TimelineController timelineController, DawClipboard dawClipboard) {
        this.timelineController = timelineController;
        this.dawClipboard = dawClipboard;
        lineContainer = new TimelineLineContainerPanel(timelineController, timelineController.getTimeline().getPlayer());

        this.setViewportView(lineContainer);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setMinimumSize(new Dimension(0, 0));
        timelineController.addObserver(this);

        updateTrackRenderPanels();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void updateTrackRenderPanels() {
        lineContainer.removeAll();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            lineContainer.add(new TrackRenderPanel(track, timelineController, dawClipboard));
        }

        revalidate();
        repaint();
    }

    // MODIFIES: this
    // EFFECTS: listens for proper change events and executes methods accordingly
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

    public void updateWidth() {
        lineContainer.revalidate();
        int newWidth = lineContainer.getPreferredSize().width;
        timelineController.getPropertyChangeSupport().firePropertyChange("TrackScrollPaneWidth", null, newWidth);
    }

    // EFFECTS: returns width of the scrollPanes container
    public int getContainerWidth() {
        return lineContainer.getPreferredSize().width;
    }
}
