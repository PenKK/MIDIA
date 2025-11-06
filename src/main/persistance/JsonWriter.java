package persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONObject;

import model.Timeline;

/**
 * Writes a JSON representation of a Timeline to a file.
 * <p>
 * Code adapted from src/main/persistance/JsonWriter
 * at <a href="https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo">...</a>
 */
public class JsonWriter {

    private static final int TAB_SPACING = 4;
    private final String path;
    private PrintWriter writer;

    /**
     * Constructs a writer that writes to the specified path.
     *
     * @param path the target file path; ".json" is appended if missing
     */
    public JsonWriter(String path) {
        path = path.trim();
        if (!path.endsWith(".json")) {
            path = path.concat(".json");
        }
        this.path = path;
    }

    /**
     * Opens the writer for the configured path.
     *
     * @throws FileNotFoundException if the path is not found or not writable
     */
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(path);
    }

    /**
     * Writes the JSON representation of the given timeline to the file.
     *
     * @param timeline the timeline to serialize
     */
    public void write(Timeline timeline) {
        JSONObject timelineJson = timeline.toJson();
        saveToFile(timelineJson.toString(TAB_SPACING));
    }

    /**
     * Closes the writer.
     */
    public void close() {
        writer.close();
    }

    /**
     * Writes the provided JSON string to the file.
     *
     * @param json the JSON string to write
     */
    public void saveToFile(String json) {
        writer.print(json);
    }
}
