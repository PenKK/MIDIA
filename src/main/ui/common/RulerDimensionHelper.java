package ui.common;

public class RulerDimensionHelper {
    private static final int PADDING = 200;

    public static void updateRulerDimensions(ContainerWidthProvider content, RulerWidthUpdater ruler) {
        int contentWidth = content.getContainerWidth();
        ruler.updateWidth(contentWidth + PADDING);
    }

    public interface ContainerWidthProvider {
        int getContainerWidth();
    }

    public interface RulerWidthUpdater {
        void updateWidth(int width);
    }
}
