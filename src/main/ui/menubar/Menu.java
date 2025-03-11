package ui.menubar;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

// This interface specifies behavior of different drop downs Menu's in the MenuBar
public class Menu extends JMenu implements PopupMenuListener {

    private static final List<JComponent> repaintComponents = new ArrayList<>();

    // EFFECTS: Makes it so that all Menu's repaint select panels to fix bleeding on paintable components
    Menu() {
        JPopupMenu popupMenu = this.getPopupMenu();
        popupMenu.addPopupMenuListener(this);
    }

    // MODIFIES: this
    // EFFECTS: Adds the component as a listener for repainting
    public static void addRepaintListener(JComponent component) {
        repaintComponents.add(component);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

    }

    // MODIFIES: this, components in repaintComponents
    // EFFECTS: Revalidates and repaints listening componenets to fix stains left by menu open
    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        SwingUtilities.invokeLater(() -> {
            for (JComponent component : repaintComponents) {
                component.getParent().revalidate();
                component.getParent().repaint();
            }
        });
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {

    }
}
