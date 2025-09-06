package ui.windows.timeline.midi;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import model.MidiTrack;
import model.TimelineController;
import ui.windows.timeline.midi.popup.LabelPopupMenu;

// Interactable display of the general information of a MidiTrack
public class TrackLabelPanel extends JPanel {

    public static final int LABEL_BOX_WIDTH = 100;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.GRAY;

    public static final MatteBorder BORDER = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK);
    public static final int HEIGHT = 100;

    private final MidiTrack midiTrack;
    private final LabelPopupMenu labelPopupMenu;
    private final JLabel nameLabel;

    // EFFECTS: Creates the label panel which contains information about the MidiTrack for that row
    public TrackLabelPanel(MidiTrack midiTrack, TimelineController timelineController) {
        this.midiTrack = midiTrack;
        nameLabel = new JLabel(midiTrack.getName());
        labelPopupMenu = new LabelPopupMenu(this, timelineController);

        this.add(nameLabel);
        this.setComponentPopupMenu(labelPopupMenu);
        this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setPreferredSize(new Dimension(LABEL_BOX_WIDTH, HEIGHT));
        this.setMaximumSize(new Dimension(LABEL_BOX_WIDTH, HEIGHT));
        this.setMinimumSize(new Dimension(LABEL_BOX_WIDTH, HEIGHT));
    }

    public MidiTrack getMidiTrack() {
        return midiTrack;
    }

    public JLabel getLabel() {
        return nameLabel;
    }
 
}
