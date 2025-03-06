package ui.tabs.timeline;

import javax.swing.JPanel;

import model.MidiTrack;

// Represents a single MidiTrack in the TinelinePanel UI.
// Contains two sub Panels, MidiTrackLabelPanel and MidiTrackRenderPanel
public class MidiTrackPanel extends JPanel {

    MidiTrackLabelPanel labelPanel;
    MidiTrackRenderPanel renderPanel;

    public MidiTrackPanel(MidiTrack midiTrack) {
        labelPanel = new MidiTrackLabelPanel(midiTrack);
        renderPanel = new MidiTrackRenderPanel(midiTrack);
        this.add(labelPanel);
        this.add(renderPanel);
    }
}
