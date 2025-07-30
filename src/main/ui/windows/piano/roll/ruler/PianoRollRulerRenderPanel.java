package ui.windows.piano.roll.ruler;

import java.awt.Graphics;

import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.ruler.RulerRenderPanel;

public class PianoRollRulerRenderPanel extends RulerRenderPanel {

    private final TimelineController timelineController;
    private final MidiTrack parentMidiTrack;
    private final BlockPlayer blockPlayer;
    
    public PianoRollRulerRenderPanel(TimelineController timelineController,
                                     MidiTrack parentMidiTrack, BlockPlayer blockPlayer) {
        super();
        this.timelineController = timelineController;
        this.parentMidiTrack = parentMidiTrack;
        this.blockPlayer = blockPlayer;

        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController, blockPlayer);
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g, timelineController, getWidth());
    }
}
