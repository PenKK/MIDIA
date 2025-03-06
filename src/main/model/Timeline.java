package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;

import org.json.JSONArray;
import org.json.JSONObject;

import persistance.Writable;

// The orchestrator of the whole project.
// This class will manage the primary Sequence/MidiTracks objects and is responsible for playback,
// converting MidiTrack to javax.sound.midi.Track, tempo, assigning channels, 
// Higher level MidiTrack(s) will be converted to the lower level Java Track for playback
public class Timeline implements Writable {

    private static Timeline instance;
    private static PropertyChangeSupport pcs = new PropertyChangeSupport(Timeline.class);

    private String projectName;
    private Sequencer sequencer;
    private Sequence sequence;
    private ArrayList<MidiTrack> midiTracks;
    private float beatsPerMinute;
    private int positionTick;
    private ArrayList<Integer> avaliableChannels;

    private static final int PULSES_PER_QUARTER_NOTE = 960;
    private static final float DEFAULT_BPM = 120;

    // EFFECTS: Creates a timeline with a single sequence with no tracks and the positon 
    //          tick at 0, and a BPM of 120.
    //          Method throws MidiUnavailableException if the device has no MIDI sequencer
    //          avaliable, which is fatal and unrecoverable.
    //          Method throws InvalidMidiDataException if the Sequence has an invalid 
    //          divison type, which is impossile since the division type will be constant.
    public Timeline(String projectName) throws MidiUnavailableException, InvalidMidiDataException {
        this.projectName = projectName;
        sequencer = MidiSystem.getSequencer();
        sequence = new Sequence(Sequence.PPQ, PULSES_PER_QUARTER_NOTE);
        sequencer.open();

        beatsPerMinute = DEFAULT_BPM;
        midiTracks = new ArrayList<>();
        positionTick = 0;

        avaliableChannels = new ArrayList<>() {
            {
                for (int i = 0; i <= 15; i++) {
                    if (i != 9) {
                        add(i); // add numbers 0-15 excluding 9, 9 is for percussion
                    }
                }
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: Initializes the singeton instance
    public static Timeline getInstance() {
        if (instance == null) {
            try {
                instance = new Timeline("New Project");
            } catch(MidiUnavailableException | InvalidMidiDataException e) {
                System.out.println("Unable to create new instance, likely that no MIDI device was found");
            }
        }
        return instance;
    }

    // MODIFIES: this
    // EFFECTS: Replaces instance and fires property change for all who need to reflect changes
    public static void setInstance(Timeline newInstance) {
        Timeline oldInstance = instance;
        instance = newInstance;
        pcs.firePropertyChange("timeline", oldInstance, newInstance);
    }

    // MODIFIES: this
    // EFFECTS: adds the specified observer as a listener of property changes
    public static void addObserver(PropertyChangeListener observer) {
        pcs.addPropertyChangeListener(observer);
    }

    // MODIFIES: this
    // EFFECTS: removes the specified observer as a listener of property changes
    public static void removeObserver(PropertyChangeListener observer) {
        pcs.removePropertyChangeListener(observer);
    }

    // REQUIRES: avaliableChannels.size() >= 0
    // MODIFIES: this
    // EFFECTS: Creates a midiTrack, add its to the list of tracks and returns it
    public MidiTrack createMidiTrack(String name, int instrument, boolean percussive) {
        if (!percussive && avaliableChannels.size() <= 0) {
            return null;
        }

        MidiTrack newMidiTrack = new MidiTrack(name, instrument,
                percussive ? 9 : avaliableChannels.remove(0));
        midiTracks.add(newMidiTrack);
        return newMidiTrack;
    }

    // REQUIRES: index >= 0 and midiTracks.size() > 0
    // MODIFIES: this
    // EFFECTS: removes the MidiTrack at the specified index from midiTracks, adds the
    //          channel to avaliable channels if the MidiTrack is not percussive, and then
    //          returns the MidiTrack
    public MidiTrack removeMidiTrack(int index) {
        MidiTrack toRemove = midiTracks.get(index);
        if (!toRemove.isPercussive()) {
            avaliableChannels.add(toRemove.getChannel());
        }
        return midiTracks.remove(index);
    }

    // MODIFIES: this
    // EFFECTS: updates the sequence with the current midiTracks, converting each one 
    //          to a Java Track. Throws InvalidMidiDataException if invalid midi data
    //          is found when setting the sequence to the sequencer
    public void updateSequence() throws InvalidMidiDataException {
        resetTracks();
        for (MidiTrack currentMidiTrack : midiTracks) {
            if (currentMidiTrack.isMuted() || currentMidiTrack.getVolume() == 0) {
                continue;
            }

            Track track = sequence.createTrack();
            currentMidiTrack.applyToTrack(track);
        }

        sequencer.setSequence(sequence);
    }

    // MODIFIES: this
    // EFFECTS: deletes all Tracks from the sequence, essentially resetting playback
    public void resetTracks() {
        for (Track track : sequence.getTracks()) {
            sequence.deleteTrack(track);
        }
    }

    // MODIFIES: this
    // EFFECTS: Begins playback at the current tick position and with tempo according to 
    //          beatsPerMinute. Can throw InvalidMidiDataException if invalid
    //          midi data is found during the sequence update (handled by UI)
    public void play() throws InvalidMidiDataException {
        updateSequence();
        sequencer.setTempoInBPM(beatsPerMinute);
        sequencer.start();
    }

    // MODIFIES: this
    // EFFECTS: Pauses playback
    public void pause() {
        sequencer.stop();
        setPositionTick((int) sequencer.getTickPosition());
    }

    // MODIFIES: this
    // EFFECTS: replaces current avaliable channels with new ones, use wisely
    //          channels may be out of sync with tracks 
    public void setAvaliableChannels(ArrayList<Integer> newChannels) {
        avaliableChannels = newChannels;
    }

    // REQUIRES: newPositionTick >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback given ticks
    public void setPositionTick(int newPositionTick) {
        this.positionTick = newPositionTick;
    }

    // REQUIRES: newPositionMs >= 0
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback at the given milliseconds
    public void setPositionMs(double newPositionMs) {
        this.positionTick = msToTicks(newPositionMs);
    }

    // REQUIRES: newPositionBeat >= 1
    // MODIFIES: this
    // EFFECTS: Changes timeline position to start playback given the beat to start at
    public void setPositionBeat(double newPositionBeat) {
        this.positionTick = beatsToTicks(newPositionBeat - 1);
    }

    // REQUIRES: bpm >= 1
    // EFFECTS: changes the BPM
    public void setBPM(float bpm) {
        this.beatsPerMinute = bpm;
    }

    // EFFECTS: returns the calculation of the sequence length in milliseconds
    public double getLengthMs() {
        return ticksToMs(getLengthTicks());
    }

    // EFFECTS: returns the calculation of the sequence length in beats
    public double getLengthBeats() {
        return ticksToBeats(getLengthTicks());
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: converts ticks to milliseconds given the BPM
    public double ticksToMs(int ticks) {
        double durationInQuarterNotes = (double) ticks / (double) sequence.getResolution();
        ;
        double durationInMinutes = durationInQuarterNotes / beatsPerMinute;
        double durationInMS = durationInMinutes * 60000;
        return durationInMS;
    }

    // REQUIRES: ms >= 0
    // EFFECTS: converts milliseconds to ticks (reverse of above)
    public int msToTicks(double ms) {
        double durationInMinutes = ms / (double) 60000;
        double durationInQuarterNotes = beatsPerMinute * durationInMinutes;
        double ticks = durationInQuarterNotes * sequence.getResolution();
        ;
        return (int) Math.round(ticks);
    }

    // REQUIRES: beats >= 0
    // EFFECTS: calculates beats to ticks conversion
    public int beatsToTicks(double beats) {
        double ticks = beats * (double) sequence.getResolution();
        ;
        return (int) Math.round(ticks);
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: calculates ticks to beats conversion (reverse of above)
    public double ticksToBeats(int ticks) {
        double beats = (double) ticks / (double) sequence.getResolution();
        ;
        return beats;
    }

    // REQUIRES: ticks >= 0
    // EFFECTS: converts the ticks to which beat the ticks are on
    public double ticksToOnBeat(int ticks) {
        return ticksToBeats(ticks) + 1;
    }

    // EFFECTS: calculates the tick at which the last note ends, this method
    //          is needed to calculate length ticks without first calling updateSequence()
    public int getLengthTicks() {
        int lastNoteEndTick = 0;
        for (MidiTrack midiTrack : midiTracks) {
            for (Block block : midiTrack.getBlocks()) {
                for (Note note : block.getNotesTimeline()) {
                    int endTick = note.getStartTick() + note.getDurationTicks();
                    if (endTick > lastNoteEndTick) {
                        lastNoteEndTick = endTick;
                    }
                }
            }
        }
        return lastNoteEndTick;
    }

    public ArrayList<MidiTrack> getTracks() {
        return midiTracks;
    }

    // EFFECTS: returns the timeline position in ms by converting ticks to ms
    public double getPositionMs() {
        return ticksToMs(positionTick);
    }

    // EFFECTS: returns the timeline position in beats by converting ticks to beats
    public double getPositionBeats() {
        return ticksToBeats(positionTick);
    }

    // EFFECTS: returns the beat on which the timeline position starts playback
    public double getPositionOnBeat() {
        return getPositionBeats() + 1;
    }

    // EFFECTS: returns the track at the specified index from tracks array
    public MidiTrack getTrack(int index) {
        return midiTracks.get(index);
    }

    public float getBPM() {
        return beatsPerMinute;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public Sequencer getSequencer() {
        return sequencer;
    }

    public int getPositionTick() {
        return positionTick;
    }

    public ArrayList<Integer> getAvaliableChannels() {
        return avaliableChannels;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String newProjectName) {
        this.projectName = newProjectName;
    }

    // MODIFIES: this
    // EFFECTS: adds midiTrack to the list of tracks
    public void addMidiTrack(MidiTrack midiTrack) {
        midiTracks.add(midiTrack);
    }

    public static PropertyChangeSupport getPropertyChangeSupport() {
        return pcs;
    }

    // EFFECTS: returns JSON object representation of the timeline
    @Override
    public JSONObject toJson() {
        JSONObject timelineJson = new JSONObject();

        timelineJson.put("projectName", projectName);
        timelineJson.put("beatsPerMinute", beatsPerMinute);
        timelineJson.put("positionTick", positionTick);
        timelineJson.put("avaliableChannels", new JSONArray(avaliableChannels));
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
