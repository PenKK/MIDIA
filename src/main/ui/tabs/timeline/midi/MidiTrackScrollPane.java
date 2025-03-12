package ui.tabs.timeline.midi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import model.MidiTrack;
import model.Timeline;

// JPanel that holds the interactable view of the timeline, rendered using graphics
public class MidiTrackScrollPane extends JScrollPane implements PropertyChangeListener {

    ArrayList<MidiTrackPanel> midiTrackPanels;
    Timeline timeline;
    JPanel container;

    // EFFECTS: initializes the timeline 
    public MidiTrackScrollPane() {
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        this.setBorder(null);
        this.setViewportView(container);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setAlignmentX(0);

        midiTrackPanels = new ArrayList<>();
        Timeline.addObserver(this);

        updateMidiTrackPanels();
    }

    // EFFECTS: clears all MidiTrackPanels and then populates according to current Timeline
    private void updateMidiTrackPanels() {
        Timeline timeline = Timeline.getInstance();

        if (timeline == null) {
            System.err.println("Timeline instance returned null, unable to update rows");
            return;
        }

        clearPanels();
        for (MidiTrack track : timeline.getTracks()) {
            MidiTrackPanel currentPanel = new MidiTrackPanel(track);
            midiTrackPanels.add(currentPanel);
            container.add(currentPanel);
        }

        revalidate();
        repaint();
    }

    // MODFIES: this
    // EFFECTS: Removes MidiTrackPanels from the container
    private void clearPanels() {
        for (MidiTrackPanel currentMidiTrackPanel : midiTrackPanels) {
            container.remove(currentMidiTrackPanel);
        }
    }

    // MODFIES: this
    // EFFECTS: listens for proper change events and executes methods accordingly
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
