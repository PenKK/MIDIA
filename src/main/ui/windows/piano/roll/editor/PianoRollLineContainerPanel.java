package ui.windows.piano.roll.editor;

import model.BlockPlayer;
import model.TimelineController;
import ui.common.LineContainerPanel;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PianoRollLineContainerPanel extends LineContainerPanel implements PropertyChangeListener {

    PianoRollLineContainerPanel(TimelineController timelineController, BlockPlayer blockPlayer) {
        super(timelineController, blockPlayer);
        blockPlayer.addPropertyChangeListener(this);

        int width = timelineController.getTimeline().scaleTickToPixel(blockPlayer.getBlock().getDurationTicks());

        this.setPreferredSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setMinimumSize(new Dimension(1000, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("tickPosition")) {
            updateLineX();
        }
    }
}
