package ui;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarculaLaf;

// Launches the Daw UI, entry point of application
public class MainGUI {
    public static void main(String[] args) throws Exception {
        if (args.length != 0 && args[0].toUpperCase().trim().equals("TRUE")) {
            FlatDarculaLaf.setup(); 
        }

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