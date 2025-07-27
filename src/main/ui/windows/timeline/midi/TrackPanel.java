package ui.windows.timeline.midi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import model.MidiTrack;
import model.TimelineController;
import model.editing.DawClipboard;

// Represents a single MidiTrack in the TimelinePanel UI.
// Contains two sub Panels, MidiTrackLabelPanel and MidiTrackRenderPanel
public class TrackPanel extends JPanel {

    public static final MatteBorder BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
    public static final int HEIGHT = 100;
    public static final int MINIMUM_WIDTH = 500;

    private final TrackLabelPanel labelPanel;
    private final TrackRenderPanel renderPanel;

    // Creates sub panels, adjusts width according to render panel length, and then adds it to this
    public TrackPanel(MidiTrack midiTrack, TimelineController timelineController, DawClipboard dawClipboard) {
        labelPanel = new TrackLabelPanel(midiTrack, timelineController);
        renderPanel = new TrackRenderPanel(midiTrack, timelineController, dawClipboard);

        this.setBorder(BORDER);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);

        int panelWidth = renderPanel.getScaledWidth() + MINIMUM_WIDTH;

        this.setMinimumSize(new Dimension(panelWidth, HEIGHT));
        this.setPreferredSize(new Dimension(panelWidth, HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));

        this.add(labelPanel);
        this.add(renderPanel);
    }

}
