package ui.windows.piano.roll.ruler;

import java.awt.Graphics;

import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.ruler.RulerRenderPanel;

import javax.swing.*;

public class PianoRollRulerRenderPanel extends RulerRenderPanel {

    private final TimelineController timelineController;
    private final BlockPlayer blockPlayer;

    public PianoRollRulerRenderPanel(TimelineController timelineController, BlockPlayer blockPlayer) {
        super();
        this.timelineController = timelineController;
        this.blockPlayer = blockPlayer;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController, blockPlayer);
        addMouseAdapter(mouseAdapter);
    }

    // MODIFIES: this
    // EFFECTS: Draws the ruler markings
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g, timelineController, getWidth());
    }
}
