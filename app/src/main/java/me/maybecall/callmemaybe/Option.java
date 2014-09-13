package me.maybecall.callmemaybe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Option {
    protected String description;
    protected JSONObject children;
    public Option(JSONArray arr) {
        try {
            description = arr.getString(0);
            children = arr.getJSONObject(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return description;
    }

    public JSONObject getChildren() {
        return children;
    }
}
