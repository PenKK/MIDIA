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

// The popup menu for modifying a track
public class LabelPopupMenu extends JPopupMenu implements ActionListener {

    private TimelineController timelineController;
    private TrackLabelPanel parentPanel;
    private JMenuItem rename;
    private JMenuItem delete;
    private JMenuItem changeInstrument;

    // EFFECTS: creates a popup menu for the specified parent midiTrackLabelPanel
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

    // MODIFIES: this, parentPanel
    // EFFECTS: prompts user for a new label for the invoking track
    private void rename() {
        String newName = JOptionPane.showInputDialog("Enter a new track name for "
                + parentPanel.getMidiTrack().getName());
        if (newName == null || newName.equals("")) {
            return;
        }

        parentPanel.getMidiTrack().setName(newName);
        parentPanel.getLabel().setText(newName);
    }

    // MODIFIES: this, parentPanel
    // EFFECTS: prompts user to choose a new instrument for the invoking track
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

    // MODIFIES: Timeline singleton
    // EFFECTS: deletes this from the the miditrack list
    private void delete() {
        Timeline tl = timelineController.getTimeline();

        int index = tl.getMidiTracks().indexOf(parentPanel.getMidiTrack());
        tl.removeMidiTrack(index);

        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            timelineController.playTimeline();
        }
    }

    // EFFECTS: listens for actions on the popup menu items and runs methods accordingly
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
