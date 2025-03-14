package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.MidiTrack;

// Interactable display of the general information of a MidiTrack
public class MidiTrackLabelPanel extends JPanel {
    
    public final static int LABEL_BOX_WIDTH = 100;

    private MidiTrack midiTrack;

    // EFFECTS: Creates the label panel which contains information about the MidiTrack for that row
    public MidiTrackLabelPanel(MidiTrack midiTrack) {
        this.midiTrack = midiTrack;
        this.add(new JLabel(midiTrack.getName()));
        this.setBackground(Color.GRAY);
        
        this.setPreferredSize(new Dimension(LABEL_BOX_WIDTH, MidiTrackPanel.HEIGHT));
        this.setMaximumSize(new Dimension(LABEL_BOX_WIDTH, MidiTrackPanel.HEIGHT));
        this.setMinimumSize(new Dimension(LABEL_BOX_WIDTH, MidiTrackPanel.HEIGHT));
    }

}
