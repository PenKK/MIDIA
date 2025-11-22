package ui.windows.timeline.midi;

import model.MidiTrack;
import model.TimelineController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Vertical list of track label panels, kept in sync with the timeline's tracks.
 */
public class TrackLabelContainer extends JPanel implements PropertyChangeListener {

    private final TimelineController timelineController;

    public TrackLabelContainer(TimelineController timelineController) {
        this.timelineController = timelineController;
        timelineController.addObserver(this);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(0, 0, 0)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

//        setPreferredSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, 0));
        setMaximumSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH,Integer.MAX_VALUE));
    }

    /**
     * Rebuilds the list of labels to match the current tracks.
     */
    private void updateTrackLabelPanels() {
        removeAll();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            this.add(new TrackLabelPanel(track, timelineController));
        }

        revalidate();
        repaint();
    }

    /**
     * Updates the label list when tracks or the timeline change.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timelineReplaced":
            case "midiTracks":
            case "horizontalScaleFactor":
                updateTrackLabelPanels();
                break;
        }
    }
}
