package model.instrument;

import org.json.JSONObject;

import persistance.Writable;

// An instrument with with a name and program number
public interface Instrument extends Writable {

    public int getProgramNumber();

    public String getName();

    public String name();

    @Override
    default JSONObject toJson() {
        JSONObject instrumentJson = new JSONObject();

        instrumentJson.put("name",this.name());
        instrumentJson.put("className", this.getClass().getSimpleName());

        return instrumentJson;
    }
}
