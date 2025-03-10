package ui;

import javax.sound.midi.InvalidMidiDataException;

public class MainConsole {
    public static void main(String[] args) {
        try {
            new DAW();
        } catch (InvalidMidiDataException e) {
            System.out.println("Unable to launch");
        }
    }
}
