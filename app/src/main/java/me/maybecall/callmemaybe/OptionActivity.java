package me.maybecall.callmemaybe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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
            final JSONObject company = new JSONObject(intent.getStringExtra("companyJSON"));
            setTitle(company.getString("name"));
            final List<String> keys = extractKeys(company);
            List<String> listableOptions = extractListToDisplay(company, keys);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, listableOptions);
            optionsListView.setAdapter(adapter);
            optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String key = keys.get(i);
                    Option opt = extractOption(company, key);
                    Log.d("OptionActivity", String.format("tapped %s", opt.getDescription()));
                }
            });
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

    private List<String> extractKeys(JSONObject companyJSON) {
        try {
            JSONObject children = companyJSON.getJSONObject("treeString");
            List<String> keys = iteratorToList(children.keys());
            Collections.sort(keys);
            return keys;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Option extractOption(JSONObject company, String key) {
        try {
            JSONObject children = company.getJSONObject("treeString");
            return new Option(children.getJSONArray(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<String> extractListToDisplay(JSONObject companyJSON, List<String> keys) {
        List<String> res = new ArrayList<String>();
        try {
            JSONObject children = companyJSON.getJSONObject("treeString");
            for (String key : keys) {
                Option option = new Option(children.getJSONArray(key));
                res.add(option.getDescription());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> res = new ArrayList<T>();
        while (iterator.hasNext()) {
            res.add(iterator.next());
        }
        return res;
    }
}

