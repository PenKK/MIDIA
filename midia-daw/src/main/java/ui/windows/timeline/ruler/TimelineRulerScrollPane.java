package ui.windows.timeline.ruler;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;

import model.TimelineController;
import ui.common.RulerDimensionHelper;
import ui.ruler.RulerScrollPane;

/**
 * Scroll pane hosting the timeline's top ruler, used to display beat and measure marks.
 * Implements a width updater so the ruler matches the track content width.
 */
public class TimelineRulerScrollPane extends RulerScrollPane implements RulerDimensionHelper.RulerWidthUpdater {

    private final TimelineRulerRenderPanel renderContainer;

    /**
     * Constructs the ruler pane and embeds the render component.
     */
    public TimelineRulerScrollPane(TimelineController timelineController) {
        renderContainer = new TimelineRulerRenderPanel(timelineController);
        renderContainer.setLayout(new BoxLayout(renderContainer, BoxLayout.X_AXIS));
        this.setViewportView(renderContainer);
    }

    /**
     * Adjusts the ruler width to match the content area.
     */
    @Override
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            renderContainer.setPreferredSize(new Dimension(width, RulerScrollPane.RULER_HEIGHT));
            renderContainer.revalidate();
        });
    }
}
