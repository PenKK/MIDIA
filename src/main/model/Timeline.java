package model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;

import org.json.JSONArray;
import org.json.JSONObject;

import model.event.Event;
import model.event.EventLog;
import model.instrument.Instrument;
import persistance.Writable;

/**
 * The orchestrator of the project.
 * <p>
 * Manages the primary sequence and MidiTracks, is responsible for playback,
 * converting MidiTracks to {@code javax.sound.midi.Track}, tempo, channel assignment,
 * and other timeline-level operations.
 */
public class Timeline implements Writable {

    private static final double DEFAULT_HORIZONTAL_SCALE = 1;
    private static final double BASE_PIXELS_PER_BEAT = 100.0;

    private String projectName;
    private final ArrayList<MidiTrack> midiTracks;
    private Player player;
    private PropertyChangeSupport pcs;

    private double horizontalScaleFactor;

    /**
     * Constructs a timeline with no tracks, position at tick 0, and a default playback state.
     *
     * @param projectName the name of the project
     * @param pcs         the property change support used to publish timeline events
     */
    public Timeline(String projectName, PropertyChangeSupport pcs) {
        this.projectName = projectName;
        this.pcs = pcs;

        horizontalScaleFactor = DEFAULT_HORIZONTAL_SCALE;
        player = new TimelinePlayer(this);
        midiTracks = new ArrayList<>();

        Event e = new Event(String.format("A new timeline instance was created with project name: %s",
                projectName));
        EventLog.getInstance().logEvent(e);
    }

    /**
     * Creates a new MidiTrack and adds it to the timeline.
     * <p>
     * If a non-percussive instrument is requested but no channels are available, returns null.
     *
     * @param name       the track name
     * @param instrument the instrument for the track
     * @return the created track, or null if creation is not possible due to channel exhaustion
     */
    public MidiTrack createMidiTrack(String name, Instrument instrument) {
        boolean percussive = !instrument.getType().equals("tonal");
        
        if (!percussive && player.getAvailableChannels().isEmpty()) {
            return null;
        }

        MidiTrack newMidiTrack = new MidiTrack(name, instrument,
                percussive ? 9 : player.getAvailableChannels().remove(0));

        ArrayList<MidiTrack> oldTracks = new ArrayList<>(midiTracks);
        midiTracks.add(newMidiTrack);
        pcs.firePropertyChange("midiTracks", oldTracks, new ArrayList<>(midiTracks));

        Event e = new Event(String.format("Created new MidiTrack, instrument: %s, channel: %d, percussive: %b. "
                + "Remaining instrumental channels: %d",
                instrument, newMidiTrack.getChannel(),
                newMidiTrack.isPercussive(), player.getAvailableChannels().size()));
        EventLog.getInstance().logEvent(e);

        return newMidiTrack;
    }

    /**
     * Removes and returns the MidiTrack at the specified index.
     * <p>
     * If the removed track is non-percussive, its channel is returned to the pool of available channels.
     * Preconditions: {@code index >= 0} and {@code midiTracks.size() > 0}
     *
     * @param index the index of the track to remove
     * @return the removed MidiTrack
     */
    public MidiTrack removeMidiTrack(int index) {
        ArrayList<MidiTrack> oldTracks = new ArrayList<>(midiTracks);
        MidiTrack removed = midiTracks.remove(index);

        if (!removed.isPercussive()) {
            player.getAvailableChannels().add(removed.getChannel());
        }

        pcs.firePropertyChange("midiTracks", oldTracks, new ArrayList<>(midiTracks));

        Event e = new Event(String.format("Removed MidiTrack[%d]: %s. Remaining instrumental channels: %d",
                index, removed, player.getAvailableChannels().size()));
        EventLog.getInstance().logEvent(e);

        return removed;
    }

    public void play() throws InvalidMidiDataException {
        player.play();
        pcs.firePropertyChange("playbackStarted", null, null);
    }

    public void pause() {
        player.pause();
    }

    public int getDurationRemainingMS() {
        return (int) (getLengthMs() - player.getPositionMs());
    }

    /**
     * Sets the project name and fires a property change event.
     *
     * @param newProjectName the new project name
     */
    public void setProjectName(String newProjectName) {
        String oldProjectName = projectName;
        this.projectName = newProjectName;

        pcs.firePropertyChange("projectName", oldProjectName, newProjectName);
    }

