package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import model.Block;
import model.MidiTrack;
import model.Player;
import model.TimelineController;

/**
 * Dialog for creating a new block on a selected track.
 */
public class BlockInputDialog extends InputDialog {

    private JComboBox<MidiTrack> midiTracksComboBox;
    private JSpinner startBeatSpinner;
    private JSpinner durationBeatsSpinner;
    private JButton create;

    /**
     * Creates a dialog prompting the user to select a track and beat range for a new block.
     */
    public BlockInputDialog(Component invoker, TimelineController timelineController) {
        super("Add Block", invoker, new Dimension(300, 200), timelineController);
    }

    /**
     * Initializes UI components for track selection and timing inputs.
     */
    @Override
    protected void initFields() {
        MidiTrack[] tracks = timelineController.getTimeline().getMidiTracksArray();

        midiTracksComboBox = new JComboBox<>(tracks);
        SpinnerNumberModel startBeatSpinnerModel = new SpinnerNumberModel(1.0, 1.0, Double.MAX_VALUE, 0.5);
        SpinnerNumberModel durationBeatsSpinnerModel = new SpinnerNumberModel(1.0, 0.0, Double.MAX_VALUE, 0.5);
        
        startBeatSpinner = new JSpinner(startBeatSpinnerModel);
        durationBeatsSpinner = new JSpinner(durationBeatsSpinnerModel);

        create = new JButton("Create block");
        create.addActionListener(this);

        this.add(new JLabel("Select a track: "));
        this.add(midiTracksComboBox);
        this.add(new JLabel("Start beat: "));
        this.add(startBeatSpinner);
        this.add(new JLabel("Duration (in beats): "));
        this.add(durationBeatsSpinner);
        this.getRootPane().setDefaultButton(create);
        this.add(create);
    }

    private void updateFields() {
        midiTracksComboBox.removeAllItems();
        for (MidiTrack midiTrack : timelineController.getTimeline().getMidiTracksArray()) {
            midiTracksComboBox.addItem(midiTrack);
        }
    }

    /**
     * Handles the Create button action to add a new block.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(create)) {
            createBlock();
        }
    }

    /**
     * Creates a new block in the selected track using the specified beats and duration.
     */
    private void createBlock() {
        MidiTrack midiTrack = (MidiTrack) midiTracksComboBox.getSelectedItem();
        Player p = timelineController.getTimeline().getPlayer();

        if (midiTrack == null) {
            return;
        }

        double startBeat = (double) startBeatSpinner.getValue() - 1;
        double durationBeats = (double) durationBeatsSpinner.getValue();
        long startTick = p.beatsToTicks(startBeat);
        long durationTicks = p.beatsToTicks(durationBeats);
        midiTrack.addBlock(new Block(startTick, durationTicks));
        timelineController.getPropertyChangeSupport().firePropertyChange("blockCreated", null, null);
    }

    @Override
    public void display() {
        updateFields();
        super.display();
    }
}
