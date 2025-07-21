package ui.ruler;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public abstract class RulerMouseAdapter extends MouseInputAdapter {

    protected boolean resume = false;

    protected abstract void updateX(MouseEvent e);
}
