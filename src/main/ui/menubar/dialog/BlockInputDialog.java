package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import model.Block;
import model.MidiTrack;
import model.Timeline;

// A JDialog to get input for creating a new block in a specified track
public class BlockInputDialog extends InputDialog {

    private JComboBox<MidiTrack> midiTracksComboBox;
    private JSpinner startBeatSpinner;
    private JButton create;

    // EFFECTS: Creates a JDialog that prompts user to select a track and start beat for a new block
    public BlockInputDialog(Component invoker) {
        super("Add Block");



        this.setBounds(new Rectangle(300, 200));
        this.getRootPane().setDefaultButton(create);
        super.display(invoker, new Rectangle(300, 200));
    }

    // MODIFIES: this
    // EFFECTS: initializes the midiTracksComboBox, spinner, and create button
    @Override
    protected void initFields() {
        Timeline timeline = Timeline.getInstance();
        ArrayList<MidiTrack> trackList = timeline.getTracks();
        MidiTrack[] tracks = new MidiTrack[trackList.size()];
        tracks = trackList.toArray(tracks);

        midiTracksComboBox = new JComboBox<>(tracks);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1);
        startBeatSpinner = new JSpinner(spinnerModel);
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(startBeatSpinner, "#.##");

        startBeatSpinner.setEditor(editor);
        create = new JButton("Create block");
        create.addActionListener(this);
        
        this.add(new JLabel("Select a track: "));
        this.add(midiTracksComboBox);
        this.add(new JLabel("Start beat: "));
        this.add(startBeatSpinner);
        this.add(create);
    }

    // EFFECTS: Listens for button actions and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(create)) {
            createBlock();
        }
    }

    // MODIFIES: this (midiTrack from timeline singleton)
    // EFFECTS: creates a new block in the selected midiTrack
    private void createBlock() {
        MidiTrack midiTrack = (MidiTrack) midiTracksComboBox.getSelectedItem();

        if (midiTrack == null) {
            return;
        }

        double startTick = (double) startBeatSpinner.getValue();
        int startBeat = Timeline.getInstance().beatsToTicks(startTick);
        midiTrack.addBlock(new Block(startBeat));

        Timeline.refresh();
        dispose();
    }
}
