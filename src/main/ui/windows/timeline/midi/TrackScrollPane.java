package ui.windows.timeline.midi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;

import model.MidiTrack;
import model.TimelineController;
import model.editing.DawClipboard;
import ui.ruler.BlankScrollPane;

// JPanel that holds the interactable view of the timeline, rendered using graphics
public class TrackScrollPane extends BlankScrollPane implements PropertyChangeListener {

    private final TimelineController timelineController;
    private final LineContainerPanel lineContainer;
    private final DawClipboard dawClipboard;

    // EFFECTS: initializes the timeline 
    public TrackScrollPane(TimelineController timelineController, DawClipboard dawClipboard) {
        this.timelineController = timelineController;
        this.dawClipboard = dawClipboard;
        lineContainer = new LineContainerPanel(timelineController, timelineController.getTimeline().getPlayer());

        this.setViewportView(lineContainer);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        timelineController.addObserver(this);

        updateTrackRenderPanels();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void updateTrackRenderPanels() {
        lineContainer.removeAll();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            TrackRenderPanel currentPanel = new TrackRenderPanel(track, timelineController, dawClipboard);
            lineContainer.add(currentPanel);
        }

        repaint();
    }

    // MODIFIES: this
    // EFFECTS: listens for proper change events and executes methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timelineReplaced":
            case "midiTracks":
            case "horizontalScaleFactor":
                updateTrackRenderPanels();
                break;
            default:
                break;
        }
    }

    // EFFECTS: returns width of the scrollPanes container
    public int getContainerWidth() {
        return lineContainer.getPreferredSize().width;
    }
}
