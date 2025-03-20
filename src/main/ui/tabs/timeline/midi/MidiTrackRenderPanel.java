package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import model.Block;
import model.MidiTrack;
import model.Note;
import ui.tabs.timeline.TimelineViewPanel;

// Interactable render of the MidiTrack's blocks and notes
public class MidiTrackRenderPanel extends JPanel implements MouseListener {

    private static final Color NOTE_COLOR = Color.WHITE;
    private static final Color BLOCK_BACKGROUND_COLOR = new Color(30, 162, 240, 200);

    private static final int CORNER_ROUNDING_BLOCK = 2;
    private static final int BLOCK_HEIGHT_MARGIN = 6;

    private static final int MIN_NOTE_RANGE = 16;
    private static final int     NOTE_RANGE_PADDING = 2;
    private static final double  NOTE_CORNER_RADIUS = 0.3;

    private MidiTrack midiTrack;

    // EFFECTS: recieves the specified midiTrack, and listens for mouse events
    public MidiTrackRenderPanel(MidiTrack midiTrack) {
        this.midiTrack = midiTrack;
        this.addMouseListener(this);
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

        int width = scalePixelsRender(Math.max(block.getDurationTicks(), 50));
        int x1 = scalePixelsRender(block.getStartTick());
        int y1 = BLOCK_HEIGHT_MARGIN / 2;
        int height = MidiTrackPanel.HEIGHT - BLOCK_HEIGHT_MARGIN;

        g.fillRoundRect(x1, y1, width, height, CORNER_ROUNDING_BLOCK, CORNER_ROUNDING_BLOCK);

        g.setColor(tempColor);
    }

    // EFFECTS: returns the pitch range of the notes within the given block
    private int[] determineRange(ArrayList<Block> blocks) {
        if (blocks.isEmpty() || midiTrack.isPercussive()) {
            return new int[] { 0, 0 };
        }
        int minPitch = blocks.get(0).getNotes().get(0).getPitch();
        int maxPitch = minPitch;

        for (Block b : blocks) {
            if (!midiTrack.isPercussive()) {
                for (Note n : b.getNotes()) {
                    int pitchN = n.getPitch();
                    if (pitchN > maxPitch) {
                        maxPitch = pitchN;
                    }

                    if (pitchN < minPitch) {
                        minPitch = pitchN;
                    }
                }
            }
        }

        return new int[] { minPitch, maxPitch };
    }

    // MODIFIES: this
    // EFFECTS: draws the specified blocks and their notes. the height of notes is 
    //          drawn relative to all other notes in the blocks
    private void drawTrack(ArrayList<Block> blocks, Graphics g) {
        int[] pitchRange = determineRange(blocks);

        int minPitch = pitchRange[0];
        int maxPitch = pitchRange[1];

        int trackHeight = MidiTrackPanel.HEIGHT - BLOCK_HEIGHT_MARGIN;
        int range = Math.abs(minPitch - maxPitch);
        int rangeAdjusted = Math.max(range, MIN_NOTE_RANGE);
        double heightDouble = trackHeight / (double) (rangeAdjusted + NOTE_RANGE_PADDING);
        int noteRounding = (int) Math.round(heightDouble * NOTE_CORNER_RADIUS);

        for (Block b : blocks) {
            drawBlock(b, g);
            for (Note n : b.getNotesTimeline()) {
                int pitchOffset = minPitch - (range == 0 ? (MIN_NOTE_RANGE / 2) : 0);
                int relativePitch = n.getPitch() - pitchOffset + (NOTE_RANGE_PADDING / 2);

                int x = scalePixelsRender(n.getStartTick());
                int y = trackHeight - (int) Math.round(relativePitch * heightDouble - BLOCK_HEIGHT_MARGIN / 2);
                int height = (int) Math.round(heightDouble);
                int width = scalePixelsRender(n.getDurationTicks());
                
                g.setColor(NOTE_COLOR);
                g.fillRoundRect(x, y, width, height, noteRounding, noteRounding);
            }
        }
    }

    // EFFECTS: returns the last horizontal pixel that is drawn from blocks (scaled)
    public int getScaledWidth() {
        int endPixel = 0;
        for (Block b : midiTrack.getBlocks()) {
            int temp = b.getStartTick() + b.getDurationTicks();
            if (temp > endPixel) {
                endPixel = temp;
            }
        }
        return scalePixelsRender(endPixel);
    }

    // EFFECTS: returns the value scaled by a factor of RENDER_SCALE, rounded to the nearest integer
    public static int scalePixelsRender(int value) {
        return (int) Math.round(value * TimelineViewPanel.getRenderScale());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getClickCount() == 2) {
            int tick = (int) Math.round(e.getX() / TimelineViewPanel.getRenderScale());
            
            for (Block b : midiTrack.getBlocks()) {
                if (b.getStartTick() <= tick && b.getStartTick() + b.getDurationTicks() >= tick) {
                    System.out.println("Block at startTick " + b.getStartTick() + "clicked!");
                }
            }
            repaint();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
