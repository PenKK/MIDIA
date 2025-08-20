package ui.windows.piano.roll;

import java.awt.Color;

import javax.swing.*;

import model.Block;
import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.windows.piano.roll.editor.PianoRollNoteDisplay;
import ui.windows.piano.roll.editor.PianoRollNoteGridPane;
import ui.windows.piano.roll.ruler.PianoRollRulerScrollPane;
import ui.windows.timeline.TimelineViewPanel;
import ui.windows.timeline.midi.TrackLabelPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    private final JPanel topHorizontalContainer;
    private final JPanel bottomHorizontalContainer;

    private final PianoRollRulerScrollPane rulerScrollPane;
    private final PianoRollNoteDisplay pianoRollNoteDisplay;
    private final PianoRollNoteGridPane pianoRollNoteGrid;

    private final BlockPlayer blockPlayer;

    public PianoRollViewPanel(TimelineController timelineController, MidiTrack parentMidiTrack, Block block) {
        blockPlayer = new BlockPlayer(block, parentMidiTrack, timelineController.getTimeline().getPlayer().getBPM());

        bottomHorizontalContainer = new JPanel();
        topHorizontalContainer = new JPanel();
        rulerScrollPane = new PianoRollRulerScrollPane(timelineController, blockPlayer);
        pianoRollNoteDisplay = new PianoRollNoteDisplay();
        pianoRollNoteGrid = new PianoRollNoteGridPane(blockPlayer, timelineController);

        initTopHorizontalContainer();
        initBottomHorizontalContainer();
        TimelineViewPanel.syncScrollBars(pianoRollNoteDisplay.getHorizontalScrollBar(),
                                                   pianoRollNoteDisplay.getHorizontalScrollBar());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackLabelPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);

        this.add(topHorizontalContainer);
        this.add(bottomHorizontalContainer);
    }

    private void initTopHorizontalContainer() {
        topHorizontalContainer.setLayout(new BoxLayout(topHorizontalContainer, BoxLayout.X_AXIS));
        topHorizontalContainer.setAlignmentX(LEFT_ALIGNMENT);
        topHorizontalContainer.add(TimelineViewPanel.getFillerPanel());
        topHorizontalContainer.add(rulerScrollPane);
    }

    private void initBottomHorizontalContainer() {
        bottomHorizontalContainer.setBackground(Color.BLACK);
        bottomHorizontalContainer.setLayout(new BoxLayout(bottomHorizontalContainer, BoxLayout.X_AXIS));
        bottomHorizontalContainer.setAlignmentX(LEFT_ALIGNMENT);
        bottomHorizontalContainer.add(pianoRollNoteDisplay);
        bottomHorizontalContainer.add(pianoRollNoteGrid);
    }
}
