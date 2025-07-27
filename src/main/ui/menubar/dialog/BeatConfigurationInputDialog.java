package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import model.Timeline;
import model.TimelineController;

// A JInputDialog for receiving integer inputs for ruler beats per measure and beat division
public class BeatConfigurationInputDialog extends InputDialog implements PropertyChangeListener {

    private JButton save;
    private JSpinner beatDivision;
    private JSpinner beatsPerMeasure;

    // EFFECTS: creates an input dialog and displays it
    public BeatConfigurationInputDialog(Component invoker, TimelineController timelineController) {
        super("Beat Configuration", invoker, new Dimension(300, 200), timelineController);
        timelineController.addObserver(this);
    }

    // MODIFIES: this
    // EFFECTS: initializes input fields
    @Override
    protected void initFields() {
        Timeline timeline = timelineController.getTimeline();
        SpinnerModel nonZeroBeatDivision = new SpinnerNumberModel(timeline.getBeatDivision(), 1, 128, 1);
        SpinnerModel nonZeroBPM = new SpinnerNumberModel(timeline.getBeatsPerMeasure(), 1, 128, 1);

        beatDivision = new JSpinner(nonZeroBeatDivision);
        beatsPerMeasure = new JSpinner(nonZeroBPM);
        save = new JButton("Save");

        save.addActionListener(this);

        this.add(new JLabel("Beat division: "));
        this.add(beatDivision);
        this.add(new JLabel("Beats per measure: "));
        this.add(beatsPerMeasure);
        this.add(save);
    }
    
    // EFFECTS: updates the timeline ruler settings with the new beat configuration
    private void save() {
        Timeline timeline = timelineController.getTimeline();
        
        timeline.setBeatDivision((int) beatDivision.getValue());
        timeline.setBeatsPerMeasure((int) beatsPerMeasure.getValue());
    }

    private void updateValues() {
        Timeline timeline = timelineController.getTimeline();
        beatDivision.setValue(timeline.getBeatDivision());
        beatsPerMeasure.setValue(timeline.getBeatsPerMeasure());
    }

    // EFFECTS: listens for button clicks and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(save)) {
            save();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();

        if (property.equals("timelineReplaced")) {
            updateValues();
        }
    }

}
