package persistance;

import org.json.JSONObject;

/**
 * An object that can be serialized to a JSON representation.
 */
public interface Writable {
    /**
     * Returns this object as a JSON object.
     *
     * @return the JSON representation of this object
     */
    JSONObject toJson();
}
