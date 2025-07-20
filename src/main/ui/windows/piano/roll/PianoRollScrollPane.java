package ui.windows.piano.roll;

import java.awt.Color;


import model.TimelineController;
import ui.ruler.BlankScrollPane;
import ui.windows.timeline.midi.LineContainerPanel;

public class PianoRollScrollPane extends BlankScrollPane {

    TimelineController timelineController;
    LineContainerPanel lineContainerPanel;

    public PianoRollScrollPane(TimelineController timelineController) {
        lineContainerPanel = new LineContainerPanel(timelineController);
        this.setViewportView(lineContainerPanel);

        this.setForeground(Color.RED);
    }
}
