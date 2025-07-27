package ui.windows.timeline.midi.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.Block;
import model.editing.DawClipboard;

public class BlockPopupMenu extends JPopupMenu implements ActionListener {

    private final Block block;
    private final DawClipboard dawClipboard;

    private final JMenuItem copyMenuItem;

    public BlockPopupMenu(Block block, DawClipboard dawClipboard) {
        this.block = block;
        this.dawClipboard = dawClipboard;

        copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.addActionListener(this);
        
        this.add(copyMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(copyMenuItem)) {
            dawClipboard.copy(Collections.singletonList(block));
        }
    }
}