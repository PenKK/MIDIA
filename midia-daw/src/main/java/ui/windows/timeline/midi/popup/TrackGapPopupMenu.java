package ui.windows.timeline.midi.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import model.TimelineController;
import model.editing.DawClipboard;
import ui.windows.timeline.midi.TrackRenderPanel;

/**
 * Context menu for actions in empty areas of a track lane (e.g., paste).
 */
public class TrackGapPopupMenu extends JPopupMenu implements ActionListener {

    private final TrackRenderPanel trackRenderPanel;
    private final TimelineController timelineController;
    private final DawClipboard dawClipboard;
    private final JMenuItem pasteMenuItem;
    private final long tick;

    /**
     * Creates a popup menu for track gaps and determines the timeline tick under the cursor.
     */
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

    /**
     * Pastes copied items into the track at the nearest lower division.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(pasteMenuItem)) {
            trackRenderPanel.getMidiTrack().paste(dawClipboard.getContents(), 
                                                  timelineController.getTimeline().getPlayer().snapTickLowerDivision(tick));
            timelineController.getPropertyChangeSupport().firePropertyChange("blockPasted", null, null);
        }
    }

}
