package ui.ruler;

import ui.common.BlankScrollPane;
import ui.windows.timeline.midi.TrackLabelPanel;

import java.awt.Dimension;

import javax.swing.JScrollPane;

/**
 * Scroll pane wrapper for ruler components with fixed height and no scroll bars.
 */
public abstract class RulerScrollPane extends BlankScrollPane {

    public static final int RULER_HEIGHT = TrackLabelPanel.HEIGHT / 4;
    public static final int DEFAULT_RULER_WIDTH = 800;

    public RulerScrollPane() {
        this.setPreferredSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, RULER_HEIGHT));
        this.setMinimumSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));

        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
