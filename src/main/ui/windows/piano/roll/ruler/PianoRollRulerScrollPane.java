package ui.windows.piano.roll.ruler;


import model.BlockPlayer;
import model.TimelineController;
import ui.common.RulerDimensionHelper;
import ui.ruler.RulerScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * Scroll pane hosting the piano roll ruler and implementing width updates to match the editor grid.
 */
public class PianoRollRulerScrollPane extends RulerScrollPane implements RulerDimensionHelper.RulerWidthUpdater {

    private final PianoRollRulerRenderPanel pianoRollRulerRenderPanel;

    /**
     * Constructs the scroll pane and embeds the piano roll ruler renderer.
     *
     * @param timelineController the controller providing state
     * @param blockPlayer        the block player associated with this piano roll
     */
    public PianoRollRulerScrollPane(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        pianoRollRulerRenderPanel = new PianoRollRulerRenderPanel(timelineController, blockPlayer);
        setViewportView(pianoRollRulerRenderPanel);
    }

    /**
     * Adjusts the ruler width to match the content grid width.
     */
    @Override
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            pianoRollRulerRenderPanel.setPreferredSize(new Dimension(width, RulerScrollPane.RULER_HEIGHT));
            pianoRollRulerRenderPanel.revalidate();
        });
    }
}
