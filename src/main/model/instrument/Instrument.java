package model.instrument;

import org.json.JSONObject;

import persistance.Writable;

// An instrument with a name and program number
public interface Instrument extends Writable {

    int getProgramNumber();

    String getName();

    String name();

    String getType();

    @Override
    default JSONObject toJson() {
        JSONObject instrumentJson = new JSONObject();

        instrumentJson.put("name",this.name());
        instrumentJson.put("type", this.getType());

        return instrumentJson;
    }
}
