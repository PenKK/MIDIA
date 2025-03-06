package ui;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import model.Timeline;
import persistance.JsonReader;

// Controller through which the UI interacts with the timeline
public class DawController {

    // static singleton
    private static DawController instance;
    private Timeline timeline;

    public DawController() throws MidiUnavailableException, InvalidMidiDataException {
        timeline = new Timeline("New project");
    }

    public static DawController getInstance() {     
        return instance;
    }

}
