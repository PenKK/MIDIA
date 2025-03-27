package ui;

import com.formdev.flatlaf.FlatDarculaLaf;

// Launches the Daw UI, entry point of application
public class MainGUI {
    public static void main(String[] args) throws Exception {
        // FlatLaf library for look and feel
        // https://www.formdev.com/flatlaf/
        FlatDarculaLaf.setup();

        try {
            new DawFrame();
        } catch (Exception e) {
            System.out.println("Unable to start program, MIDI device likely unavaliable");
            e.printStackTrace();
        }

    }
}