package ui.ruler;

import java.awt.Dimension;

import javax.swing.JScrollPane;

import ui.windows.timeline.midi.TrackPanel;

public abstract class RulerScrollPane extends BlankScrollPane {

    public static final int BEAT_WIDTH = 25;
    public static final int RULER_HEIGHT = TrackPanel.HEIGHT / 4;
    public static final int DEFAULT_RULER_WIDTH = 800;

    public RulerScrollPane() {
        this.setPreferredSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, RULER_HEIGHT));
        this.setMinimumSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));

        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
}
