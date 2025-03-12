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

    // Creates sub panels, adjusts width according to render panel length, and then adds it to this
    public MidiTrackPanel(MidiTrack midiTrack) {
        labelPanel = new MidiTrackLabelPanel(midiTrack);
        renderPanel = new MidiTrackRenderPanel(midiTrack);

        this.setBorder(BORDER);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.setMinimumSize(new Dimension(renderPanel.getEndPixel(), HEIGHT));
        System.out.println(renderPanel.getEndPixel());
        this.setPreferredSize(new Dimension(renderPanel.getEndPixel(), HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));

        this.add(labelPanel);
        this.add(renderPanel);
    }

}
