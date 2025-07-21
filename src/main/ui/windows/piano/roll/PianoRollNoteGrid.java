package ui.windows.piano.roll;

import java.awt.Color;

import javax.swing.JPanel;

import ui.ruler.BlankScrollPane;

public class PianoRollNoteGrid extends BlankScrollPane {

    JPanel gridContainer;

    PianoRollNoteGrid() {
        gridContainer = new JPanel();

        setViewportView(gridContainer);
        setBackground(Color.BLUE);
    }
}
