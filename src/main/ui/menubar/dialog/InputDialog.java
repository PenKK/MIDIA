package ui.menubar.dialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;

public abstract class InputDialog extends JDialog implements ActionListener {

    // EFFECTS: creates an input dialog with the specified title
    InputDialog(String frameTitle) {
        super((Frame) null, frameTitle, true);

        initFields();

        this.setLayout(new GridLayout(0, 2, 10, 10));
        this.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
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
