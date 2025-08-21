package ui.windows.piano.roll.editor;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PianoRollNoteGridEditor extends JPanel {

    private static final Color BEAT_LINE_COLOR = Color.decode("#303234");
    private static final Color MEASURE_LINE_COLOR = Color.decode("#242627");
    private static final Color NOTE_COLOR = Color.decode("#AB47BC");

    private final BlockPlayer blockPlayer;
    private final TimelineController timelineController;

    PianoRollNoteGridEditor(BlockPlayer blockPlayer, TimelineController timelineController) {
        this.blockPlayer = blockPlayer;
        this.timelineController = timelineController;
        this.setPreferredSize(new Dimension(1000, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setMinimumSize(new Dimension(1000, 128 * PianoRollNoteDisplay.KEY_HEIGHT));

        assignControls();
    }

    private void assignControls() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "toggleLoop");

        getActionMap().put("toggleLoop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                blockPlayer.toggleLoop();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        drawGridLines(g);
        drawBlockNotes(g);
    }

    private void drawGridLines(Graphics g) {
        Timeline timeline = timelineController.getTimeline();

        int beatDivisions = timeline.getBeatDivision();
        int beatsPerMeasure = timeline.getBeatsPerMeasure();
        int ppq = Player.PULSES_PER_QUARTER_NOTE;

        long divisionTickInterval = ppq / beatDivisions;
        long measureTickInterval = (long) ppq * beatsPerMeasure;
        // Vertical lines
        for (long tick = 0; tick <= getWidth() / timeline.getPixelsPerTick(); tick += divisionTickInterval) {
            g.setColor(BEAT_LINE_COLOR);
            int pixelPosition = (int) (timeline.scaleTickToPixel(tick));

            if (tick % ppq == 0) { // for each beat
                if (tick % measureTickInterval == 0) {
                    g.setColor(MEASURE_LINE_COLOR);
                }
                g.drawLine(pixelPosition, 0, pixelPosition, getHeight());
            }
        }
        // Horizontal lines
        for (int i = 0; i <= 127; i++) {
            g.setColor(BEAT_LINE_COLOR);
            int y = i * PianoRollNoteDisplay.KEY_HEIGHT;
            g.drawLine(0, y, getWidth(), y);
        }
    }

    private void drawBlockNotes(Graphics g) {
        Timeline timeline = timelineController.getTimeline();
        Block block = blockPlayer.getBlock();
        g.setColor(NOTE_COLOR);

        block.getNotes().forEach(note -> {
            int x = (int) timeline.scaleTickToPixel(note.getStartTick());
            int y = PianoRollNoteDisplay.KEY_HEIGHT * 127 - note.getPitch() * PianoRollNoteDisplay.KEY_HEIGHT;
            int width = (int) timeline.scaleTickToPixel(note.getDurationTicks());
            int height = PianoRollNoteDisplay.KEY_HEIGHT;
            g.fillRect(x, y, width, height);
        });
    }
}
