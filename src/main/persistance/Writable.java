package persistance;

import org.json.JSONObject;

/**
 * An object that can be serialized to a JSON representation.
 * <p>
 * Code adapted from src/main/persistance/Writable
 * at <a href="https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo">...</a>
 */
public interface Writable {
    /**
     * Returns this object as a JSON object.
     *
     * @return the JSON representation of this object
     */
    JSONObject toJson();
}
