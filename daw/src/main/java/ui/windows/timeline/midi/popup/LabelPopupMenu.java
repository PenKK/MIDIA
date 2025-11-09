package ui.windows.timeline.midi.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import model.MidiTrack;
import model.Timeline;
import model.TimelineController;
import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import ui.windows.timeline.midi.TrackLabelPanel;
import model.instrument.PercussiveInstrument;

/**
 * Context menu for track label actions (rename, change instrument, delete).
 */
public class LabelPopupMenu extends JPopupMenu implements ActionListener {

    private final TimelineController timelineController;
    private final TrackLabelPanel parentPanel;
    private final JMenuItem rename;
    private final JMenuItem delete;
    private final JMenuItem changeInstrument;

    /**
     * Creates a popup menu for the specified track label panel.
     */
    public LabelPopupMenu(TrackLabelPanel parentPanel, TimelineController timelineController) {
        super(parentPanel.getName());

        this.timelineController = timelineController;
        rename = new JMenuItem("Rename track");
        delete = new JMenuItem("Delete track");
        changeInstrument = new JMenuItem("Change instrument");

        rename.addActionListener(this);
        delete.addActionListener(this);
        changeInstrument.addActionListener(this);

        this.add(rename);
        this.add(changeInstrument);
        this.add(delete);

        this.setBorderPainted(true);
        this.parentPanel = parentPanel;
    }

    /**
     * Prompts for and applies a new name for the track.
     */
    private void rename() {
        String newName = JOptionPane.showInputDialog("Enter a new track name for "
                + parentPanel.getMidiTrack().getName());
        if (newName == null || newName.isEmpty()) {
            return;
        }

        parentPanel.getMidiTrack().setName(newName);
        parentPanel.getLabel().setText(newName);
    }

    /**
     * Prompts the user to choose a new instrument and applies it to the track.
     */
    private void changeInstrument() {
        MidiTrack midiTrack = parentPanel.getMidiTrack();
        Instrument[] options = midiTrack.isPercussive() ? PercussiveInstrument.values()
                : TonalInstrument.values();
        Object choice = JOptionPane.showInputDialog(this, "Select a new Instrument", midiTrack.getName(),
                JOptionPane.PLAIN_MESSAGE, null, options,
                midiTrack.getInstrument());

        if (choice == null) {
            return;
        }

        midiTrack.setInstrument((Instrument) choice);
    }

    /**
     * Deletes the track from the timeline and restarts playback if necessary.
     */
    private void delete() {
        Timeline tl = timelineController.getTimeline();

        int index = tl.getMidiTracks().indexOf(parentPanel.getMidiTrack());
        tl.removeMidiTrack(index);

        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            timelineController.playTimeline();
        }
    }

    /**
     * Routes popup menu actions to the appropriate handlers.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(rename)) {
            rename();
        } else if (source.equals(delete)) {
            delete();
        } else if (source.equals(changeInstrument)) {
            changeInstrument();
        }
    }

}
