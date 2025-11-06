package ui.common;

/**
 * Utility for keeping a ruler view width in sync with its content area.
 * Applies padding so the ruler extends beyond the content width slightly.
 */
public class RulerDimensionHelper {
    private static final int PADDING = 200;

    /**
     * Updates the ruler width based on the content width plus padding.
     *
     * @param content the content whose width should be mirrored by the ruler
     * @param ruler   the ruler to update
     */
    public static void updateRulerDimensions(ContainerWidthProvider content, RulerWidthUpdater ruler) {
        int contentWidth = content.getContainerWidth();
        ruler.updateWidth(contentWidth + PADDING);
    }

    /**
     * Provides the current container width, used to size a ruler.
     */
    public interface ContainerWidthProvider {
        /**
         * Returns the current width of the container in pixels.
         *
         * @return the container width
         */
        int getContainerWidth();
    }

    /**
     * Updates a ruler's width to match its content.
     */
    public interface RulerWidthUpdater {
        /**
         * Updates the ruler width.
         *
         * @param width the new width in pixels
         */
        void updateWidth(int width);
    }
}
