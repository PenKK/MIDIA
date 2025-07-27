package ui.windows.timeline.midi;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import model.Block;
import model.MidiTrack;
import model.Note;
import model.Player;
import model.Timeline;
import model.TimelineController;
import model.editing.DawClipboard;
import ui.windows.piano.roll.PianoRollDialog;
import ui.windows.timeline.midi.popup.BlockPopupMenu;
import ui.windows.timeline.midi.popup.TrackGapPopupMenu;

// Interactable render of the MidiTrack's blocks and notes
public class TrackRenderPanel extends JPanel {

    private static final Color NOTE_COLOR = Color.decode("#ECF0F1");
    private static final Color BLOCK_BACKGROUND_COLOR = Color.decode("#34495E");
    private static final Color LINE_COLOR = Color.decode("#333333");

    private static final int CORNER_ROUNDING_BLOCK = 2;
    private static final int BLOCK_HEIGHT_MARGIN = 6;
    private static final int EMPTY_BLOCK_WIDTH = 100;

    private static final int MIN_NOTE_RANGE = 16;
    private static final int NOTE_RANGE_PADDING = 2;
    private static final double NOTE_CORNER_RADIUS = 0.3;

    private final MidiTrack midiTrack;
    private final TimelineController timelineController;
    private PianoRollDialog pianoRollDialog;
    private final DawClipboard dawClipboard;

    // EFFECTS: recieves the specified midiTrack, and listens for mouse events
    public TrackRenderPanel(MidiTrack midiTrack, TimelineController timelineController, DawClipboard dawClipboard) {
        this.dawClipboard = dawClipboard;
        this.timelineController = timelineController;
        this.midiTrack = midiTrack;

        this.addMouseListener(mouseAdapter());
    }

    // EFFECTS: Draws all blocks in the midiTrack
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawBlockBorders(midiTrack.getBlocks(), g);
        drawLines(g);
        drawBlockNotes(midiTrack.getBlocks(), g);

    }

    private void drawLines(Graphics g) {
        Timeline timeline = timelineController.getTimeline();

        int beatDivisions = timeline.getBeatDivision();
        int beatsPerMeasure = timeline.getBeatsPerMeasure();

        long divisionTickInterval = Player.PULSES_PER_QUARTER_NOTE / beatDivisions;
        long measureTickInterval = (long) Player.PULSES_PER_QUARTER_NOTE * beatsPerMeasure;

        for (long tick = 0; tick <= getWidth() / timeline.getPixelsPerTick(); tick += divisionTickInterval) {
            g.setColor(LINE_COLOR);
            int pixelPosition = (int) (timeline.scaleTickToPixel(tick));

            if (tick % measureTickInterval == 0) { // One measure
                g.setColor(g.getColor().darker());
            }

            g.drawLine(pixelPosition, 0, pixelPosition, getHeight());
        }
    }

    // EFFECTS: draws the specified block with scaling.
    private void drawBlock(Block block, Graphics g) {
        Color tempColor = g.getColor();
        g.setColor(BLOCK_BACKGROUND_COLOR);

        Timeline timeline = timelineController.getTimeline();

        int width = (int) timeline.scaleTickToPixel(Math.max(block.getDurationTicks(), EMPTY_BLOCK_WIDTH));
        int x = (int) timeline.scaleTickToPixel(block.getStartTick());
        int y = BLOCK_HEIGHT_MARGIN / 2;
        int height = TrackPanel.HEIGHT - BLOCK_HEIGHT_MARGIN;

        g.fillRoundRect(x, y, width, height, CORNER_ROUNDING_BLOCK, CORNER_ROUNDING_BLOCK);

        g.setColor(tempColor);
    }

    private void drawBlockBorders(ArrayList<Block> blocks, Graphics g) {
        for (Block b : blocks) {
            drawBlock(b, g);
        }
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
    @SuppressWarnings("methodlength")
    private void drawBlockNotes(ArrayList<Block> blocks, Graphics g) {
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
            for (Note n : b.getNotesTimeline()) {
                int pitchOffset = minPitch - (range == 0 ? (MIN_NOTE_RANGE / 2) : 0);
                int relativePitch = n.getPitch() - pitchOffset + (NOTE_RANGE_PADDING / 2);

                int x = (int) timeline.scaleTickToPixel(n.getStartTick());
                int y = trackHeight - (int) Math.round(relativePitch * heightDouble - (double) BLOCK_HEIGHT_MARGIN / 2);
                int height = (int) Math.round(heightDouble);
                int width = (int) timeline.scaleTickToPixel(n.getDurationTicks());

                g.setColor(NOTE_COLOR);
                if (isNotePlaying(n)) {
                    g.setColor(Color.BLACK);
                }
                g.fillRoundRect(x, y, width, height, noteRounding, noteRounding);
            }
        }
    }

    private boolean isNotePlaying(Note n) {
        if (!timelineController.isPlaying()) {
            return false;
        }

        long tick = timelineController.getTimeline().getPlayer().getPositionTick();

        return n.getStartTick() <= tick && tick <= n.getStartTick() + n.getDurationTicks();
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
        return (int) timelineController.getTimeline().scaleTickToPixel(endPixel);
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
        long tick = timelineController.getTimeline().scalePixelToTick(e.getX());

        Block block = getBlock(tick);

        if (block == null) {
            new TrackGapPopupMenu(this, timelineController, dawClipboard, e.getX()).show(this, e.getX(), e.getY());
        } else {
            new BlockPopupMenu(block, dawClipboard).show(this, e.getX(), e.getY());
        }
    }

    // EFFECTS: Handles double click behavior on the rendered track
    private void doubleClick(MouseEvent e) {
        long tick = timelineController.getTimeline().scalePixelToTick(e.getX());

        Block b = getBlock(tick);
        String title = midiTrack.getName().concat(String.format(" [%d]", midiTrack.getBlocks().indexOf(b) + 1));

        if (b != null) {
            if (pianoRollDialog != null && pianoRollDialog.isVisible()) {
                pianoRollDialog.dispose();
            }
            pianoRollDialog = new PianoRollDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                  b, timelineController, midiTrack, title);
            pianoRollDialog.setVisible(true);
        }
    }

    private Block getBlock(long tick) {
        for (Block b : midiTrack.getBlocks()) {
            if (b.getStartTick() <= tick && b.getStartTick() + b.getDurationTicks() >= tick) {
                return b;
            }
        }

        return null;
    }

    public MidiTrack getMidiTrack() {
        return midiTrack;
    }
}