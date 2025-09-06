package ui.windows.timeline.ruler;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.TimelineController;
import ui.common.RulerDimensionHelper;
import ui.ruler.RulerScrollPane;

// Panel that shows the tick marks above timeline to indicate beat marks and other timely information
public class TimelineRulerScrollPane extends RulerScrollPane implements RulerDimensionHelper.RulerWidthUpdater {

    private final TimelineRulerRenderPanel renderContainer;

    // EFFECTS: Constructs the pane, setting dimensions and appropriate listeners and viewports
    public TimelineRulerScrollPane(TimelineController timelineController) {
        renderContainer = new TimelineRulerRenderPanel(timelineController);
        renderContainer.setLayout(new BoxLayout(renderContainer, BoxLayout.X_AXIS));
        this.setViewportView(renderContainer);
    }

    // EFFECTS: adjusts the width of the ruler to match the MidiTrackPanel rows
    @Override
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            renderContainer.setPreferredSize(new Dimension(width, RulerScrollPane.RULER_HEIGHT));
            renderContainer.revalidate();
        });
    }
}
