package ui.ruler;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

public class BlankScrollPane extends JScrollPane {

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
