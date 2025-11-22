package ui.media;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import model.TimelineController;

/**
 * Panel containing transport controls and timeline scaling UI.
 * Provides play/pause, horizontal scale adjustment, BPM display and live time readout.
 */
public class MediaControlPanel extends JPanel implements ActionListener, ChangeListener, PropertyChangeListener {

    public static final double MAX_HORIZONTAL_SCALE = 5;
    public static final double MIN_HORIZONTAL_SCALE = 0.3;

    private final TimelineController timelineController;

    private ImageIcon playImage = null;
    private ImageIcon pauseImage = null;

    private JPanel leftAlignPanel;
    private JPanel rightAlignPanel;

    private JLabel timeLabel;
    private JButton playButton;
    private JSlider scaleSlider;
    private JLabel bpmDisplay;

    /**
     * Constructs the media control panel, initializes icons and components, and subscribes to timeline updates.
     *
     * @param timelineController the controller used to interact with playback and timeline state
     */
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

        scaleSlider = new JSlider();
        scaleSlider.addChangeListener(this);
        scaleSlider.setPreferredSize(new Dimension(100, scaleSlider.getPreferredSize().height));
        updateScaleSliderValue();

        playButton = new JButton();
        playButton.addActionListener(this);

        createTimeLabel();
        createBpmLabel();

        try {
            playImage = getImageIcon("/images/play.png");
            pauseImage = getImageIcon("/images/pause.png");
        } catch (Exception e) {
            System.out.println("Unable to load media icons: " + e.getMessage());
        }

        playButton.setIcon(playImage);
        leftAlignPanel.add(scaleSlider);
        rightAlignPanel.add(bpmDisplay);
        rightAlignPanel.add(playButton);
        rightAlignPanel.add(timeLabel);
    }

    /**
     * Initializes the BPM label with the current tempo and attaches drag-to-adjust behavior.
     */
    private void createBpmLabel() {
        float bpm = timelineController.getTimeline().getPlayer().getBPM();
        ImageIcon quaverIcon = getImageIcon("/images/quaver.png");

        bpmDisplay = new JLabel(String.format("%.2f", bpm), quaverIcon, JLabel.RIGHT);
        bpmDisplay.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        bpmDisplay.putClientProperty("FlatLaf.style", "font: bold 14 Monospaced; background:rgb(77, 77, 77); arc: 6;");
        bpmDisplay.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));

        MouseInputAdapter bpmMouseAdapter = new BpmMouseInputAdapter(timelineController, bpmDisplay);
        bpmDisplay.addMouseListener(bpmMouseAdapter);
        bpmDisplay.addMouseMotionListener(bpmMouseAdapter);
    }

    private void createTimeLabel() {
        timeLabel = new JLabel("00:00.00");
        timeLabel.setOpaque(true);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        timeLabel.putClientProperty("FlatLaf.style", "font: bold 14; background:rgb(77, 77, 77); arc: 6;");
    }

    /**
     * Toggles timeline playback and updates the play/pause icon.
     */
    public void togglePlay() {
        if (timelineController.isPlaying()) {
            timelineController.pauseTimeline();
            playButton.setIcon(playImage);
            return;
        }

        timelineController.playTimeline();
        playButton.setIcon(pauseImage);
    }

    /**
     * Synchronizes the scale slider value with the timeline's current horizontal scale.
     */
    private void updateScaleSliderValue() {
        int value = (int) (100 * timelineController.getTimeline().getHorizontalScaleFactor()
                / MAX_HORIZONTAL_SCALE);
        scaleSlider.setValue(value);
    }

    /**
     * Loads and scales an icon image from the classpath.
     *
     * @param path the resource path to the image
     * @return the loaded ImageIcon, or null if it could not be loaded
     */
    private ImageIcon   getImageIcon(String path) {
        try {
            return new ImageIcon(
                    ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)))
                            .getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.out.println("Couldn't load image at " + path);
            return null;
        }
    }

    /**
     * Applies the scale slider value to the timeline's horizontal scale (clamped to a safe range).
     */
    private void updateScale() {
        double factor = (double) scaleSlider.getValue() / 100;

        double scale = Math.max(factor * MAX_HORIZONTAL_SCALE, MIN_HORIZONTAL_SCALE);
        timelineController.getTimeline().setHorizontalScaleFactor(scale);
    }

    /**
     * Handles end-of-playback UI: resets the play icon and stops the periodic updater.
     */
    private void handleEnd() {
        playButton.setIcon(playImage);
        timelineController.getTimeline().getPlayer().getPlaybackUpdaterTimer().stop();
    }

    /**
     * Updates the on-screen time display to reflect the current playback position.
     */
    private void updateTimeDisplay() {
        double ms = timelineController.getTimeline().getPlayer().getPositionMs();
        double sec = ms / 1000;
        int mm = (int) (sec / 60);
        double ss = sec % 60;

        String display = String.format("%02d:%05.2f", mm, ss);
        timeLabel.setText(display);
    }

    /**
     * Resets transport UI elements to reflect a stopped state.
     */
    private void reset() {
        handleEnd();
        updateTimeDisplay();
    }

    /**
     * Handles UI actions (e.g., play/pause button).
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(playButton)) {
            togglePlay();
        }
    }

    /**
     * Applies changes from controls such as the scale slider.
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        Object source = e.getSource();
        if (source.equals(scaleSlider)) {
            updateScale();
        }
    }

    private void updateBpmValue() {
        bpmDisplay.setText(String.format("%6.2f", timelineController.getTimeline().getPlayer().getBPM()));
    }

    private void updateMediaValues() {
        updateScaleSliderValue();
        updateBpmValue();
    }

    /**
     * Reacts to timeline property changes by updating transport UI elements.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case "timelineReplaced":
                updateMediaValues();
                reset();
                break;
            case "playbackEnded":
                handleEnd();
                break;
            case "tickPosition":
                updateTimeDisplay();
                break;
            default:
                break;
        }
    }
}
