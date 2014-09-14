package me.maybecall.callmemaybe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
            final String number = company.getString("number");
            final String name = company.getString("name");
            phoneNumberText.setText(String.format("%s (%s)", name, number));
            selectedOptionsText.setText(prettyPath(path, pathDescriptions));
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = "tel:" + sanitizeUri(number) + generateToneString(path);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
                    startActivity(intent);
                }
            });
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

    private static String generateToneString(List<String> path) {
        if (path.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String key : path) {
                sb.append(",,");
                sb.append(key);
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private static String sanitizeUri(String uri) {
        return uri.trim().replace(" ", "%20").replace("&", "%26")
                .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
                .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
                .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
                .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
                .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
                .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
                .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
                .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
                .replace("|", "%7C").replace("}", "%7D");
    }
}
