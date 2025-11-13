package ui;

import com.formdev.flatlaf.FlatDarculaLaf;

/**
 * Launches the DAW UI; entry point of the application.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length > 0 && args[0].equals("--cli"))
            new DawCLI();
        else {
            FlatDarculaLaf.setup();
            new DawFrame();
        }
        // check for resource leaks; only the main thread should be alive
        assert Thread.getAllStackTraces().keySet().stream().filter(t -> !t.isDaemon()).count() == 1;
    }
}