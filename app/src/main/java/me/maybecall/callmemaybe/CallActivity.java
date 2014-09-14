package me.maybecall.callmemaybe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CallActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        TextView phoneNumberText = (TextView)findViewById(R.id.phone_number_text);
        TextView selectedOptionsText = (TextView)findViewById(R.id.selected_options_text);
        Button callButton = (Button)findViewById(R.id.call_button);

        Intent intent = getIntent();
        final ArrayList<String> path;
        if (intent.getExtras().containsKey("selectedKeys")) {
            path = intent.getStringArrayListExtra("selectedKeys");
        } else {
            path = new ArrayList<String>();
        }
        final ArrayList<String> pathDescriptions;
        if (intent.getExtras().containsKey("selectedKeyDescriptions")) {
            pathDescriptions = intent.getStringArrayListExtra("selectedKeyDescriptions");
        } else {
            pathDescriptions = new ArrayList<String>();
        }
        Log.d("OptionActivity", "keys: " + path.toString());
        Log.d("OptionActivity", "key descs: " + pathDescriptions.toString());
        try {
            final JSONObject company = new JSONObject(intent.getStringExtra("companyJSON"));
            String number = company.getString("number");
            phoneNumberText.setText(number);
            selectedOptionsText.setText(prettyPath(path, pathDescriptions));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.call, menu);
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

    private static String prettyPath(List<String> path, List<String> pathDescriptions) {
        if (path.size() > 0) {
            StringBuilder sb = new StringBuilder(
                    String.format("(%s) %s", path.get(0), pathDescriptions.get(0)));
            for (int i = 1; i < path.size(); i++) {
                sb.append("\n");
                sb.append(String.format("(%s) %s", path.get(i), pathDescriptions.get(i)));
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
