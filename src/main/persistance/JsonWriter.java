package persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.json.JSONObject;

import model.Timeline;

// Represents a writer that writes JSON representation of Timeline to file
// Code adapted from src/main/persistance/JsonWriter
//     at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public class JsonWriter {

    private static final int TAB_SPACING = 4;
    private String path;
    private PrintWriter writer;

    // EFFECTS: constructs writer that writes at the specified path
    public JsonWriter(String path) {
        path = path.trim();
        if (!path.endsWith(".json")) {
            path = path.concat(".json");
        }
        this.path = path;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if the path is not found/accessible
    //          for writing
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(new File(path));
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of timeline to path
    public void write(Timeline timeline) {
        JSONObject timelineJson = timeline.toJson();
        saveToFile(timelineJson.toString(TAB_SPACING));
    }

    // EFFECTS: closes the writer
    public void close() {
        writer.close();
    }

    // MODFIES: this
    // EFFECTS: writes string to file
    public void saveToFile(String json) {
        writer.print(json);
    }
}
