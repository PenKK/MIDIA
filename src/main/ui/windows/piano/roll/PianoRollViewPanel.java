package ui.windows.piano.roll;


import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.TimelineController;
import ui.ruler.RulerScrollPane;
import ui.windows.timeline.midi.TrackPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    RulerScrollPane rulerScrollPane;
    PianoRollScrollPane pianoRollScrollPane;

    public PianoRollViewPanel(TimelineController timelineController) {
        rulerScrollPane = new RulerScrollPane(timelineController);
        pianoRollScrollPane = new PianoRollScrollPane(timelineController);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(rulerScrollPane);
        this.add(pianoRollScrollPane);
    }
}
