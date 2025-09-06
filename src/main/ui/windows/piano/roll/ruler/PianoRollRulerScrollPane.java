package ui.windows.piano.roll.ruler;


import model.BlockPlayer;
import model.TimelineController;
import ui.common.RulerDimensionHelper;
import ui.ruler.RulerScrollPane;

import javax.swing.*;
import java.awt.*;

public class PianoRollRulerScrollPane extends RulerScrollPane implements RulerDimensionHelper.RulerWidthUpdater {

    private final PianoRollRulerRenderPanel pianoRollRulerRenderPanel;

    public PianoRollRulerScrollPane(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        pianoRollRulerRenderPanel = new PianoRollRulerRenderPanel(timelineController, blockPlayer);
        setViewportView(pianoRollRulerRenderPanel);
    }

    @Override
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            pianoRollRulerRenderPanel.setPreferredSize(new Dimension(width, RulerScrollPane.RULER_HEIGHT));
            pianoRollRulerRenderPanel.revalidate();
        });
    }
}
