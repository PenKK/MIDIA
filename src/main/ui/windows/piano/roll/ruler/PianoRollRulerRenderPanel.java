package ui.windows.piano.roll.ruler;

import java.awt.Graphics;

import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.ruler.RulerRenderPanel;

import javax.swing.*;

/**
 * Ruler render panel for the piano roll view.
 * Draws measure, beat, and division tick marks and allows position scrubbing.
 */
public class PianoRollRulerRenderPanel extends RulerRenderPanel {

    private final TimelineController timelineController;
    private final BlockPlayer blockPlayer;

    /**
     * Creates a piano roll ruler render panel and wires mouse interaction for scrubbing.
     *
     * @param timelineController the controller providing timeline state
     * @param blockPlayer        the player used in the piano roll context
     */
    public PianoRollRulerRenderPanel(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        this.timelineController = timelineController;
        this.blockPlayer = blockPlayer;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController, blockPlayer);
        addMouseAdapter(mouseAdapter);
    }

    /**
     * Paints the ruler tick marks according to the current timeline settings.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g, timelineController, getWidth());
    }
}
