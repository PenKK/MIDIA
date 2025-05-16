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

    @Override
    public void mouseDragged(MouseEvent e) {
        timelineController.startRulerDrag();
        updateX(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        updateX(e);
        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            resume = true;
        }
    }

    private void updateX(MouseEvent e) {
        Timeline timeline = timelineController.getTimeline();

        int tick = (int) ((e.getX() - TrackLabelPanel.LABEL_BOX_WIDTH)
                / timelineController.getTimeline().getHorizontalScale());

        timeline.getPlayer().setPositionTick(tick);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        timelineController.stopRulerDrag();
        if (resume) {
            timelineController.playTimeline();
        }
    }
}
