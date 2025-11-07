package ui.windows.piano.roll.editor;

import model.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main editing surface for the piano roll, responsible for drawing the grid and notes,
 * and handling simple keyboard controls (e.g., toggle loop).
 */
public class PianoRollEditor extends JPanel implements PropertyChangeListener {

    private static final Color BEAT_LINE_COLOR = Color.decode("#303234");
    private static final Color MEASURE_LINE_COLOR = Color.decode("#242627");
    private static final Color NOTE_COLOR = Color.decode("#AB47BC");
    private static final Color BACKGROUND_COLOR = Color.decode("#2A2C2D");

    private final PianoRollPlayer pianoRollPlayer;
    private final TimelineController timelineController;

    /**
     * Constructs the piano roll editor and wires mouse and keyboard interactions.
     */
    PianoRollEditor(PianoRollPlayer pianoRollPlayer, TimelineController timelineController) {
        this.pianoRollPlayer = pianoRollPlayer;
        this.timelineController = timelineController;
        int width = getLoopPixelWidth();
        this.setPreferredSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setMinimumSize(new Dimension(width, 128 * PianoRollNoteDisplay.KEY_HEIGHT));
        this.setBackground(BACKGROUND_COLOR);

        PianoRollEditorMouseAdapter mouseAdapter = new PianoRollEditorMouseAdapter(pianoRollPlayer, timelineController);
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        assignControls();

        pianoRollPlayer.addPropertyChangeListener(this);
    }

    /**
     * Binds keyboard shortcuts (e.g., Space to toggle loop).
     */
    private void assignControls() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("SPACE"), "toggleLoop");
        inputMap.put(KeyStroke.getKeyStroke("D"), "incrementBeatDivision");
        inputMap.put(KeyStroke.getKeyStroke("shift D"), "decrementBeatDivision");
        inputMap.put(KeyStroke.getKeyStroke("M"), "incrementBeatsPerMeasure");
        inputMap.put(KeyStroke.getKeyStroke("shift M"), "decrementBeatsPerMeasure");

        actionMap.put("toggleLoop", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pianoRollPlayer.toggleLoop();
            }
        });
        actionMap.put("incrementBeatDivision", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pianoRollPlayer.incrementBeatDivision();
                repaint();
            }
        });
        actionMap.put("decrementBeatDivision", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pianoRollPlayer.decrementBeatDivision();
                repaint();
            }
        });
        actionMap.put("incrementBeatsPerMeasure", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pianoRollPlayer.incrementBeatsPerMeasure();
                repaint();
            }
        });
        actionMap.put("decrementBeatsPerMeasure", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pianoRollPlayer.decrementBeatsPerMeasure();
                repaint();
            }
        });
    }

    /**
     * Paints the piano roll background, grid lines, and notes.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.decode("#3c3f41"));
        g.fillRect(0, 0, getLoopPixelWidth(), this.getHeight());

        drawGridLines(g);
        drawBlockNotes(g);
    }

    private int getLoopPixelWidth() {
        return timelineController.getTimeline().scaleTickToPixel(pianoRollPlayer.getBlock().getDurationTicks());
    }

    /**
     * Draws horizontal (pitches) and vertical (time) grid lines for the editor.
     */
    @SuppressWarnings("methodlength")
    private void drawGridLines(Graphics g) {
        Timeline timeline = timelineController.getTimeline();

        int ppq = Player.PULSES_PER_QUARTER_NOTE;

        long divisionTickInterval = (long) ppq / pianoRollPlayer.getBeatDivision();
        long measureTickInterval = (long) ppq * pianoRollPlayer.getBeatsPerMeasure();

        boolean percussive = pianoRollPlayer.getParentMidiTrack().isPercussive();
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

            if (tick % measureTickInterval == 0) {
                g.setColor(MEASURE_LINE_COLOR.darker().darker());
            } else if (tick % ppq == 0) {
                g.setColor(MEASURE_LINE_COLOR);
            }
            g.drawLine(pixelPosition, percussive ? topY : 0, pixelPosition,percussive ? botY : getHeight());
        }
    }

    /**
     * Draws the notes contained in the current block onto the editor surface.
     */
    private void drawBlockNotes(Graphics g) {
        Timeline timeline = timelineController.getTimeline();
        Block block = pianoRollPlayer.getBlock();
        g.setColor(NOTE_COLOR);

        block.getNotes().forEach(note -> {
            int x = timeline.scaleTickToPixel(note.getStartTick());
            int y = PianoRollNoteDisplay.KEY_HEIGHT * 127 - note.getPitch() * PianoRollNoteDisplay.KEY_HEIGHT;
            int width = timeline.scaleTickToPixel(note.getDurationTicks());
            int height = PianoRollNoteDisplay.KEY_HEIGHT;
            g.fillRect(x, y, width, height);
        });
    }

    /**
     * Repaints the editor when notes are created or removed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property =  evt.getPropertyName();

        switch (property) {
            case "noteRemoved":
            case "noteCreated":
                repaint();
                try {
                    pianoRollPlayer.updateSequence();
                } catch (InvalidMidiDataException e) {
                    throw new RuntimeException("Failed to update sequence on note change in Piano Roll", e);
                }
                break;
        }
    }
}
