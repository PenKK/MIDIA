package ui.tabs.timeline.midi.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.Block;
import model.TimelineController;
import model.util.DawClipboard;

public class BlockPopupMenu extends JPopupMenu implements ActionListener {

    private Block block;
    private DawClipboard dawClipboard;

    private JMenuItem copyMenuItem;

    public BlockPopupMenu(Block block, TimelineController timelineController, DawClipboard dawClipboard) {
        this.block = block;
        this.dawClipboard = dawClipboard;

        copyMenuItem = new JMenuItem("Copy");
        copyMenuItem.addActionListener(this);
        
        this.add(copyMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(copyMenuItem)) {
            dawClipboard.copy(Arrays.asList(block));
        }
    }
}