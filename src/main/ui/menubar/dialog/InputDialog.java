package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import model.TimelineController;

public abstract class InputDialog extends JDialog implements ActionListener {

    protected TimelineController timelineController;
    private final Component invoker;

    // EFFECTS: creates an input dialog with the specified title
    InputDialog(String frameTitle, Component invoker, Dimension d, TimelineController timelineController) {
        super((JFrame) SwingUtilities.getWindowAncestor(invoker), frameTitle, true);

        this.timelineController = timelineController;
        this.invoker = invoker;
        initFields();

        this.setLayout(new GridLayout(0, 2, 10, 10));
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        this.setPreferredSize(new Dimension(d));
        this.pack();
    }

    // MODIFIES: this
    // EFFECTS: initializes fields and adds them to this
    protected abstract void initFields();

    // MODIFIES: this
    // EFFECTS: makes the dialog visible and relative to invoker
    public void display() {
        this.setLocationRelativeTo(SwingUtilities.getWindowAncestor(invoker));
        this.setVisible(true);
    }
}
