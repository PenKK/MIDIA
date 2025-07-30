package ui.windows.piano.roll;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;


import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ui.ruler.BlankScrollPane;
import ui.windows.timeline.midi.TrackLabelPanel;

public class PianoRollKeysScrollPane extends BlankScrollPane {

    private static int KEY_HEIGHT = 30;

    PianoRollKeysScrollPane() {
        super();
        JPanel keysRenderPanel = keysRenderPanel();

        keysRenderPanel.setBackground(Color.WHITE);
        keysRenderPanel.setPreferredSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, 7 * 10 * KEY_HEIGHT));
        setPreferredSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, 7 * 10 * KEY_HEIGHT));
        setMaximumSize(new Dimension(TrackLabelPanel.LABEL_BOX_WIDTH, 7 * 10 * KEY_HEIGHT));

        setViewportView(keysRenderPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
        addKeys();
    }

    private JPanel keysRenderPanel() {
        return new JPanel() {
            
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                
                g.drawRect(0, 7 * 10 * KEY_HEIGHT - 20, 10, 10);
            }
        };
    }

    private void addKeys() {
        String[] noteNames = {
                "C", "C#", "D", "D#", "E", "F",
                "F#", "G", "G#", "A", "A#", "B"
        };

        for (int i = 127; i >= 0; i--) {
            String noteName = noteNames[i % 12].concat(Integer.toString(i / 12 - 1));
            
        }
    }
}
