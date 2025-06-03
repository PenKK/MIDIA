package model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;

import org.json.JSONArray;
import org.json.JSONObject;

import model.instrument.Instrument;
import persistance.Writable;

// The orchestrator of the whole project.
// This class will manage the primary Sequence/MidiTracks objects and is responsible for playback,
// converting MidiTrack to javax.sound.midi.Track, tempo, assigning channels, 
// Higher level MidiTrack(s) will be converted to the lower level Java Track for playback
public class Timeline implements Writable {

    public static final double MAX_HORIZONTAL_SCALE = 1;
    public static final double MIN_HORIZONTAL_SCALE = 0.02;

    private static final int DEFAULT_BEAT_DIVISON = 4;
    private static final int DEFAULT_BEATS_PER_MEASURE = 4;
    private static final double DEFAULT_HORIZONTAL_SCALE = 1;
    private static final double BASE_PIXELS_PER_BEAT = 100.0;

    private String projectName;
    private ArrayList<MidiTrack> midiTracks;
    private Player player;
    private PropertyChangeSupport pcs;

    private int beatDivision;
    private int beatsPerMeasure;
    private double horizontalScaleFactor;

    // EFFECTS: Creates a timeline with a single sequence with no tracks and the positon 
    //          tick at 0, and a BPM of 120.
    //          Method throws MidiUnavailableException if the device has no MIDI sequencer
    //          available, which is fatal and unrecoverable.
    //          Method throws InvalidMidiDataException if the Sequence has an invalid 
    //          divison type.
    public Timeline(String projectName, PropertyChangeSupport pcs) {
        this.projectName = projectName;
        this.pcs = pcs;

        beatDivision = DEFAULT_BEAT_DIVISON;
        beatsPerMeasure = DEFAULT_BEATS_PER_MEASURE;
        horizontalScaleFactor = DEFAULT_HORIZONTAL_SCALE;
        player = new Player(this);
        midiTracks = new ArrayList<>();

        Event e = new Event(String.format("A new timeline instance was created with project name: %s",
                projectName));
        EventLog.getInstance().logEvent(e);
    }

    // REQUIRES: player.getAvailableChannels().size() >= 0
    // MODIFIES: this
    // EFFECTS: Creates a midiTrack, add its to the list of tracks and returns it
    public MidiTrack createMidiTrack(String name, Instrument instrument, boolean percussive) {
        if (!percussive && player.getAvailableChannels().size() <= 0) {
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

    // REQUIRES: index >= 0 and midiTracks.size() > 0
    // MODIFIES: this
    // EFFECTS: removes the MidiTrack at the specified index from midiTracks, adds the
    //          channel to available channels if the MidiTrack is not percussive, and then
    //          returns the MidiTrack
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
    }

    public void pause() {
        player.pause();
    }

    public int getDurationRemainingMS() {
        return (int) (getLengthMs() - player.getPositionMs());
    }

    // MODIFIES: this
    // EFFECTS: changes projectName and fires propertyChangeEvent
    public void setProjectName(String newProjectName) {
        String oldProjectName = projectName;
        this.projectName = newProjectName;

        pcs.firePropertyChange("projectName", oldProjectName, newProjectName);
    }

    // MODIFIES: this
    // EFFECTS: changes beatDivision and fires propertyChangeEvent
    public void setBeatDivision(int newBeatDivision) {
        int oldBeatDivision = this.beatDivision;
        this.beatDivision = newBeatDivision;
        pcs.firePropertyChange("beatDivision", oldBeatDivision, newBeatDivision);
    }

    // MODIFIES: this
    // EFFECTS: changes beatsPerMeasure and fires propertyChangeEvent
    public void setBeatsPerMeasure(int newBeatsPerMeasure) {
        int oldBeatsPerMeasure = this.beatsPerMeasure;
        this.beatsPerMeasure = newBeatsPerMeasure;
        pcs.firePropertyChange("beatsPerMeasure", oldBeatsPerMeasure, newBeatsPerMeasure);
    }

    // MODIFIES: this
    // EFFECTS: changes horizontalScale and fires propertyChangeEvent
    public void setHorizontalScaleFactor(double newHorizontalScale) {
        double oldHorizontalScale = this.horizontalScaleFactor;
        this.horizontalScaleFactor = newHorizontalScale;
        pcs.firePropertyChange("horizontalScale", oldHorizontalScale, newHorizontalScale);
    }

    public void setPlayer(Player p) {
        player = p;
    }

    // EFFECTS: calculates the tick at which the last note ends, this method
    //          is needed to calculate length ticks without first calling updateSequence()
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

    // EFFECTS: returns the tick scaled for UI, rounded to the nearest integer
    public long scalePixelsRender(long tick) {
        return Math.round(tick * getPixelsPerTick());
    }

    public ArrayList<MidiTrack> getMidiTracks() {
        return midiTracks;
    }

    public MidiTrack[] getMidiTracksArray() {
        return midiTracks.toArray(new MidiTrack[midiTracks.size()]);
    }

    public void updatePlayerSequence() throws InvalidMidiDataException {
        player.updateSequence();
    }

    // EFFECTS: returns the track at the specified index from tracks array
    public MidiTrack getTrack(int index) {
        return midiTracks.get(index);
    }

    public String getProjectName() {
        return projectName;
    }

    public double getHorizontalScaleFactor() {
        return horizontalScaleFactor;
    }

    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
    }

    public int getBeatDivision() {
        return beatDivision;
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

    // MODIFIES: this
    // EFFECTS: adds midiTrack to the list of tracks
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

    // EFFECTS: returns JSON object representation of the timeline
    @Override
    public JSONObject toJson() {
        JSONObject timelineJson = new JSONObject();

        timelineJson.put("projectName", projectName);
        timelineJson.put("player", player.toJson());
        timelineJson.put("beatDivision", beatDivision);
        timelineJson.put("beatsPerMeasure", beatsPerMeasure);
        timelineJson.put("horizontalScale", horizontalScaleFactor);
        timelineJson.put("midiTracks", midiTracksToJson());

        return timelineJson;
    }

    // EFFECTS: returns JSON Array representation of midiTracks array
    private JSONArray midiTracksToJson() {
        JSONArray midiTracksJson = new JSONArray();

        for (MidiTrack midiTrack : midiTracks) {
            midiTracksJson.put(midiTrack.toJson());
        }

        return midiTracksJson;
    }
}
