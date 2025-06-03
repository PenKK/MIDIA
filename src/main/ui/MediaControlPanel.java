package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Timeline;
import model.TimelineController;

// Panel containing UI elements related to playback
public class MediaControlPanel extends JPanel implements ActionListener, ChangeListener, PropertyChangeListener {

    public static final int UI_UPDATE_DELAY = 10;

    private TimelineController timelineController;
    private JButton playButton;
    private ImageIcon playImage = null;
    private ImageIcon pauseImage = null;
    private Timer playbackUpdateTimer;
    private JSlider scaleSlider;
    private JPanel leftAlignPanel;
    private JPanel rightAlignPanel;
    private JLabel timeLabel;

    // EFFECTS: creates a MediaControlPanel with timers and initializes image icons and components
    public MediaControlPanel(TimelineController timelineController) {
        this.timelineController = timelineController;
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        timelineController.addObserver(this);
        initFields();

        this.add(leftAlignPanel);
        this.add(rightAlignPanel);
    }

    // MODIFIES: this
    // EFFECTS: initializes media panel fields
    private void initFields() {
        leftAlignPanel = new JPanel();
        rightAlignPanel = new JPanel();

        leftAlignPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        rightAlignPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        playbackUpdateTimer = new Timer(UI_UPDATE_DELAY, this);

        scaleSlider = new JSlider();
        scaleSlider.addChangeListener(this);
        scaleSlider.setPreferredSize(new Dimension(100, scaleSlider.getPreferredSize().height));
        copyTimelineScaleValue();

        playButton = new JButton();
        playButton.addActionListener(this);

        createTimeLabel();

        try {
            playImage = getImageIcon("/resources/images/play.png");
            pauseImage = getImageIcon("/resources/images/pause.png");
        } catch (Exception e) {
            System.out.println("Unable to load media icons: " + e.getMessage());
        }

        playButton.setIcon(playImage);
        leftAlignPanel.add(scaleSlider);
        rightAlignPanel.add(playButton);
        rightAlignPanel.add(timeLabel);
    }

    private void createTimeLabel() {
        timeLabel = new JLabel("00:00.00");
        timeLabel.setOpaque(true);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        timeLabel.putClientProperty("FlatLaf.style", "font: bold 14; background:rgb(77, 77, 77); arc: 6;");
    }

    // EFFECTS: toggles playback
    public void togglePlay() {
        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            playbackUpdateTimer.stop();
            playButton.setIcon(playImage);
            return;
        }

        timelineController.playTimeline();
        playbackUpdateTimer.start();
        playButton.setIcon(pauseImage);
    }

    // MODIFIES: this
    // EFFECTS: sets the render sliders value to that of the timeline render scale
    private void copyTimelineScaleValue() {
        int value = (int) (100 * timelineController.getTimeline().getHorizontalScaleFactor()
                / Timeline.MAX_HORIZONTAL_SCALE);
        scaleSlider.setValue(value);
    }

    // EFFECTS: returns the ImageIcon representation of the image at path
    private ImageIcon getImageIcon(String path) {
        try {
            return new ImageIcon(
                    ImageIO.read(getClass().getResource(path)).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.out.println("Couldnt load image at " + path);
            return null;
        }
    }

    // MODIFIES: timeline singleton
    // EFFECTS: updates render scale according to slider
    private void updateScale() {
        double factor = (double) scaleSlider.getValue() / 100;

        double scale = Math.max(factor * Timeline.MAX_HORIZONTAL_SCALE, Timeline.MIN_HORIZONTAL_SCALE);
        timelineController.getTimeline().setHorizontalScaleFactor(scale);
    }

    // MODIFIES: this
    // EFFECTS: handles behavior for when song ends: changes play icon, stops tickUpdateTimer, brings position to 0
    private void handleEnd() {
        playButton.setIcon(playImage);
        playbackUpdateTimer.stop();
    }

    // MODIFIES: this
    // EFFECTS: triggers an update to the positionTick field in the instance (and hence fires propertyChangeEvent)
    private void updateTimelineTick() {
        if (timelineController.isDraggingRuler()) {
            return;
        }
        timelineController.getTimeline().getPlayer().updatePositionTick();
    }

    // MODIFIES: this
    // EFFECTS: updates the UI time display using the timeline player
    private void updateTimeDisplay() {
        double ms = timelineController.getTimeline().getPlayer().getPositionMs();
        double sec = ms / 1000;
        int mm = (int) (sec / 60);
        double ss = sec % 60;

        String display = String.format("%02d:%05.2f", mm, ss);
        timeLabel.setText(display);
    }

    // MODIFIES: this
    // EFFECTS: resets icon and other media related states such as playback
    private void reset() {
        handleEnd();
        updateTimeDisplay();
    }

    // EFFECTS: listens for timer and button actions and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(playButton)) {
            togglePlay();
        } else if (source.equals(playbackUpdateTimer)) {
            updateTimelineTick();
            updateTimeDisplay();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source.equals(scaleSlider)) {
            updateScale();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case "timelineReplaced":
                copyTimelineScaleValue();
                reset();
                break;
            case "playbackEnded":
                handleEnd();
                break;
            case "positionTick":
                updateTimeDisplay();
                break;
            default:
                break;
        }
    }
}
