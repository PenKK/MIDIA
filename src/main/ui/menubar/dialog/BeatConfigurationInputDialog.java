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

/**
 * Dialog for configuring the ruler beat division and beats per measure settings.
 */
public class BeatConfigurationInputDialog extends InputDialog implements PropertyChangeListener {

    private JButton save;
    private JSpinner beatDivision;
    private JSpinner beatsPerMeasure;

    /**
     * Creates the beat configuration dialog and subscribes to timeline updates.
     */
    public BeatConfigurationInputDialog(Component invoker, TimelineController timelineController) {
        super("Beat Configuration", invoker, new Dimension(300, 200), timelineController);
        timelineController.addObserver(this);
    }

    /**
     * Initializes the input fields for beat division and beats per measure.
     */
    @Override
    protected void initFields() {
        Timeline timeline = timelineController.getTimeline();
        SpinnerModel nonZeroBeatDivision = new SpinnerNumberModel(timeline.getPlayer().getBeatDivision(), 1, 128, 1);
        SpinnerModel nonZeroBPM = new SpinnerNumberModel(timeline.getPlayer().getBeatsPerMeasure(), 1, 128, 1);

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
    
    /**
     * Applies the selected beat configuration to the timeline.
     */
    private void save() {
        Timeline timeline = timelineController.getTimeline();
        
        timeline.getPlayer().setBeatDivision((int) beatDivision.getValue());
        timeline.getPlayer().setBeatsPerMeasure((int) beatsPerMeasure.getValue());
    }

    private void updateValues() {
        Timeline timeline = timelineController.getTimeline();
        beatDivision.setValue(timeline.getPlayer().getBeatDivision());
        beatsPerMeasure.setValue(timeline.getPlayer().getBeatsPerMeasure());
    }

    /**
     * Handles the Save button action to commit beat configuration changes.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(save)) {
            save();
        }
    }

    /**
     * Refreshes displayed values when the timeline instance is replaced.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();

        if (property.equals("timelineReplaced")) {
            updateValues();
        }
    }

}
