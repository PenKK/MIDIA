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

/**
 * Base class for modal input dialogs used in the menu system.
 */
public abstract class InputDialog extends JDialog implements ActionListener {

    protected TimelineController timelineController;
    private final Component invoker;

    /**
     * Creates an input dialog with the specified title and preferred size.
     *
     * @param frameTitle         the dialog title
     * @param invoker            the component the dialog is positioned relative to
     * @param d                  the preferred dialog size
     * @param timelineController the controller used to act on user input
     */
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

    /**
     * Initializes and adds fields to the dialog.
     */
    protected abstract void initFields();

    /**
     * Shows the dialog centered relative to the invoker window.
     */
    public void display() {
        this.setLocationRelativeTo(SwingUtilities.getWindowAncestor(invoker));
        this.setVisible(true);
    }
}
