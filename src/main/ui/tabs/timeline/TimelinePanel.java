package ui.tabs.timeline;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;


import javax.swing.JPanel;

import model.MidiTrack;
import model.Timeline;

// JPanel that holds the interactable view of the timeline, rendered using graphics
public class TimelinePanel extends JPanel implements PropertyChangeListener {

    ArrayList<MidiTrackPanel> midiTrackPanels;
    Timeline timeline;

    // EFFECTS: initializes the timeline 
    public TimelinePanel() {
        this.setName("Timeline");
        this.setLayout(new GridLayout(4, 1));

        midiTrackPanels = new ArrayList<>();
        Timeline.addObserver(this);
        updateMidiTrackPanels();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void updateMidiTrackPanels() {
        Timeline timeline = Timeline.getInstance();

        if (timeline == null) {
            System.err.println("Timeline instance returned null, unable to populate rows");
            return;
        }

        clearPanels();
        for (MidiTrack track : timeline.getTracks()) {
            MidiTrackPanel currentPanel = new MidiTrackPanel(track);
            midiTrackPanels.add(currentPanel);
            this.add(currentPanel);
        }

        revalidate();
        repaint();
    }

    private void clearPanels() {
        for (MidiTrackPanel currentMidiTrackPanel : midiTrackPanels) {
            this.remove(currentMidiTrackPanel);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timeline":
                updateMidiTrackPanels();
                break;
            case "midiTracks":
                updateMidiTrackPanels();
                break;
            default:
                break;
        }
    }
}
