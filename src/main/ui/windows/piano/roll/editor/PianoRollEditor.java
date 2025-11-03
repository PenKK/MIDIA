package ui.windows.piano.roll.editor;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PianoRollEditor extends JPanel implements PropertyChangeListener {

    private static final Color BEAT_LINE_COLOR = Color.decode("#303234");
    private static final Color MEASURE_LINE_COLOR = Color.decode("#242627");
    private static final Color NOTE_COLOR = Color.decode("#AB47BC");
    private static final Color BACKGROUND_COLOR = Color.decode("#2A2C2D");

    private final BlockPlayer blockPlayer;
    private final TimelineController timelineController;

    PianoRollEditor(BlockPlayer blockPlayer, TimelineController timelineController) {
        this.blockPlayer = blockPlayer;
        this.timelineController = timelineController;
        int width = getLoopPixelWidth();
        this.setPreferredSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setMinimumSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setBackground(BACKGROUND_COLOR);

        PianoRollEditorMouseAdapter mouseAdapter = new PianoRollEditorMouseAdapter(blockPlayer, timelineController);
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        assignControls();

        blockPlayer.addPropertyChangeListener(this);
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
        g.setColor(Color.decode("#3c3f41"));
        g.fillRect(0, 0, getLoopPixelWidth(), this.getHeight());

        drawGridLines(g);
        drawBlockNotes(g);
    }

    private int getLoopPixelWidth() {
        return timelineController.getTimeline().scaleTickToPixel(blockPlayer.getBlock().getDurationTicks());
    }

    @SuppressWarnings("methodlength")
    private void drawGridLines(Graphics g) {
        Timeline timeline = timelineController.getTimeline();

        int beatDivisions = timeline.getPlayer().getBeatDivision();
        int beatsPerMeasure = timeline.getPlayer().getBeatsPerMeasure();
        int ppq = Player.PULSES_PER_QUARTER_NOTE;

        long divisionTickInterval = ppq / beatDivisions;
        long measureTickInterval = (long) ppq * beatsPerMeasure;

        boolean percussive = blockPlayer.getParentMidiTrack().isPercussive();
        int topY = 7 * PianoRollNoteDisplay.KEY_HEIGHT;
        int botY = 8 * PianoRollNoteDisplay.KEY_HEIGHT;

        g.setColor(BEAT_LINE_COLOR);
        // Horizontal lines
        if (percussive) {
            g.drawLine(0, topY, getWidth(), topY);
            g.drawLine(0, botY, getWidth(), botY);
        } else {
            for (int i = 0; i <= 127; i++) {
                int y = i * PianoRollNoteDisplay.KEY_HEIGHT;
                g.drawLine(0, y, getWidth(), y);
            }
        }
        // Vertical lines
        for (long tick = 0; tick <= getWidth() / timeline.getPixelsPerTick(); tick += divisionTickInterval) {
            g.setColor(BEAT_LINE_COLOR);
            int pixelPosition = timeline.scaleTickToPixel(tick);

            if (tick % ppq == 0) {
                g.setColor(MEASURE_LINE_COLOR);
            } else if (tick % measureTickInterval == 0) {
                g.setColor(MEASURE_LINE_COLOR.darker());
            }
            g.drawLine(pixelPosition, percussive ? topY : 0, pixelPosition,percussive ? botY : getHeight());
        }
    }

    private void drawBlockNotes(Graphics g) {
        Timeline timeline = timelineController.getTimeline();
        Block block = blockPlayer.getBlock();
        g.setColor(NOTE_COLOR);

        block.getNotes().forEach(note -> {
            int x = timeline.scaleTickToPixel(note.getStartTick());
            int y = PianoRollNoteDisplay.KEY_HEIGHT * 127 - note.getPitch() * PianoRollNoteDisplay.KEY_HEIGHT;
            int width = timeline.scaleTickToPixel(note.getDurationTicks());
            int height = PianoRollNoteDisplay.KEY_HEIGHT;
            g.fillRect(x, y, width, height);
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property =  evt.getPropertyName();

        switch (property) {
            case "noteRemoved":
            case "noteCreated":
                repaint();
        }
    }
}
