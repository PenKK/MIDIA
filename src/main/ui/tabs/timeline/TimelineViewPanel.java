package ui.tabs.timeline;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import model.Timeline;
import ui.tabs.timeline.midi.MidiTrackPanel;
import ui.tabs.timeline.midi.MidiTrackScrollPane;
import ui.tabs.timeline.ruler.RulerScrollPane;

// Holds the timeline view, and a ruler at the top
public class TimelineViewPanel extends JPanel implements PropertyChangeListener, AdjustmentListener {

    MidiTrackScrollPane midiTrackScrollPane;
    RulerScrollPane ruler;

    // EFFECTS: Creates timeline view container, and initializes sub components
    public TimelineViewPanel() {
        midiTrackScrollPane = new MidiTrackScrollPane();
        ruler = new RulerScrollPane();

        Timeline.addObserver(this);

        syncHorizontalScrollBars();

        this.setName("Timeline");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(MidiTrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(ruler);
        this.add(midiTrackScrollPane);
    }


    private void syncHorizontalScrollBars() {
        JScrollBar tracksBar = midiTrackScrollPane.getHorizontalScrollBar();
        tracksBar.addAdjustmentListener(this);
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        
        switch (propertyName) {
            case "timeline":
                ruler.updateLength(midiTrackScrollPane.getWidth());
                break;
            default:
                break;
        }
    }


    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        JScrollBar rulerBar = ruler.getHorizontalScrollBar();
        rulerBar.setValue(e.getValue());

        // RulerCanvas canvas = ruler.getCanvas();

        // SwingUtilities.invokeLater(() -> {
        //     canvas.getParent().revalidate();
        //     canvas.getParent().repaint();
        // });
    }


}
