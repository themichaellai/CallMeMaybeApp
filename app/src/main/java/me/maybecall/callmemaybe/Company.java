package me.maybecall.callmemaybe;

import org.json.JSONException;
import org.json.JSONObject;

public class Company {
    protected String name;
    protected String number;
    protected JSONObject obj;

    public Company(JSONObject obj) {
        try {
            name = obj.getString("name");
            number = obj.getString("number");
            this.obj = obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public JSONObject getObj() {
        return obj;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", name, number);
    }
}
