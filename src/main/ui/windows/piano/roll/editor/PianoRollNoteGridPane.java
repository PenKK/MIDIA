package ui.windows.piano.roll.editor;

import java.awt.Color;

import javax.swing.*;

import model.BlockPlayer;
import model.TimelineController;
import ui.ruler.BlankScrollPane;
import ui.ruler.LineContainerPanel;

public class PianoRollNoteGridPane extends BlankScrollPane {

    private final PianoRollNoteGridEditor pianoRollNoteGridEditor;
    private final LineContainerPanel lineContainerPanel;

    public PianoRollNoteGridPane(BlockPlayer blockPlayer, TimelineController timelineController) {
        pianoRollNoteGridEditor = new PianoRollNoteGridEditor(blockPlayer, timelineController);
        lineContainerPanel = new PianoRollLineContainerPanel(timelineController, blockPlayer);
        lineContainerPanel.add(pianoRollNoteGridEditor);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setViewportView(lineContainerPanel);
        setBackground(Color.BLUE);
    }
}
