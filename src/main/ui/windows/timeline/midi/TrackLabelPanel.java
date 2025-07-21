package ui.windows.timeline.midi;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.MidiTrack;
import model.TimelineController;
import ui.windows.timeline.midi.popup.LabelPopupMenu;

// Interactable display of the general information of a MidiTrack
public class TrackLabelPanel extends JPanel {

    public static final int LABEL_BOX_WIDTH = 100;
    private static final Color DEFAULT_BACKGROUND_COLOR = Color.GRAY;

    private MidiTrack midiTrack;
    private LabelPopupMenu labelPopupMenu;
    private JLabel nameLabel;

    // EFFECTS: Creates the label panel which contains information about the MidiTrack for that row
    public TrackLabelPanel(MidiTrack midiTrack, TimelineController timelineController) {
        nameLabel = new JLabel(midiTrack.getName());
        labelPopupMenu = new LabelPopupMenu(this, timelineController);

        this.midiTrack = midiTrack;
        this.setComponentPopupMenu(labelPopupMenu);

        this.add(nameLabel);

        this.setBackground(DEFAULT_BACKGROUND_COLOR);
        this.setPreferredSize(new Dimension(LABEL_BOX_WIDTH, TrackPanel.HEIGHT));
        this.setMaximumSize(new Dimension(LABEL_BOX_WIDTH, TrackPanel.HEIGHT));
    }

    public MidiTrack getMidiTrack() {
        return midiTrack;
    }

    public JLabel getLabel() {
        return nameLabel;
    }
 
}
