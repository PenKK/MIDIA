package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import model.MidiTrack;
import model.Timeline;
import model.instrument.Instrument;
import model.instrument.InstrumentalInstrument;
import model.instrument.PercussionInstrument;

// An InputDialog for creating new MidiTracks.
public class TrackInputDialog extends JDialog implements ActionListener {

    private JTextField nameField;
    private JCheckBox percussiveCheckBox;
    private JComboBox<Instrument> instrumentComboBox;
    private JButton create;

    // EFFECTS: creates input dialog for creating a new track
    public TrackInputDialog(Component invoker) {
        super((Frame) null, "Create Track", true);
        this.setLayout(new GridLayout(0, 2, 10, 10));
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        nameField = new JTextField();
        percussiveCheckBox = new JCheckBox();
        instrumentComboBox = new JComboBox<>(InstrumentalInstrument.values());
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
        this.setBounds(new Rectangle(400, 300));
        this.setLocationRelativeTo(invoker);
        this.setVisible(true);
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
        String name = nameField.getText().trim();;
        boolean percussive = percussiveCheckBox.isSelected();;
        Instrument instrument = (Instrument) instrumentComboBox.getSelectedItem();;

        if (name.equals("") || name == null) {
            return;
        }

        MidiTrack midiTrack = Timeline.getInstance().createMidiTrack(name, instrument, percussive);

        if (midiTrack == null) {
            JOptionPane.showMessageDialog(this, "You have already reached the maximum number of instrumental tracks," 
                                              + "15.\n Track was not created", "Warning", JOptionPane.WARNING_MESSAGE);
        }
        dispose();
    }

    // MODIFIES: this
    // EFFECTS: updates the instrumentComboBox list of instrument options accoring to checkBox for percussion
    private void updateInstrumentList(JCheckBox checkBox) {
        if (checkBox.isSelected()) {
            DefaultComboBoxModel<Instrument> items = 
                    new DefaultComboBoxModel<Instrument>(PercussionInstrument.values());
            instrumentComboBox.setModel(items);
        } else {
            DefaultComboBoxModel<Instrument> items = 
                    new DefaultComboBoxModel<Instrument>(InstrumentalInstrument.values());
            instrumentComboBox.setModel(items);
        }
    }
}
