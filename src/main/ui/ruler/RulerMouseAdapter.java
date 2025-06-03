package ui.ruler;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

import model.Timeline;
import model.TimelineController;
import ui.tabs.timeline.midi.TrackLabelPanel;

public class RulerMouseAdapter extends MouseInputAdapter {

    private TimelineController timelineController;
    private boolean resume = false;

    RulerMouseAdapter(TimelineController timelineController) {
        this.timelineController = timelineController;
    }

    private void updateX(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();

        int tick = (int) Math.max(0, (timeline.scalePixelToTick(e.getX() - TrackLabelPanel.LABEL_BOX_WIDTH)));

        tick = Math.max(tick, 0);
        timeline.getPlayer().setPositionTick(tick);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            resume = true;
        }

        if (!timelineController.isDraggingRuler()) {
            timelineController.startRulerDrag();
        }
        updateX(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateX(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        timelineController.stopRulerDrag();
        if (resume) {
            timelineController.playTimeline();
            resume = false;
        }
    }
}
