package persistance;

import org.json.JSONObject;

// Code adapted from src/main/persistance/Writable
//     at https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
public interface Writable {
    // EFFECTS: returns the object as a JSON object
    JSONObject toJson();
}
