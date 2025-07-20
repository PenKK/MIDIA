package ui.ruler;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.TimelineController;
import ui.windows.timeline.midi.TrackPanel;

// Panel that shows the tickmarks above timeline to indicate beat marks and other timely infomration
public class RulerScrollPane extends BlankScrollPane {

    public static final int BEAT_WIDTH = 25;
    public static final int RULER_HEIGHT = TrackPanel.HEIGHT / 4;
    public static final int DEFAULT_RULER_WIDTH = 800;

    private RulerRenderPanel renderContainer;

    // EFFECTS: Constructs the pane, setting dimensions and appropirate listeners and viewports
    public RulerScrollPane(TimelineController timelineController) {
        renderContainer = new RulerRenderPanel(timelineController);
        renderContainer.setLayout(new BoxLayout(renderContainer, BoxLayout.X_AXIS));

        this.setPreferredSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));
        this.setMaximumSize(new Dimension(Integer.MAX_VALUE, RULER_HEIGHT));
        this.setMinimumSize(new Dimension(DEFAULT_RULER_WIDTH, RULER_HEIGHT));

        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setViewportView(renderContainer);
    }

    // EFFECTS: adjusts the width of the ruler to match the MidiTrackPanel rows
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            int widthScrollBoxAdjusted = width + (int) UIManager.get("ScrollBar.width");
            renderContainer.setPreferredSize(new Dimension(widthScrollBoxAdjusted, RULER_HEIGHT));
            renderContainer.revalidate();
        });
    }

    public RulerRenderPanel getRenderPanel() {
        return renderContainer;
    }
}
