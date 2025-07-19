package ui.windows.piano.roll;

import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;

import model.TimelineController;
import ui.windows.timeline.midi.LineContainerPanel;

public class PianoRollScrollPane extends JScrollPane {

    TimelineController timelineController;
    LineContainerPanel lineContainerPanel;

    public PianoRollScrollPane(TimelineController timelineController) {
        lineContainerPanel = new LineContainerPanel(timelineController);
        this.setBorder(null);
        this.setViewportView(lineContainerPanel);
        this.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.getHorizontalScrollBar().setUnitIncrement(16);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setAlignmentX(0);

        this.setForeground(Color.RED);
    }
}
