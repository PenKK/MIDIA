package ui;

import com.formdev.flatlaf.FlatDarculaLaf;

// Launches the Daw UI, entry point of application
public class Main {
    public static void main(String[] args) throws Exception {
        // FlatLaf library for look and feel
        // https://www.formdev.com/flatlaf/
        FlatDarculaLaf.setup();
        new DawFrame();
    }
}