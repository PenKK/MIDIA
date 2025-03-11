package ui.tabs.timeline;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ui.tabs.timeline.midi.MidiTrackPanel;
import ui.tabs.timeline.ruler.RulerScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements AdjustmentListener, ComponentListener {

    TimelineScrollPane timelineScrollPane;
    RulerScrollPane ruler;

    // EFFECTS: Creates timeline view container, and initializes sub components
    public TimelineViewPanel() {
        timelineScrollPane = new TimelineScrollPane();
        ruler = new RulerScrollPane();

        timelineScrollPane.addComponentListener(this);
        timelineScrollPane.getHorizontalScrollBar().addAdjustmentListener(this);

        this.setName("Timeline");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(MidiTrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(ruler);
        this.add(timelineScrollPane);
    }

    // TODO
    // private void syncHorizontalBar() {
    //     JScrollBar timelineHBar = timelineScrollPane.getHorizontalScrollBar();
    //     JScrollBar rulerHBar = timelineScrollPane.getHorizontalScrollBar();
    //     rulerHBar.setModel(timelineHBar.getModel());
    // }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        // ruler.getHorizontalScrollBar().setValue(e.getValue());
    }

    @Override
    public void componentResized(ComponentEvent e) {
        ruler.setSize(e.getComponent().getWidth(), ruler.getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
