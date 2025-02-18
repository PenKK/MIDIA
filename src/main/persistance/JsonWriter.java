package persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

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
        this.path = path;
    }

    // MODIFIES: this
    // EFFECTS: opens writer; throws FileNotFoundException if the path is not found/accessible
    //          for writing
    public void open() throws FileNotFoundException {
        // stub
    }

    // MODIFIES: this
    // EFFECTS: writes JSON representation of timeline to path
    public void write(Timeline timeline) {
        // stub
    }

    // EFFECTS: closes the writer
    public void close() {
        // stub
    }

    // MODFIES: this
    // EFFECTS: writes string to file
    public void saveToFile() {
        // stub
    }
}
