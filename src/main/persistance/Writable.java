package persistance;

import org.json.JSONObject;

public interface Writable {
    // EFFECTS: returns the object as a JSON object
    JSONObject toJson();
}
