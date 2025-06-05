package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

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

// An InputDialog for creating new MidiTracks.
public class TrackInputDialog extends InputDialog {

    private JTextField nameField;
    private JCheckBox percussiveCheckBox;
    private JComboBox<Instrument> instrumentComboBox;
    private JButton create;

    // EFFECTS: creates input dialog for creating a new track
    public TrackInputDialog(Component invoker, TimelineController timelineController) {
        super("Create Track", invoker, new Dimension(400, 300), timelineController);
    }

    // MODIFIES: this
    // EFFECTS: initializes fields and adds them to this
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

    // EFFECTS: listens for actions and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(percussiveCheckBox)) {
            updateInstrumentList((JCheckBox) e.getSource());
        } else if (e.getSource().equals(create)) {
            submit();
        }
    }

    // MODIFIES: this
    // EFFECTS: creates a track on the the timeline with user input from fields
    private void submit() {
        String name = nameField.getText().trim();
        Instrument instrument = (Instrument) instrumentComboBox.getSelectedItem();

        if (name.equals("") || name == null) {
            return;
        }

        MidiTrack midiTrack = timelineController.getTimeline().createMidiTrack(name, instrument);

        if (midiTrack == null) {
            JOptionPane.showMessageDialog(this, "You have already reached the maximum number of tonal tracks," 
                                              + "15.\n Track was not created", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    // MODIFIES: this
    // EFFECTS: updates the instrumentComboBox list of instrument options accoring to checkBox for percussion
    private void updateInstrumentList(JCheckBox checkBox) {
        if (checkBox.isSelected()) {
            DefaultComboBoxModel<Instrument> items = 
                    new DefaultComboBoxModel<Instrument>(PercussiveInstrument.values());
            instrumentComboBox.setModel(items);
        } else {
            DefaultComboBoxModel<Instrument> items = 
                    new DefaultComboBoxModel<Instrument>(TonalInstrument.values());
            instrumentComboBox.setModel(items);
        }
    }


}
