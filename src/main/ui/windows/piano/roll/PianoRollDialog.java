package ui.windows.piano.roll;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;

import model.Block;
import model.MidiTrack;
import model.TimelineController;

public class PianoRollDialog extends JDialog {

    public PianoRollDialog(JFrame parent, Block block, TimelineController timelineController, MidiTrack parentMidiTrack, String title) {
        super(parent, title, false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setResizable(true);

        PianoRollViewPanel pianoRollViewPanel = new PianoRollViewPanel(timelineController, parentMidiTrack, block);
        add(pianoRollViewPanel, BorderLayout.CENTER);

        setSize(700, 600);
        setLocationRelativeTo(parent);
    }
}
