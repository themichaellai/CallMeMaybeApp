package me.maybecall.callmemaybe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class OptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ListView optionsListView = (ListView)findViewById(R.id.options_list);
        Intent intent = getIntent();
        try {
            JSONObject options = new JSONObject(intent.getStringExtra("companyJSON"));
            List<String> listableOptions = extractListToDisplay(options);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, listableOptions);
            optionsListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private List<String> extractListToDisplay(JSONObject companyJSON) {
        List<String> res = new ArrayList<String>();
        try {
            JSONObject children = companyJSON.getJSONObject("treeString");
            Iterator<String> keys = children.keys();
            while (keys.hasNext()) {
                JSONArray arr = children.getJSONArray(keys.next());
                String description = arr.getString(0);
                res.add(description);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }
}

