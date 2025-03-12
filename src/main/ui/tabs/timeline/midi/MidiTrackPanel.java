package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import model.MidiTrack;

// Represents a single MidiTrack in the TinelinePanel UI.
// Contains two sub Panels, MidiTrackLabelPanel and MidiTrackRenderPanel
public class MidiTrackPanel extends JPanel {

    public static final MatteBorder BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
    public static final int HEIGHT = 100;

    MidiTrackLabelPanel labelPanel;
    MidiTrackRenderPanel renderPanel;

    public MidiTrackPanel(MidiTrack midiTrack) {
        this.setBorder(BORDER);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.setPreferredSize(new Dimension(750, HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));

        labelPanel = new MidiTrackLabelPanel(midiTrack);
        renderPanel = new MidiTrackRenderPanel(midiTrack);

        this.add(labelPanel);
        this.add(renderPanel);
    }
}
