package ui.windows.piano.roll.ruler;

import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import model.PianoRollPlayer;
import model.TimelineController;
import ui.ruler.RulerMouseAdapter;
import ui.ruler.RulerRenderPanel;

import javax.swing.*;

/**
 * Ruler render panel for the piano roll view.
 * Draws measure, beat, and division tick marks and allows position scrubbing.
 */
public class PianoRollRulerRenderPanel extends RulerRenderPanel implements PropertyChangeListener {

    private final TimelineController timelineController;
    private final PianoRollPlayer pianoRollPlayer;

    /**
     * Creates a piano roll ruler render panel and wires mouse interaction for scrubbing.
     *
     * @param timelineController the controller providing timeline state
     * @param pianoRollPlayer        the player used in the piano roll context
     */
    public PianoRollRulerRenderPanel(TimelineController timelineController, PianoRollPlayer pianoRollPlayer) {
        super();
        this.timelineController = timelineController;
        this.pianoRollPlayer = pianoRollPlayer;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        pianoRollPlayer.addPropertyChangeListener(this);
        RulerMouseAdapter mouseAdapter = new RulerMouseAdapter(timelineController, pianoRollPlayer);
        addMouseAdapter(mouseAdapter);
    }

    /**
     * Paints the ruler tick marks according to the current timeline settings.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawAllTickMarks(g, timelineController, getWidth(), pianoRollPlayer.getBeatDivision(),
                pianoRollPlayer.getBeatsPerMeasure());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        System.out.println("called " + propertyName);
        if (propertyName.equals("beatsPerMeasure") || propertyName.equals("beatDivision")) {

            repaint();
        }
    }
}
