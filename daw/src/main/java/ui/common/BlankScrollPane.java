package ui.common;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * Base scroll pane with neutral styling and sensible defaults for scrolling behavior.
 */
public abstract class BlankScrollPane extends JScrollPane {

    /**
     * Constructs a blank scroll pane with no border and smooth scrolling increments.
     */
    public BlankScrollPane() {
        this.setBorder(null);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.getHorizontalScrollBar().setUnitIncrement(16);
        this.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
    }
}
