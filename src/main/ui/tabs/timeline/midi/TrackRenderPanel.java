package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Timeline;
import model.TimelineController;

// Interactable render of the MidiTrack's blocks and notes
public class TrackRenderPanel extends JPanel {

    private static final Color NOTE_COLOR = Color.WHITE;
    private static final Color BLOCK_BACKGROUND_COLOR = new Color(30, 162, 240, 200);

    private static final int CORNER_ROUNDING_BLOCK = 2;
    private static final int BLOCK_HEIGHT_MARGIN = 6;
    private static final int EMPTY_BLOCK_WIDTH = 100;

    private static final int MIN_NOTE_RANGE = 16;
    private static final int NOTE_RANGE_PADDING = 2;
    private static final double NOTE_CORNER_RADIUS = 0.3;

    private MidiTrack midiTrack;
    private TimelineController timelineController;

    // EFFECTS: recieves the specified midiTrack, and listens for mouse events
    public TrackRenderPanel(MidiTrack midiTrack, TimelineController timelineController) {
        this.timelineController = timelineController;
        this.midiTrack = midiTrack;
        this.addMouseListener(mouseAdapter());
    }

    // EFFECTS: Draws all blocks in the midiTrack
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawTrack(midiTrack.getBlocks(), g);
    }

    // EFFECTS: draws the specified block with scaling.
    private void drawBlock(Block block, Graphics g) {
        Color tempColor = g.getColor();
        g.setColor(BLOCK_BACKGROUND_COLOR);

        Timeline timeline = timelineController.getTimeline();

        int width = (int) timeline.scalePixelsRender(Math.max(block.getDurationTicks(), EMPTY_BLOCK_WIDTH));
        int x = (int) timeline.scalePixelsRender(block.getStartTick());
        int y = BLOCK_HEIGHT_MARGIN / 2;
        int height = TrackPanel.HEIGHT - BLOCK_HEIGHT_MARGIN;

        g.fillRoundRect(x, y, width, height, CORNER_ROUNDING_BLOCK, CORNER_ROUNDING_BLOCK);

        g.setColor(tempColor);
    }

    // EFFECTS: returns the pitch range of the notes within the blocks
    private int[] determineRange(ArrayList<Block> blocks) {
        if (blocks.isEmpty() || midiTrack.isPercussive()) {
            return new int[] { 0, 0 };
        }

        int minPitch = -1;
        int maxPitch = -1;

        for (Block b : blocks) {
            for (Note n : b.getNotes()) {
                if (minPitch == -1) {
                    minPitch = n.getPitch();
                }

                if (maxPitch == -1) {
                    maxPitch = n.getPitch();
                }

                int pitchN = n.getPitch();
                if (pitchN > maxPitch) {
                    maxPitch = pitchN;
                }

                if (pitchN < minPitch) {
                    minPitch = pitchN;
                }
            }
        }

        return new int[] { minPitch, maxPitch };
    }

    // MODIFIES: this
    // EFFECTS: draws the specified blocks and their notes. the height of notes is 
    //          drawn relative to all other notes in the blocks
    private void drawTrack(ArrayList<Block> blocks, Graphics g) {
        Timeline timeline = timelineController.getTimeline();
        int[] pitchRange = determineRange(blocks);

        int minPitch = pitchRange[0];
        int maxPitch = pitchRange[1];

        int trackHeight = TrackPanel.HEIGHT - BLOCK_HEIGHT_MARGIN;
        int range = Math.abs(minPitch - maxPitch);
        int rangeAdjusted = Math.max(range, MIN_NOTE_RANGE);
        double heightDouble = trackHeight / (double) (rangeAdjusted + NOTE_RANGE_PADDING);
        int noteRounding = (int) Math.round(heightDouble * NOTE_CORNER_RADIUS);

        for (Block b : blocks) {
            drawBlock(b, g);
            for (Note n : b.getNotesTimeline()) {
                int pitchOffset = minPitch - (range == 0 ? (MIN_NOTE_RANGE / 2) : 0);
                int relativePitch = n.getPitch() - pitchOffset + (NOTE_RANGE_PADDING / 2);

                int x = (int) timeline.scalePixelsRender(n.getStartTick());
                int y = trackHeight - (int) Math.round(relativePitch * heightDouble - BLOCK_HEIGHT_MARGIN / 2);
                int height = (int) Math.round(heightDouble);
                int width = (int) timeline.scalePixelsRender(n.getDurationTicks());

                g.setColor(NOTE_COLOR);
                g.fillRoundRect(x, y, width, height, noteRounding, noteRounding);
            }
        }
    }

    // EFFECTS: returns the last horizontal pixel that is drawn from blocks (scaled)
    public int getScaledWidth() {
        long endPixel = 0;
        for (Block b : midiTrack.getBlocks()) {
            long temp = b.getStartTick() + b.getDurationTicks();
            if (temp > endPixel) {
                endPixel = temp;
            }
        }
        return (int) timelineController.getTimeline().scalePixelsRender(endPixel);
    }

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            // EFFECTS: listens for mouse events and runs methods accoringly
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    rightClick(e);
                    return;
                }

                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    doubleClick(e);
                }
            }
        };
    }

    private void rightClick(MouseEvent e) {

    }

    // EFFECTS: Handles double click behavior on the rendered track
    private void doubleClick(MouseEvent e) {
        int tick = (int) Math.round(e.getX() / timelineController.getTimeline().getHorizontalScale());

        for (Block b : midiTrack.getBlocks()) {
            if (b.getStartTick() <= tick && b.getStartTick() + b.getDurationTicks() >= tick) {
                System.out.println("Block at startTick " + b.getStartTick() + " clicked!");
            }
        }
        repaint();
    }
}