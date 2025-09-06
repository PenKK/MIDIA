package ui.windows.piano.roll;

import java.awt.BorderLayout;

import javax.swing.*;

import model.Block;
import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;

public class PianoRollDialog extends JDialog {

    private final BlockPlayer blockPlayer;

    public PianoRollDialog(JFrame parent, Block block, TimelineController timelineController, MidiTrack parentMidiTrack, String title) {
        super(parent, title, false);
        blockPlayer = new BlockPlayer(block, parentMidiTrack, timelineController.getTimeline().getPlayer().getBPM());

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        PianoRollViewPanel pianoRollViewPanel = new PianoRollViewPanel(timelineController, blockPlayer);
        add(pianoRollViewPanel, BorderLayout.CENTER);

        setSize(700, 600);
        setLocationRelativeTo(parent);
    }

    @Override
    public void dispose() {
        super.dispose();
        blockPlayer.close();
    }
}
