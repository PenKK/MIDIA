package ui.tabs.timeline;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.MidiTrack;

// Interactable display of the general information of a MidiTrack
public class MidiTrackLabelPanel extends JPanel {
    MidiTrack midiTrack;

    public MidiTrackLabelPanel(MidiTrack midiTrack) {
        this.midiTrack = midiTrack;
        this.add(new JLabel(midiTrack.getName()));
    }

    
}
