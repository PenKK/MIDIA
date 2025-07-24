package ui.windows.timeline.ruler;

import java.awt.event.MouseEvent;

import model.Timeline;
import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.windows.timeline.midi.TrackLabelPanel;

public class TimelineRulerMouseAdapter extends RulerMouseAdapter {

    private TimelineController timelineController;

    public TimelineRulerMouseAdapter(TimelineController timelineController) {
        this.timelineController = timelineController;
    }

    @Override
    protected void updateX(MouseEvent e) {
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

        if (!timelineController.getTimeline().getPlayer().isDraggingRuler()) {
            timelineController.getTimeline().getPlayer().startRulerDrag();
        }
        updateX(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateX(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        timelineController.getTimeline().getPlayer().stopRulerDrag();
        if (resume) {
            timelineController.playTimeline();
            resume = false;
        }
    }
}
