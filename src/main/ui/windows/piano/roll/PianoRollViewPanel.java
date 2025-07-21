package ui.windows.piano.roll;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.TimelineController;
import ui.windows.piano.roll.ruler.PianoRollRulerScrollPane;
import ui.windows.timeline.midi.TrackPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    PianoRollRulerScrollPane rulerScrollPane;
    JPanel editorContainer;
    PianoRollKeysScrollPane pianoRollKeysScrollPane;
    PianoRollNoteGrid pianoRollNoteGrid;

    public PianoRollViewPanel(TimelineController timelineController) {
        rulerScrollPane = new PianoRollRulerScrollPane();
        editorContainer = new JPanel();
        pianoRollKeysScrollPane = new PianoRollKeysScrollPane();
        pianoRollNoteGrid = new PianoRollNoteGrid();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);

        editorContainer.setBackground(Color.BLACK);
        editorContainer.setLayout(new BoxLayout(editorContainer, BoxLayout.X_AXIS));
        editorContainer.setAlignmentX(LEFT_ALIGNMENT);
        editorContainer.add(pianoRollKeysScrollPane);
        editorContainer.add(pianoRollNoteGrid);

        this.add(rulerScrollPane);
        this.add(editorContainer);
    }
}
