package ui.windows.timeline;

import model.Player;
import model.TimelineController;
import ui.common.LineContainerPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TimelineLineContainerPanel extends LineContainerPanel implements PropertyChangeListener {

    public TimelineLineContainerPanel(TimelineController timelineController, Player player) {
        super(timelineController, player);
        timelineController.addObserver(this);
    }

    // EFFECTS: listens for property changes and runs methods accordingly
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        switch (propertyName) {
            case "tickPosition": case "horizontalScaleFactor":
                updateLineX();
                break;
            case "timelineReplaced":
                updatePlayer();
                break;
            case "blockPasted":
            case "blockCreated":
            case "noteCreated":
            case "pianoRollNoteEdited":
                repaint();
                break;
        }
    }
}
