package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import model.Block;
import model.MidiTrack;
import model.Timeline;
import model.instrument.Instrument;

// A JDialog to get input for creating a new block in a specified track
public class BlockInputDialog extends JDialog implements ActionListener {

    private JComboBox<MidiTrack> midiTracksComboBox;
    private JSpinner startBeatSpinner;
    private JButton create;

    public BlockInputDialog(Component invoker) {
        super((Frame) null, "Add block", true);
        this.setLayout(new GridLayout(0, 2, 10, 10));
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        Timeline timeline = Timeline.getInstance();
        ArrayList<MidiTrack> trackList = timeline.getTracks();
        MidiTrack[] tracks = new MidiTrack[trackList.size()];
        tracks = trackList.toArray(tracks);

        midiTracksComboBox = new JComboBox<>(tracks);
        startBeatSpinner = new JSpinner();
        create = new JButton("Create block");
        create.addActionListener(this);
        
        this.add(new JLabel("Select a track: "));
        this.add(midiTracksComboBox);
        this.add(new JLabel("Start beat: "));
        this.add(startBeatSpinner);
        this.add(create);

        this.setBounds(new Rectangle(300, 200));
        this.getRootPane().setDefaultButton(create);
        this.setLocationRelativeTo(invoker);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(create)) {
            createBlock();
        }
    }

    private void createBlock() {
        MidiTrack midiTrack = (MidiTrack) midiTracksComboBox.getSelectedItem();
        int startTick = (Integer) startBeatSpinner.getValue();
        int startBeat = Timeline.getInstance().beatsToTicks(startTick);
        midiTrack.addBlock(new Block(startBeat));
    }
}
