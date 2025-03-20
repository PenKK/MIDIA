package ui;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarculaLaf;

// Launches the Daw UI, entry point of application
public class MainGUI {
    public static void main(String[] args) throws Exception {
        // FlatDarculaLaf.setup(); // Loads (nicer looking) look and feel
        SwingUtilities.invokeLater(() -> {
            try {
                new DawFrame();
            } catch (Exception e) {
                System.out.println("Unable to start program, MIDI device likely unavaliable");
                e.printStackTrace();
            }
        });
    }
}