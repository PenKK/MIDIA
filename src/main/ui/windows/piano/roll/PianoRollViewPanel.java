package ui.windows.piano.roll;


import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.TimelineController;
import ui.ruler.RulerScrollPane;
import ui.windows.timeline.midi.TrackPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    RulerScrollPane rulerScrollPane;
    PianoRollKeysScrollPane pianoRollKeysScrollPane;
    JPanel editorContainer;

    public PianoRollViewPanel(TimelineController timelineController) {
        rulerScrollPane = new RulerScrollPane(timelineController);
        pianoRollKeysScrollPane = new PianoRollKeysScrollPane();
        editorContainer = new JPanel();
        editorContainer.setBackground(Color.BLACK);
        editorContainer.setLayout(new BoxLayout(editorContainer, BoxLayout.X_AXIS));
        editorContainer.add(pianoRollKeysScrollPane);


        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(TrackPanel.BORDER);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.add(rulerScrollPane);
        this.add(editorContainer);
    }
}
