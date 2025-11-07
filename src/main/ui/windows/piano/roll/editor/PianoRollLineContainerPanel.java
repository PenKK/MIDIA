package ui.windows.piano.roll.editor;

import model.PianoRollPlayer;
import model.TimelineController;
import ui.common.LineContainerPanel;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Overlay panel drawing the playhead line above the piano roll editor grid.
 */
public class PianoRollLineContainerPanel extends LineContainerPanel implements PropertyChangeListener {

    /**
     * Constructs the overlay and sizes it to the block duration and piano roll height.
     */
    PianoRollLineContainerPanel(TimelineController timelineController, PianoRollPlayer pianoRollPlayer) {
        super(timelineController, pianoRollPlayer);
        pianoRollPlayer.addPropertyChangeListener(this);

        int width = timelineController.getTimeline().scaleTickToPixel(pianoRollPlayer.getBlock().getDurationTicks());

        this.setPreferredSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setMinimumSize(new Dimension(1000, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
    }

    /**
     * Advances the playhead line during playback updates.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if (propertyName.equals("tickPosition")) {
            updateLineX();
        } else if (propertyName.equals("beatDivision") || propertyName.equals("beatsPerMeasure")) {
            repaint();
        }
    }
}
