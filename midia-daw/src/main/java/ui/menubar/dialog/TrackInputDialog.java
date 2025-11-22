package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Objects;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import model.MidiTrack;
import model.TimelineController;
import model.instrument.Instrument;
import model.instrument.TonalInstrument;
import model.instrument.PercussiveInstrument;

/**
 * Dialog for creating new tracks with optional percussive instrument selection.
 */
public class TrackInputDialog extends InputDialog {

    private JTextField nameField;
    private JCheckBox percussiveCheckBox;
    private JComboBox<Instrument> instrumentComboBox;
    private JButton create;

    /**
     * Creates a dialog for track creation, including instrument selection.
     */
    public TrackInputDialog(Component invoker, TimelineController timelineController) {
        super("Create Track", invoker, new Dimension(400, 300), timelineController);
    }

    /**
     * Initializes input fields and binds actions for track creation.
     */
    @Override
    protected void initFields() {
        nameField = new JTextField();
        percussiveCheckBox = new JCheckBox();
        instrumentComboBox = new JComboBox<>(TonalInstrument.values());
        create = new JButton("Create");

        percussiveCheckBox.addActionListener(this);
        create.addActionListener(this);

        this.add(new JLabel("Name: "));
        this.add(nameField);
        this.add(new JLabel("Percussive: "));
        this.add(percussiveCheckBox);
        this.add(new JLabel("Instrument: "));
        this.add(instrumentComboBox);
        this.add(create);
        this.getRootPane().setDefaultButton(create);
        
    }

    /**
     * Handles UI actions for instrument mode toggling and submission.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(percussiveCheckBox)) {
            updateInstrumentList((JCheckBox) e.getSource());
        } else if (e.getSource().equals(create)) {
            submit();
        }
    }

    /**
     * Creates a track on the timeline using the provided name and selected instrument.
     */
    private void submit() {
        String name = nameField.getText().trim();
        Instrument instrument = (Instrument) Objects.requireNonNull(instrumentComboBox.getSelectedItem(),
                                                            "Instrument not selected");

        if (name.isEmpty()) {
            return;
        }

        MidiTrack midiTrack = timelineController.getTimeline().createMidiTrack(name, instrument);

        if (midiTrack == null) {
            JOptionPane.showMessageDialog(this, "You have already reached the maximum number of tonal tracks," 
                                              + "15.\n Track was not created", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Updates the instrument list to tonal or percussive options based on the checkbox state.
     *
     * @param checkBox the percussive mode checkbox
     */
    private void updateInstrumentList(JCheckBox checkBox) {
        DefaultComboBoxModel<Instrument> items;
        if (checkBox.isSelected()) {
            items = new DefaultComboBoxModel<>(PercussiveInstrument.values());
        } else {
            items = new DefaultComboBoxModel<>(TonalInstrument.values());
        }
        instrumentComboBox.setModel(items);
    }


}