    /**
     * Sets the horizontal scale for UI rendering and fires a property change event.
     *
     * @param newHorizontalScale the new horizontal scale factor
     */
    public void setHorizontalScaleFactor(double newHorizontalScale) {
        double oldHorizontalScale = this.horizontalScaleFactor;
        this.horizontalScaleFactor = newHorizontalScale;
        pcs.firePropertyChange("horizontalScaleFactor", oldHorizontalScale, newHorizontalScale);
    }

    public void setPlayer(Player p) {
        player = p;
    }

    /**
     * Calculates the tick at which the last note ends across all tracks and blocks.
     * Useful for computing the total length without updating the sequencer.
     *
     * @return the last note end tick
     */
    public long getLengthTicks() {
        long lastNoteEndTick = 0;
        for (MidiTrack midiTrack : midiTracks) {
            for (Block block : midiTrack.getBlocks()) {
                for (Note note : block.getNotesTimeline()) {
                    long endTick = note.getStartTick() + note.getDurationTicks();
                    if (endTick > lastNoteEndTick) {
                        lastNoteEndTick = endTick;
                    }
                }
            }
        }
        return lastNoteEndTick;
    }

    public double getPixelsPerTick() {
        return (BASE_PIXELS_PER_BEAT / Player.PULSES_PER_QUARTER_NOTE) * horizontalScaleFactor;
    }

    /**
     * Converts a tick value to pixels for UI rendering.
     *
     * @param tick the tick value
     * @return the pixel value, rounded to the nearest integer
     */
    public int scaleTickToPixel(long tick) {
        return (int) Math.round(tick * getPixelsPerTick());
    }

    /**
     * Converts a pixel value to ticks for UI interactions.
     *
     * @param pixel the pixel value
     * @return the tick value, rounded to the nearest integer
     */
    public long scalePixelToTick(int pixel) {
        return Math.round(pixel / getPixelsPerTick());
    }

    public long snapTickLowerBeat(long rawTick) {
        return (rawTick / Player.PULSES_PER_QUARTER_NOTE) * Player.PULSES_PER_QUARTER_NOTE;
    }

    public ArrayList<MidiTrack> getMidiTracks() {
        return midiTracks;
    }

    public MidiTrack[] getMidiTracksArray() {
        return midiTracks.toArray(new MidiTrack[0]);
    }

    public void updatePlayerSequence() throws InvalidMidiDataException {
        player.updateSequence();
    }

    /**
     * Returns the track at the specified index.
     *
     * @param index the track index
     * @return the MidiTrack at the given index
     */
    public MidiTrack getTrack(int index) {
        return midiTracks.get(index);
    }

    public String getProjectName() {
        return projectName;
    }

    public double getHorizontalScaleFactor() {
        return horizontalScaleFactor;
    }

    public Player getPlayer() {
        return player;
    }

    public double getLengthMs() {
        return player.getLengthMs();
    }

    public double getLengthBeats() {
        return player.getLengthBeats();
    }

    /**
     * Adds a MidiTrack to this timeline.
     *
     * @param midiTrack the track to add
     */
    public void addMidiTrack(MidiTrack midiTrack) {
        midiTracks.add(midiTrack);

        Event e = new Event(String.format("A MidiTrack was added to timeline %s. New Length: %d ticks",
                projectName, getLengthTicks()));
        EventLog.getInstance().logEvent(e);
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    public void setPropertyChangeSupport(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    /**
     * Returns a JSON object representation of this timeline.
     *
     * @return the JSON representation of the timeline
     */
    @Override
    public JSONObject toJson() {
        JSONObject timelineJson = new JSONObject();

        timelineJson.put("projectName", projectName);
        timelineJson.put("player", player.toJson());
        timelineJson.put("beatDivision", player.getBeatDivision());
        timelineJson.put("beatsPerMeasure", player.getBeatsPerMeasure());
        timelineJson.put("horizontalScaleFactor", horizontalScaleFactor);
        timelineJson.put("midiTracks", midiTracksToJson());

        return timelineJson;
    }

    /**
     * Returns a JSON array representation of the MidiTracks in this timeline.
     *
     * @return a JSON array of track JSON objects
     */
    private JSONArray midiTracksToJson() {
        JSONArray midiTracksJson = new JSONArray();

        for (MidiTrack midiTrack : midiTracks) {
            midiTracksJson.put(midiTrack.toJson());
        }

        return midiTracksJson;
    }
}
