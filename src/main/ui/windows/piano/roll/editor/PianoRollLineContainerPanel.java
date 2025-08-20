package ui.windows.piano.roll.editor;

import model.BlockPlayer;
import model.TimelineController;
import ui.ruler.LineContainerPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PianoRollLineContainerPanel extends LineContainerPanel implements PropertyChangeListener {

    PianoRollLineContainerPanel(TimelineController timelineController, BlockPlayer blockPlayer) {
        super(timelineController, blockPlayer);
        blockPlayer.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("positionTick")) {
            updateLineX();
        }
    }
}
