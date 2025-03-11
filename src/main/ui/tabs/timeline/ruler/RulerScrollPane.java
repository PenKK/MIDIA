package ui.tabs.timeline.ruler;

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;

import model.Timeline;
import ui.menubar.Menu;
import ui.tabs.timeline.midi.MidiTrackPanel;

// Panel that shows the tickmarks above timeline to indicate beat marks and other timely infomration
public class RulerScrollPane extends JScrollPane implements PropertyChangeListener {

    RulerCanvas container;
    Timeline timeline;

    // EFFECTS: Constructs the pane, setting dimensions and appropirate listeners and viewports
    public RulerScrollPane() {
        timeline = Timeline.getInstance();
        container = new RulerCanvas();
        container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
        Menu.addRepaintListener(this);
        int quarterHeight = MidiTrackPanel.HEIGHT / 4;

        this.setBorder(null);
        this.setPreferredSize(new Dimension(800, quarterHeight));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, quarterHeight));
        this.setMinimumSize(new Dimension(800, quarterHeight));

        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        
        this.setViewportView(container);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    

}
