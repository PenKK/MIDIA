package ui.tabs.timeline;

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
        timeline = Timeline.getInstance();
        midiTrackPanels = new ArrayList<>();
        Timeline.addObserver(this);

        if (timeline == null) {
            System.err.println("Timeline instance returned null, MIDI device may be unavliable");
            return;
        }

        populateRows();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void populateRows() {
        midiTrackPanels.clear();
        for (MidiTrack track : timeline.getTracks()) {
            midiTrackPanels.add(new MidiTrackPanel(track));
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timeline")) {
            timeline = Timeline.getInstance();
            populateRows();
        }
    }
}
