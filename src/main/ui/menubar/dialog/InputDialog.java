package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;

import model.TimelineController;

public abstract class InputDialog extends JDialog implements ActionListener {

    protected TimelineController timelineController;

    // EFFECTS: creates an input dialog with the specified title
    InputDialog(String frameTitle, Component invoker, Rectangle r, TimelineController timelineController) {
        super((Frame) null, frameTitle, true);

        this.timelineController = timelineController;
        initFields();

        this.setLayout(new GridLayout(0, 2, 10, 10));
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        display(invoker, r);
    }

    // MODIFIES: this
    // EFFECTS: initializes fields and adds them to this
    protected abstract void initFields();

    // MODIFIES: this
    // EFFECTS: makes the dialog visible and relative to invoker
    protected void display(Component invoker, Rectangle r) {
        this.setBounds(r);
        this.setLocationRelativeTo(invoker);
        this.setVisible(true);
    }
}
