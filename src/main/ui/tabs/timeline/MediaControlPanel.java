package ui.tabs.timeline;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import model.Timeline;

// Panel containing UI elements related to playback
public class MediaControlPanel extends JPanel implements ActionListener {
    private JButton play;
    private ImageIcon playImage = null;
    private ImageIcon pauseImage = null;
    private Timer pausePlayTimer;
    private Timer tickUpdateTimer;

    private static final int POSITION_LINE_UPDATE_DELAY = 10;

    // EFFECTS: creates a MediaControlPanel with timers and initializes image icons and components
    public MediaControlPanel() {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pausePlayTimer = new Timer(0, this);
        tickUpdateTimer = new Timer(POSITION_LINE_UPDATE_DELAY, this);
        pausePlayTimer.setRepeats(false);

        try {
            playImage = getImageIcon("lib/images/play.png");
            pauseImage = getImageIcon("lib/images/pause.png");
        } catch (Exception e) {
            System.out.println("Unable to load media icons");
            e.printStackTrace();
        }

        play = new JButton();
        play.addActionListener(this);
        
        showIcon(playImage);
        this.add(play);
    }

    // EFFECTS: toggles playback
    public void togglePlay() {
        Timeline timeline = Timeline.getInstance();
        
        if (timeline.getSequencer().isRunning()) {
            timeline.pause();
            pausePlayTimer.stop();
            tickUpdateTimer.stop();
            showIcon(playImage);
            return;
        }

        try {
            int delay = (int) (timeline.getLengthMs() - timeline.getPositionMs());
            pausePlayTimer.setInitialDelay(delay);
            pausePlayTimer.setDelay(delay);
            timeline.play();
            pausePlayTimer.start();
            tickUpdateTimer.start();
            showIcon(pauseImage);
        } catch (InvalidMidiDataException e) {
            System.out.println("Invalid midi data found, unable to start playback");
            e.printStackTrace();
        }
    }

    // EFFECTS: returns the ImageIcon representation of the image at path
    private ImageIcon getImageIcon(String path) {
        try {
            return new ImageIcon(ImageIO.read(new File(path)).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.out.println("Couldnt load image at " + path);
            return null;
        }
    }

    // MODIFES: this
    // EFFECTS: replaces play button icon with the specified icon
    private void showIcon(ImageIcon icon) {
        play.setIcon(icon);
    }

    // EFFECTS: listens for timer and button actions and runs methods accordingly
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(play)) {
            togglePlay();
        } else if (source.equals(pausePlayTimer)) {
            handleEnd();
        } else if (source.equals(tickUpdateTimer)) {
            updateTimelineTick();
        }
    }

    // MODIFIES: this, timeline singleton
    // EFFECTS: handles behavior for when song ends: changes play icon, stops tickUpdateTimer, brings position to 0
    private void handleEnd() {
        showIcon(playImage);
        Timeline.getInstance().setPositionTick(0);
        tickUpdateTimer.stop();
    }

    // MODIFIES: timeline singleton
    // EFFECTS: triggers an update to the positionTick field in the instance (and hence fires propertyChangeEvent)
    private void updateTimelineTick() {
        Timeline.getInstance().updatePositionTick();
    }
}
