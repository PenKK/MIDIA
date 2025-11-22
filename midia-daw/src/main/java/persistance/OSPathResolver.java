package persistance;

import java.nio.file.*;

public final class OSPathResolver {

    public static Path getProjectsDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        Path base;

        if (os.contains("win")) {
            base = Paths.get(System.getenv("LOCALAPPDATA"));
        } else if (os.contains("mac")) {
            base = Paths.get(System.getProperty("user.home"), "Library", "Application Support");
        } else { // Linux and others
            base = Paths.get(System.getProperty("user.home"), ".local", "share");
        }

        Path lowestDir = base.resolve("MIDIA").resolve("projects").resolve("autosave");
        try {
            Files.createDirectories(lowestDir);
        } catch (Exception ignored) {}
        return lowestDir.getParent();
    }
}
