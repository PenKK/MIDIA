package ui.tabs.timeline.ruler;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import model.Timeline;
import ui.menubar.Menu;
import ui.tabs.timeline.midi.MidiTrackLabelPanel;
import ui.tabs.timeline.midi.MidiTrackPanel;

// Panel that shows the tickmarks above timeline to indicate beat marks and other timely infomration
public class RulerScrollPane extends JScrollPane implements PropertyChangeListener {

    public static final int BEAT_WIDTH = 25;
    public static final int RULER_HEIGHT = MidiTrackPanel.HEIGHT / 4;
    public static final int DEFAULT_RULER_HEIGHT = 800;

    RulerCanvas container;
    Timeline timeline;

    // EFFECTS: Constructs the pane, setting dimensions and appropirate listeners and viewports
    public RulerScrollPane() {
        timeline = Timeline.getInstance();
        container = new RulerCanvas();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));

        Menu.addRepaintListener(this);
        Timeline.addObserver(this);

        this.setPreferredSize(new Dimension(DEFAULT_RULER_HEIGHT, RULER_HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, RULER_HEIGHT));
        this.setMinimumSize(new Dimension(DEFAULT_RULER_HEIGHT, RULER_HEIGHT));

        this.setBorder(null);

        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        this.setViewportView(container);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // EFFECTS: adjusts the width of the ruler to match the MidiTrackPanel rows
    public void updateWidth(int midiTrackWidth) {
        int adjustedWidth = midiTrackWidth + MidiTrackLabelPanel.LABEL_BOX_WIDTH;
        SwingUtilities.invokeLater(() -> {
            container.revalidate();
            container.repaint();
            container.setPreferredSize(new Dimension(adjustedWidth, RULER_HEIGHT));
            container.setMinimumSize(new Dimension(adjustedWidth, RULER_HEIGHT));
            container.setMaximumSize(new Dimension(adjustedWidth, RULER_HEIGHT));

            revalidate();
            repaint();

            container.revalidate();
            container.repaint();
        });
    }

    // MODIFIES: this
    // EFFECTS: listens for property changes and executes appropriate methods
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "timeline":
                revalidate();
                repaint();
                break;
            default:
                break;
        }
    }

    public RulerCanvas getCanvas() {
        return container;
    }

}
