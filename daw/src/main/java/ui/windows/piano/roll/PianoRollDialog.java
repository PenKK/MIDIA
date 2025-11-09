package ui.windows.piano.roll;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import model.Block;
import model.PianoRollPlayer;
import model.MidiTrack;
import model.TimelineController;

/**
 * Non-modal dialog hosting the piano roll editor for a single block.
 * It uses a dedicated BlockPlayer for auditioning and closes when the timeline changes.
 */
public class PianoRollDialog extends JDialog implements PropertyChangeListener {

    private final PianoRollPlayer pianoRollPlayer;

    /**
     * Constructs a piano roll dialog for the given block and wires it to the timeline.
     */
    public PianoRollDialog(JFrame parent, Block block, TimelineController timelineController, MidiTrack parentMidiTrack, String title) {
        super(parent, title, false);
        pianoRollPlayer = new PianoRollPlayer(block, parentMidiTrack, timelineController.getTimeline().getPlayer().getBPM());
        timelineController.addObserver(this);

        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
        getRootPane().setWindowDecorationStyle(JRootPane.FRAME);

        PianoRollViewPanel pianoRollViewPanel = new PianoRollViewPanel(timelineController, pianoRollPlayer);
        add(pianoRollViewPanel, BorderLayout.CENTER);

        setSize(700, 600);
        setLocationRelativeTo(parent);
    }

    /**
     * Ensures the BlockPlayer is closed when the dialog is disposed of.
     */
    @Override
    public void dispose() {
        super.dispose();
        pianoRollPlayer.close();
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
