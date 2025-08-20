package ui.windows.piano.roll.editor;

import java.awt.Color;

import javax.swing.*;

import model.BlockPlayer;
import model.TimelineController;
import ui.ruler.BlankScrollPane;

public class PianoRollNoteGridPane extends BlankScrollPane {

    private final PianoRollNoteGridEditor pianoRollNoteGridEditor;

    public PianoRollNoteGridPane(BlockPlayer blockPlayer, TimelineController timelineController) {
        pianoRollNoteGridEditor = new PianoRollNoteGridEditor(blockPlayer, timelineController);

        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        setViewportView(pianoRollNoteGridEditor);
        setBackground(Color.BLUE);
    }
}
