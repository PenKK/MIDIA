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
import javax.swing.JTextField;

import model.Instrument;
import model.InstrumentalInstrument;
import model.PercussionInstrument;

// An InputDialog for creating new MidiTracks.
public class TrackInputDialog extends JDialog implements ActionListener {

    private JTextField nameField;
    private JCheckBox percussiveCheckBox;
    private JComboBox<Instrument> instrumentComboBox;
    private JButton create;

    private String name;
    private boolean percussive;
    private Instrument instrument;

    // EFFECTS: creates input dialog for creating a new track
    public TrackInputDialog(Component invoker, String title) {
        super((Frame) null, title, true);
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

        this.setBounds(new Rectangle(400, 300));
        this.setLocationRelativeTo(invoker);
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
    // EFFECTS: stores values in the fields and then closes the dialog
    private void submit() { 
        name = nameField.getText().trim();

        if (name.equals("")) {
            return;
        }

        percussive = percussiveCheckBox.isSelected();
        instrument = (Instrument) instrumentComboBox.getSelectedItem();
        dispose();
    }

    // MODIFIES: this
    // EFFECTS: updates the instrumentComboBox list of instrument options accoring to checkBox for percussion
    private void updateInstrumentList(JCheckBox checkBox) {
        if (checkBox.isSelected()) {
            DefaultComboBoxModel<Instrument> items = new DefaultComboBoxModel<Instrument>(PercussionInstrument.values());
            instrumentComboBox.setModel(items);
        } else {
            DefaultComboBoxModel<Instrument> items = new DefaultComboBoxModel<Instrument>(InstrumentalInstrument.values());
            instrumentComboBox.setModel(items);
        }
    }

    public String getInputName() {
        return name;
    }

    public boolean isPercussive() {
        return percussive;
    }

    public Instrument getInstrument() {
        return instrument;
    }
}
