package ui.windows.piano.roll.editor;

import java.awt.Color;

import javax.swing.*;

import model.PianoRollPlayer;
import model.TimelineController;
import ui.common.BlankScrollPane;
import ui.common.LineContainerPanel;
import ui.common.RulerDimensionHelper;

/**
 * Scrollable container for the piano roll editor and its playhead overlay.
 */
public class PianoRollEditorPane extends BlankScrollPane implements RulerDimensionHelper.ContainerWidthProvider {

    private final PianoRollEditor pianoRollEditor;
    private final LineContainerPanel lineContainerPanel;

    /**
     * Constructs the editor pane, embedding the piano roll editor and playhead overlay panel.
     */
    public PianoRollEditorPane(PianoRollPlayer pianoRollPlayer, TimelineController timelineController) {
        pianoRollEditor = new PianoRollEditor(pianoRollPlayer, timelineController);
        lineContainerPanel = new PianoRollLineContainerPanel(timelineController, pianoRollPlayer);
        lineContainerPanel.add(pianoRollEditor);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setViewportView(lineContainerPanel);
        setBackground(Color.BLUE);
    }

    /**
     * Returns the preferred width of the editor container (for ruler sizing).
     */
    @Override
    public int getContainerWidth() {
        return lineContainerPanel.getPreferredSize().width;
    }
}
