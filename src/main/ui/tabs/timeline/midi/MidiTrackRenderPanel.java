package ui.tabs.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import model.Block;
import model.MidiTrack;

// Interactable render of the MidiTrack's blocks and notes
public class MidiTrackRenderPanel extends JPanel implements MouseListener {

    private MidiTrack midiTrack;
    public static final double RENDER_SCALE = 0.1;
    public static final Color BLOCK_BACKGROUND_COLOR = new Color(0, 162, 240);
    public static final int HEIGHT_MARGIN_PIXELS = 5;
    public static final int CORNER_ROUNDING = 10;

    // EFFECTS: recieves the specified midiTrack, and listens for mouse events
    public MidiTrackRenderPanel(MidiTrack midiTrack) {
        this.midiTrack = midiTrack;
        this.addMouseListener(this);
    }

    // EFFECTS: Draws all blocks in the midiTrack
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Block block : midiTrack.getBlocks()) {
            drawBlock(block, g);
        }
    }

    // EFFECTS: draws the specified block with scaling.
    private void drawBlock(Block block, Graphics g) {
        Color tempColor = g.getColor();
        g.setColor(BLOCK_BACKGROUND_COLOR);

        int width = scalePixelsRender(Math.max(block.getDurationTicks(), 50));
        int x1 = scalePixelsRender(block.getStartTick());
        int y1 = HEIGHT_MARGIN_PIXELS / 2;
        int height = MidiTrackPanel.HEIGHT - HEIGHT_MARGIN_PIXELS;

        g.fillRoundRect(x1, y1, width, height, 10, 10);
        g.setColor(tempColor);
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
        return (int) Math.round(value * RENDER_SCALE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println(e.getClickCount());
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
