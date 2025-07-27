package ui.windows.timeline.ruler;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.TimelineController;
import ui.ruler.RulerScrollPane;

// Panel that shows the tick marks above timeline to indicate beat marks and other timely infomration
public class TimelineRulerScrollPane extends RulerScrollPane {

    private final TimelineRulerRenderPanel renderContainer;

    // EFFECTS: Constructs the pane, setting dimensions and appropirate listeners and viewports
    public TimelineRulerScrollPane(TimelineController timelineController) {
        renderContainer = new TimelineRulerRenderPanel(timelineController);
        renderContainer.setLayout(new BoxLayout(renderContainer, BoxLayout.X_AXIS));
        this.setViewportView(renderContainer);
    }

    // EFFECTS: adjusts the width of the ruler to match the MidiTrackPanel rows
    public void updateWidth(int width) {
        SwingUtilities.invokeLater(() -> {
            int widthScrollBoxAdjusted = width + (int) UIManager.get("ScrollBar.width");
            renderContainer.setPreferredSize(new Dimension(widthScrollBoxAdjusted, RulerScrollPane.RULER_HEIGHT));
            renderContainer.revalidate();
        });
    }
}
