package ui.windows.piano.roll.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;


import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ui.common.BlankScrollPane;
import ui.windows.timeline.midi.TrackLabelPanel;

/**
 * Fixed-width scroll pane that renders piano key names and octave guidelines.
 */
public class PianoRollNoteDisplay extends BlankScrollPane {

    public static final int KEY_HEIGHT = 20;
    public static final int KEY_WIDTH = TrackLabelPanel.LABEL_BOX_WIDTH;

    /**
     * Constructs the piano key display panel with a fixed width and custom renderer.
     */
    public PianoRollNoteDisplay() {
        super();
        JPanel keysRenderPanel = keysRenderPanel();

        keysRenderPanel.setBackground(Color.WHITE);
        keysRenderPanel.setPreferredSize(new Dimension(KEY_WIDTH, 128 * KEY_HEIGHT));
        setPreferredSize(new Dimension(KEY_WIDTH, 128 * KEY_HEIGHT));
        setMinimumSize(new Dimension(KEY_WIDTH, 128 * KEY_HEIGHT));
        setMaximumSize(new Dimension(KEY_WIDTH, 128 * KEY_HEIGHT));

        setViewportView(keysRenderPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
    }

    /**
     * Creates the inner panel responsible for painting keys and octave guidelines.
     */
    private JPanel keysRenderPanel() {
        return new JPanel() {
            
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                addKeys(g);
            }
        };
    }

    /**
     * Renders black/white keys and octave markers with note names.
     */
    private void addKeys(Graphics g) {
        for (int i = 0; i <= 127; i++) {
            g.setColor(Color.BLACK);
            int noteIndex = i % 12;
            boolean isBlackKey = (noteIndex == 1 || noteIndex == 3
                               || noteIndex == 6 || noteIndex == 8
                               || noteIndex == 10);
            int y = 128 * KEY_HEIGHT - i * KEY_HEIGHT;

            if (isBlackKey) {
                g.fillRect(0, y - KEY_HEIGHT, (int) (KEY_WIDTH * 0.65), KEY_HEIGHT);
            }

            if (noteIndex == 0) {
                String noteName = "C".concat(Integer.toString(i / 12 - 1));
                g.drawString(noteName, KEY_WIDTH - 20, y - 5);
                g.drawLine(0, y, KEY_WIDTH, y);
            }

            if (noteIndex == 5) {
                g.setColor(Color.LIGHT_GRAY);
                g.drawLine(0, y, KEY_WIDTH, y);
            }
        }
    }
}
