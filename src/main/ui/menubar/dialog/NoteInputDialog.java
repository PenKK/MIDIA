package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;
import model.TimelineController;

/**
 * Dialog for adding a note to a selected block on a selected track.
 */
public class NoteInputDialog extends InputDialog {

    private JButton create;
    private JSpinner pitch;
    private JSpinner velocity;
    private JSpinner startBeat;
    private JSpinner durationBeats;

    private JLabel pitchLabel;

    JComboBox<MidiTrack> midiTracksComboBox;
    JComboBox<Block> blocksComboBox;

    /**
     * Creates a dialog for entering note parameters.
     */
    public NoteInputDialog(Component invoker, TimelineController timelineController) {
        super("Add note", invoker, new Dimension(300, 400), timelineController);
    }

    /**
     * Initializes combo boxes and spinners for note parameters.
     */
    @Override
    protected void initFields() {
        initComboBoxes();

        SpinnerNumberModel doubleModelStart = new SpinnerNumberModel(1.0, 1.0, Double.MAX_VALUE, 0.5);
        SpinnerNumberModel doubleModelDuration = new SpinnerNumberModel(1.0, 0.0, Double.MAX_VALUE, 0.5);
        SpinnerNumberModel byteModelPitch = new SpinnerNumberModel(60, 0, 127, 1);
        SpinnerNumberModel byteModelVelocity = new SpinnerNumberModel(80, 0, 127, 1);

        pitch = new JSpinner(byteModelPitch);
        velocity = new JSpinner(byteModelVelocity);
        startBeat = new JSpinner(doubleModelStart);
        durationBeats = new JSpinner(doubleModelDuration);

        pitchLabel = new JLabel("Pitch: ");

        this.add(pitchLabel);
        this.add(pitch);
        this.add(new JLabel("Velocity: "));
        this.add(velocity);
        this.add(new JLabel("Start beat: "));
        this.add(startBeat);
        this.add(new JLabel("Duration (in beats): "));
        this.add(durationBeats);

        create = new JButton("Add note");
        create.addActionListener(this);
        this.getRootPane().setDefaultButton(create);
        this.add(create);
    }

    /**
     * Initializes the track and block selection combo boxes.
     */
    private void initComboBoxes() {
        Timeline timeline = timelineController.getTimeline();

        MidiTrack[] tracks = timeline.getMidiTracksArray();
        midiTracksComboBox = new JComboBox<>(tracks);

        blocksComboBox = new JComboBox<>();
        updateTrackSelectionUI();

        midiTracksComboBox.addActionListener(this);

        this.add(new JLabel("Track: "));
        this.add(midiTracksComboBox);
        this.add(new JLabel("Block: "));
        this.add(blocksComboBox);
    }

    private void updateFields() {
        midiTracksComboBox.removeAllItems();
        for (MidiTrack midiTrack : timelineController.getTimeline().getMidiTracksArray()) {
            midiTracksComboBox.addItem(midiTrack);
        }
        updateTrackSelectionUI();
    }

    /**
     * Routes UI actions to note creation or track selection changes.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(create)) {
            createNote();
        } else if (source.equals(midiTracksComboBox)) {
            updateTrackSelectionUI();
        }
    }

    /**
     * Updates the block list for the selected track and toggles pitch input for percussive tracks.
     */
    private void updateTrackSelectionUI() {
        MidiTrack midiTrack = (MidiTrack) midiTracksComboBox.getSelectedItem();

        if (midiTrack == null) {
            return;
        }
        
        boolean pitchVisible = this.isAncestorOf(pitch);
        boolean percussive = midiTrack.isPercussive();
        
        if (percussive && pitchVisible) {
            this.remove(pitch);
            this.remove(pitchLabel);
        } else if (!percussive && !pitchVisible) {
            this.add(pitch, 4);
            this.add(pitchLabel, 4);
        }

        ArrayList<Block> blockList = midiTrack.getBlocks();
        blocksComboBox.removeAllItems();

        for (Block b : blockList) {
            blocksComboBox.addItem(b);
        }
    }

    /**
     * Creates a note in the selected block from the provided parameters.
     * If the selected track is percussive, the pitch is set to a default constant.
     */
    private void createNote() {
        Block selectedBlock = (Block) blocksComboBox.getSelectedItem();

        if (selectedBlock == null) {
            return;
        }

        Timeline timeline = timelineController.getTimeline();
        int pitch = (int) this.pitch.getValue();
        int velocity = (int) this.velocity.getValue();
        long startTick = timeline.getPlayer().beatsToTicks((double) startBeat.getValue() - 1);
        long durationTicks = timeline.getPlayer().beatsToTicks((double) durationBeats.getValue());

        MidiTrack midiTrack = (MidiTrack) Objects.requireNonNull(midiTracksComboBox.getSelectedItem(),
                                                        "No track selected");

        if (midiTrack.isPercussive()) {
            pitch = Note.PERCUSSIVE_DEFAULT_PITCH;
        }

        Note note = new Note(pitch, velocity, startTick, durationTicks);
        selectedBlock.addNote(note);
        timelineController.getPropertyChangeSupport().firePropertyChange("noteCreated", null, selectedBlock);
    }

    @Override
    public void display() {
        updateFields();
        super.display();
    }

}
