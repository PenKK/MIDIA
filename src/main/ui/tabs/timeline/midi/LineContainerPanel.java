package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Timeline;

public class LineContainerPanel extends JPanel implements PropertyChangeListener {
    private int lineX = MidiTrackLabelPanel.LABEL_BOX_WIDTH;

    public LineContainerPanel() {
        super();
        Timeline.addObserver(this);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void updateLineX() {
        this.lineX = MidiTrackRenderPanel.scalePixelsRender(Timeline.getInstance().getPositionTick()) 
                                                            + MidiTrackLabelPanel.LABEL_BOX_WIDTH;
        repaint(); 
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (lineX >= 0) {
            g.setColor(Color.RED);
            g.drawLine(lineX, 0, lineX, getHeight());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("positionTick")) {
            updateLineX();
        }
    }
}