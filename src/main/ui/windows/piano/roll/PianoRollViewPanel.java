package ui.windows.piano.roll;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import model.Block;
import model.BlockPlayer;
import model.MidiTrack;
import model.TimelineController;
import ui.windows.piano.roll.ruler.PianoRollRulerScrollPane;
import ui.windows.timeline.midi.TrackPanel;

// JPanel tab that lets the user create notes via graphical interface
public class PianoRollViewPanel extends JPanel {

    private PianoRollRulerScrollPane rulerScrollPane;
    private JPanel editorContainer;
    private PianoRollKeysScrollPane pianoRollKeysScrollPane;
    private PianoRollNoteGrid pianoRollNoteGrid;

    private BlockPlayer blockPlayer;

    public PianoRollViewPanel(TimelineController timelineController, MidiTrack parentMidiTrack, Block block) {
        blockPlayer = new BlockPlayer(block, parentMidiTrack, timelineController.getTimeline().getPlayer().getBPM());
        
        rulerScrollPane = new PianoRollRulerScrollPane(timelineController, parentMidiTrack, blockPlayer);
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
