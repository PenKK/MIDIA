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
import model.Note;
import model.Timeline;

// A JDialog for getting note input into a specified block in a specified track
public class NoteInputDialog extends InputDialog {

    private JButton create;
    private JSpinner pitch;
    private JSpinner velocity;
    private JSpinner startBeat;
    private JSpinner durationBeats;

    JComboBox<MidiTrack> midiTracksComboBox;
    JComboBox<Block> blocksComboBox;

    // Creates and launches an input dialog for note information 
    public NoteInputDialog(Component invoker) {
        super("Add note");

        create = new JButton("Add note");
        create.addActionListener(this);
        this.add(create);

        this.getRootPane().setDefaultButton(create);
        super.display(invoker, new Rectangle(300, 400));
    }

    // MODIFIES: this
    // EFFECTS: initializes combo boxes and spinner fields and adds them to this
    @Override
    protected void initFields() {
        initComboBoxes();

        SpinnerNumberModel doubleModelStart = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1);
        SpinnerNumberModel doubleModelDuration = new SpinnerNumberModel(0.0, 0.0, Double.MAX_VALUE, 0.1);
        SpinnerNumberModel byteModelPitch = new SpinnerNumberModel(0, 0, 127, 1);
        SpinnerNumberModel byteModelVelocity = new SpinnerNumberModel(0, 0, 127, 1);

        pitch = new JSpinner(byteModelPitch);
        velocity = new JSpinner(byteModelVelocity);
        startBeat = new JSpinner(doubleModelStart);
        durationBeats = new JSpinner(doubleModelDuration);

        this.add(new JLabel("Pitch: "));
        this.add(pitch);
        this.add(new JLabel("Velocity: "));
        this.add(velocity);
        this.add(new JLabel("Start beat: "));
        this.add(startBeat);
        this.add(new JLabel("Duration (in beats): "));
        this.add(durationBeats);
    }

    // MODIFIES: this
    // EFFECTS: Initializes ComboBox fields
    private void initComboBoxes() {
        Timeline timeline = Timeline.getInstance();

        ArrayList<MidiTrack> trackList = timeline.getTracks();
        MidiTrack[] tracks = new MidiTrack[trackList.size()];
        tracks = trackList.toArray(tracks);
        midiTracksComboBox = new JComboBox<>(tracks);

        blocksComboBox = new JComboBox<>();
        updateBlocks();

        midiTracksComboBox.addActionListener(this);

        this.add(new JLabel("Track: "));
        this.add(midiTracksComboBox);
        this.add(new JLabel("Block index: "));
        this.add(blocksComboBox);
    }

    // EFFECTS: listens for actions and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(create)) {
            createNote();
        } else if (source.equals(midiTracksComboBox)) {
            updateBlocks();
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the blocksComboBox list with the currently selected midiTrack
    private void updateBlocks() {
        MidiTrack midiTrack = (MidiTrack) midiTracksComboBox.getSelectedItem();

        if (midiTrack == null) {
            return;
        }

        ArrayList<Block> blockList = midiTrack.getBlocks();
        blocksComboBox.removeAllItems();

        for (Block b : blockList) {
            blocksComboBox.addItem(b);
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a note in the specified block in a track with the avaliable field information
    //          and closes the dialog
    private void createNote() {
        Block selectedBlock = (Block) blocksComboBox.getSelectedItem();

        if (selectedBlock == null) {
            return;
        }

        Timeline timeline = Timeline.getInstance();
        int p = (int) pitch.getValue();
        int v = (int) velocity.getValue();
        int startTick = timeline.beatsToTicks((double) startBeat.getValue());
        int durationTicks = timeline.beatsToTicks((double) durationBeats.getValue());

        Note note = new Note(p, v, startTick, durationTicks);
        selectedBlock.addNote(note);
        Timeline.refresh();
        dispose();
    }

}
