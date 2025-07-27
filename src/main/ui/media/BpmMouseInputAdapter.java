package ui.media;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.event.MouseInputAdapter;

import model.TimelineController;

public class BpmMouseInputAdapter extends MouseInputAdapter {

    private static final int RESISTANCE = 10;

    private final TimelineController timelineController;
    private final JLabel bpmDisplay;
    private Robot robot;
    private Point anchorPointOnScreen;
    private float dragAccumulator;

    BpmMouseInputAdapter(TimelineController timelineController, JLabel bpmDisplay) {
        this.timelineController = timelineController;
        this.bpmDisplay = bpmDisplay;
        dragAccumulator = 0;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.out.println("Failed to initialize BpmMouseInputAdapter robot");
            e.printStackTrace();
        }
    }

    private static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit()
            .createCustomCursor(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "invisible");

    private float newBpm = 0;
    private Cursor orignalCursor;

    @Override
    public void mousePressed(MouseEvent e) {
        newBpm = timelineController.getTimeline().getPlayer().getBPM();
        orignalCursor = bpmDisplay.getCursor();
        anchorPointOnScreen = e.getLocationOnScreen();
        bpmDisplay.setCursor(INVISIBLE_CURSOR);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int currentY = e.getYOnScreen();
        int deltaY = currentY - anchorPointOnScreen.y;
        dragAccumulator += deltaY;

        float change = e.isShiftDown() ? 0.01f : 1f;

        while (dragAccumulator >= RESISTANCE) {
            newBpm -= change;
            dragAccumulator -= RESISTANCE;
        }
        
        while (dragAccumulator <= RESISTANCE) {
            newBpm += change;
            dragAccumulator += RESISTANCE;
        }

        newBpm = Math.max(1, Math.min(999, newBpm));

        setBpmDisplay(newBpm);
        robot.mouseMove(anchorPointOnScreen.x, anchorPointOnScreen.y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        changeBpm(Float.valueOf(bpmDisplay.getText()));
        bpmDisplay.setCursor(orignalCursor);
    }

    private void changeBpm(float bpm) {
        timelineController.getTimeline().getPlayer().setBPM(bpm);
        setBpmDisplay(bpm);
    }

    private void setBpmDisplay(float bpm) {
        bpmDisplay.setText(String.format("%6.2f", bpm));
    }
}
