package ui.windows.timeline.midi;

import model.MidiTrack;
import model.TimelineController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    private void updateTrackLabelPanels() {
        removeAll();
        for (MidiTrack track : timelineController.getTimeline().getMidiTracks()) {
            this.add(new TrackLabelPanel(track, timelineController));
        }

        revalidate();
        repaint();
    }

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
