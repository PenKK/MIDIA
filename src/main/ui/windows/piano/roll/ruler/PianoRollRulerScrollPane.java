package ui.windows.piano.roll.ruler;


import model.BlockPlayer;
import model.TimelineController;
import ui.ruler.RulerScrollPane;

public class PianoRollRulerScrollPane extends RulerScrollPane {

    private final PianoRollRulerRenderPanel pianoRollRulerRenderPanel;

    public PianoRollRulerScrollPane(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        pianoRollRulerRenderPanel = new PianoRollRulerRenderPanel(timelineController, blockPlayer);
        setViewportView(pianoRollRulerRenderPanel);
    }
}
