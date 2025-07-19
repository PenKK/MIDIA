package ui.windows.timeline.midi.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.windows.timeline.midi.TrackRenderPanel;

public class TrackGapPopupMenu extends JPopupMenu implements ActionListener {

    private TrackRenderPanel trackRenderPanel;
    private TimelineController timelineController;
    private DawClipboard dawClipboard;
    private JMenuItem pasteMenuItem;
    private long tick;

    public TrackGapPopupMenu(TrackRenderPanel trackRenderPanel, TimelineController timelineController, 
                             DawClipboard dawClipboard, int mouseXPosition) {
        this.trackRenderPanel = trackRenderPanel;
        this.timelineController = timelineController;
        this.dawClipboard = dawClipboard;
        this.tick = timelineController.getTimeline().scalePixelToTick(mouseXPosition);

        pasteMenuItem = new JMenuItem("Paste");
        pasteMenuItem.addActionListener(this);
        
        this.add(pasteMenuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(pasteMenuItem)) {
            trackRenderPanel.getMidiTrack().paste(dawClipboard.getContents(), 
                                                  timelineController.getTimeline().snapTickLower(tick));
            timelineController.refreshTrackLayout();
        }
    }

}
