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
        final ArrayList<String> path;
        if (intent.getExtras().containsKey("selectedKeys")) {
            path = intent.getStringArrayListExtra("selectedKeys");
        } else {
            path = new ArrayList<String>();
        }
        try {
            final JSONObject company = new JSONObject(intent.getStringExtra("companyJSON"));
            JSONObject treeObject = company.getJSONObject("treeString");
            final JSONObject currentLevel = traverseTree(treeObject, path);
            setTitle(String.format("%s %s", company.getString("name"), prettyPath(path)));
            final List<String> keys = extractKeys(currentLevel);
            List<String> listableOptions = extractListToDisplay(currentLevel, keys);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_list_item_1, listableOptions);
            optionsListView.setAdapter(adapter);
            optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String key = keys.get(i);
                    Option opt = extractOption(currentLevel, key);
                    if (opt.getChildren() != null && opt.getChildren().length() > 0) {
                        ArrayList<String> newPath = (ArrayList<String>)path.clone();
                        newPath.add(key);
                        Intent newIntent = new Intent(OptionActivity.this, OptionActivity.class);
                        newIntent.putExtra("companyJSON", company.toString());
                        newIntent.putStringArrayListExtra("selectedKeys", newPath);
                        startActivity(newIntent);
                    }
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

    private List<String> extractKeys(JSONObject tree) {
        List<String> keys = iteratorToList(tree.keys());
        Collections.sort(keys);
        return keys;
    }

    private Option extractOption(JSONObject tree, String key) {
        try {
            return new Option(tree.getJSONArray(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject traverseTree(JSONObject tree, List<String> path) {
        try {
            JSONObject currObject = tree;
            for (String key : path) {
                Option option = new Option(currObject.getJSONArray(key));
                currObject = option.getChildren();
            }
            return currObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    private List<String> extractListToDisplay(JSONObject tree, List<String> keys) {
        List<String> res = new ArrayList<String>();
        try {
            for (String key : keys) {
                Option option = new Option(tree.getJSONArray(key));
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

    private static String prettyPath(List<String> path) {
        if (path.size() > 0) {
            StringBuilder sb = new StringBuilder(path.get(0));
            for (int i = 1; i < path.size(); i++) {
                sb.append(" > ");
                sb.append(path.get(i));
            }
            return String.format("(%s)", sb.toString());
        } else {
            return "";
        }
    }
}

