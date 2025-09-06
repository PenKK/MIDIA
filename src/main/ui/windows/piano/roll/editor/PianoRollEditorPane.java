package ui.windows.piano.roll.editor;

import java.awt.Color;

import javax.swing.*;

import model.BlockPlayer;
import model.TimelineController;
import ui.common.BlankScrollPane;
import ui.common.LineContainerPanel;
import ui.common.RulerDimensionHelper;

public class PianoRollEditorPane extends BlankScrollPane implements RulerDimensionHelper.ContainerWidthProvider {

    private final PianoRollEditor pianoRollEditor;
    private final LineContainerPanel lineContainerPanel;

    public PianoRollEditorPane(BlockPlayer blockPlayer, TimelineController timelineController) {
        pianoRollEditor = new PianoRollEditor(blockPlayer, timelineController);
        lineContainerPanel = new PianoRollLineContainerPanel(timelineController, blockPlayer);
        lineContainerPanel.add(pianoRollEditor);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setViewportView(lineContainerPanel);
        setBackground(Color.BLUE);
    }

    @Override
    public int getContainerWidth() {
        return lineContainerPanel.getPreferredSize().width;
    }
}
