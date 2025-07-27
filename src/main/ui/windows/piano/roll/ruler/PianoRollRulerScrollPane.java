package ui.windows.piano.roll.ruler;


import javax.swing.BoxLayout;

import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.ruler.RulerScrollPane;

public class PianoRollRulerScrollPane extends RulerScrollPane {

    private final PianoRollRulerRenderPanel pianoRollRulerRenderPanel;

    public PianoRollRulerScrollPane(TimelineController timelineController, MidiTrack parentMidiTrack,
                                    BlockPlayer blockPlayer) {
        super();
        pianoRollRulerRenderPanel = new PianoRollRulerRenderPanel(timelineController, parentMidiTrack, blockPlayer);
        pianoRollRulerRenderPanel.setLayout(new BoxLayout(pianoRollRulerRenderPanel, BoxLayout.X_AXIS));
        setViewportView(pianoRollRulerRenderPanel);
    }
}
