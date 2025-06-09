package ui.tabs.timeline.midi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import model.MidiTrack;
import model.TimelineController;

// JPanel that holds the interactable view of the timeline, rendered using graphics
public class TrackScrollPane extends JScrollPane implements PropertyChangeListener {

    private TimelineController timelineController;
    private ArrayList<TrackPanel> midiTrackPanels;
    private LineContainerPanel lineContainer;

    // EFFECTS: initializes the timeline 
    public TrackScrollPane(TimelineController timelineController) {
        this.timelineController = timelineController;
        lineContainer = new LineContainerPanel(timelineController);

        this.setBorder(null);
        this.setViewportView(lineContainer);
        this.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.getHorizontalScrollBar().setUnitIncrement(16);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setAlignmentX(0);

        midiTrackPanels = new ArrayList<>();
        timelineController.addObserver(this);

        updateMidiTrackPanels();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void updateMidiTrackPanels() {
        clearTrackPanels();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            TrackPanel currentPanel = new TrackPanel(track, timelineController);
            midiTrackPanels.add(currentPanel);

            lineContainer.add(currentPanel);
        }

        revalidate();
        repaint();
    }

    // MODFIES: this
    // EFFECTS: Removes MidiTrackPanels from the container
    private void clearTrackPanels() {
        for (TrackPanel currentMidiTrackPanel : midiTrackPanels) {
            lineContainer.remove(currentMidiTrackPanel);
        }
    }

    // MODFIES: this
    // EFFECTS: listens for proper change events and executes methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timelineReplaced":
            case "midiTracks":
            case "horizontalScaleFactor":
                updateMidiTrackPanels();
                break;
            default:
                break;
        }
    }

    // EFFECTS: returns width of the scrollPanes container
    public int getContainerWidth() {
        updateMidiTrackPanels(); // forces width update according to render scale, better solution one day maybe
        return lineContainer.getPreferredSize().width;
    }
}
