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
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import model.Timeline;

public class MediaControlPanel extends JPanel implements ActionListener {
    private JButton play;
    private ImageIcon playImage = null;
    private ImageIcon pauseImage = null;
    private Timer timer;

    public MediaControlPanel() {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        timer = new Timer(0, this);
        timer.setRepeats(false);

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

    public void play() {
        Timeline timeline = Timeline.getInstance();
        
        if (timeline.getSequencer().isRunning()) {
            timeline.pause();
            timer.stop();
            showIcon(playImage);
            return;
        }

        try {
            int delay = (int) (timeline.getLengthMs() - timeline.getPositionMs());
            timer.setInitialDelay(delay);
            timer.setDelay(delay);
            timer.start();
            timeline.play();
            showIcon(pauseImage);
        } catch (InvalidMidiDataException e) {
            System.out.println("Invalid midi data found, unable to start playback");
            e.printStackTrace();
        }
    }

    private ImageIcon getImageIcon(String path) {
        try {
            return new ImageIcon(ImageIO.read(new File(path)).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            System.out.println("Couldnt load image at " + path);
            return null;
        }
    }

    private void showIcon(ImageIcon icon) {
        play.setIcon(icon);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(play)) {
            play();
        } else if (source.equals(timer)) {
            handleEnd();
        }
    }

    private void handleEnd() {
        showIcon(playImage);
        Timeline.getInstance().setPositionTick(0);
    }
}
