package ui.windows.piano.roll.ruler;


import javax.swing.BoxLayout;

import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.ruler.RulerScrollPane;
import ui.windows.timeline.midi.LineContainerPanel;

public class PianoRollRulerScrollPane extends RulerScrollPane {

    private final PianoRollRulerRenderPanel pianoRollRulerRenderPanel;

    public PianoRollRulerScrollPane(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        pianoRollRulerRenderPanel = new PianoRollRulerRenderPanel(timelineController, blockPlayer);
        setViewportView(pianoRollRulerRenderPanel);
    }
}
