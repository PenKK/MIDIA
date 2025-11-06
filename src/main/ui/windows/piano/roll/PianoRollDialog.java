package ui.windows.piano.roll;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import model.Block;
import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;

/**
 * Non-modal dialog hosting the piano roll editor for a single block.
 * It uses a dedicated BlockPlayer for auditioning and closes when the timeline changes.
 */
public class PianoRollDialog extends JDialog implements PropertyChangeListener {

    private final BlockPlayer blockPlayer;

    /**
     * Constructs a piano roll dialog for the given block and wires it to the timeline.
     */
    public PianoRollDialog(JFrame parent, Block block, TimelineController timelineController, MidiTrack parentMidiTrack, String title) {
        super(parent, title, false);
        blockPlayer = new BlockPlayer(block, parentMidiTrack, timelineController.getTimeline().getPlayer().getBPM());
        timelineController.addObserver(this);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        PianoRollViewPanel pianoRollViewPanel = new PianoRollViewPanel(timelineController, blockPlayer);
        add(pianoRollViewPanel, BorderLayout.CENTER);

        setSize(700, 600);
        setLocationRelativeTo(parent);
    }

    /**
     * Ensures the BlockPlayer is closed when the dialog is disposed.
     */
    @Override
    public void dispose() {
        super.dispose();
        blockPlayer.close();
    }

    /**
     * Closes the piano roll when the underlying timeline is replaced.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("timelineReplaced")) {
            dispose();
        }
    }
}
