package ui.windows.piano.roll;

import java.awt.*;

import javax.swing.*;

import model.BlockPlayer;
import model.TimelineController;
import ui.common.RulerDimensionHelper;
import ui.windows.piano.roll.editor.PianoRollNoteDisplay;
import ui.windows.piano.roll.editor.PianoRollEditorPane;
import ui.windows.piano.roll.ruler.PianoRollRulerScrollPane;
import ui.windows.timeline.TimelineViewPanel;
import ui.windows.timeline.midi.TrackLabelPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    private final JPanel topHorizontalContainer;
    private final JPanel bottomHorizontalContainer;

    private final PianoRollRulerScrollPane pianoRollRulerScrollPane;
    private final PianoRollNoteDisplay pianoRollNoteDisplay;
    private final PianoRollEditorPane pianoRollNoteGrid;

    public PianoRollViewPanel(TimelineController timelineController, BlockPlayer blockPlayer) {
        bottomHorizontalContainer = new JPanel();
        topHorizontalContainer = new JPanel();
        pianoRollRulerScrollPane = new PianoRollRulerScrollPane(timelineController, blockPlayer);
        pianoRollNoteDisplay = new PianoRollNoteDisplay();
        pianoRollNoteGrid = new PianoRollEditorPane(blockPlayer, timelineController);

        initTopHorizontalContainer();
        initBottomHorizontalContainer();

        updateRulerDimensions();

        this.setBorder(TrackLabelPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);

        this.setLayout(new BorderLayout());
        this.add(topHorizontalContainer, BorderLayout.NORTH);
        this.add(bottomHorizontalContainer, BorderLayout.CENTER);
    }

    private void updateRulerDimensions() {
        RulerDimensionHelper.updateRulerDimensions(pianoRollNoteGrid, pianoRollRulerScrollPane);
    }

    private void initTopHorizontalContainer() {
        topHorizontalContainer.setLayout(new BoxLayout(topHorizontalContainer, BoxLayout.X_AXIS));
        topHorizontalContainer.setAlignmentX(LEFT_ALIGNMENT);
        topHorizontalContainer.add(TimelineViewPanel.getFillerPanel());
        topHorizontalContainer.add(pianoRollRulerScrollPane);
    }

    private void initBottomHorizontalContainer() {
        bottomHorizontalContainer.setBackground(Color.BLACK);
        bottomHorizontalContainer.setLayout(new BoxLayout(bottomHorizontalContainer, BoxLayout.X_AXIS));
        bottomHorizontalContainer.setAlignmentX(LEFT_ALIGNMENT);
        bottomHorizontalContainer.add(pianoRollNoteDisplay);
        bottomHorizontalContainer.add(pianoRollNoteGrid);

        TimelineViewPanel.syncScrollBars(pianoRollNoteGrid.getHorizontalScrollBar(),
                pianoRollRulerScrollPane.getHorizontalScrollBar());
        TimelineViewPanel.syncScrollBars(pianoRollNoteGrid.getVerticalScrollBar(),
                pianoRollNoteDisplay.getVerticalScrollBar());
    }
}
