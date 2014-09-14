package me.maybecall.callmemaybe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends Activity {
    public JSONObject items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        setTitle("Enter phone number");

        final Button searchButton = (Button)findViewById(R.id.search_button);
        final EditText searchField = (EditText)findViewById(R.id.search_term);
        //final ListView searchResults = (ListView)findViewById(R.id.search_results);
        //final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                //this, android.R.layout.simple_list_item_1, items);
        //searchResults.setAdapter(adapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new GetAsync().execute(MainActivity.this);
            }
        });
        //button.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        String url = "tel:" + sanitizeUri("18774869273,,,4");
        //        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        //        startActivity(intent);
        //    }
        //});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    public String getSearchQuery() {
        EditText searchField = (EditText)findViewById(R.id.search_term);
        return searchField.getText().toString();
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

class GetAsync extends AsyncTask<Object, Void, JSONObject> {
    MainActivity activity;

    @Override
    protected JSONObject doInBackground(Object... params) {
        this.activity = (MainActivity)params[0];
        return fetch();
    }

    protected JSONObject fetch() {
        String host = "http://158.130.169.168:3000/";
        //String host = "http://young-gorge-7543.herokuapp.com/";
        Uri.Builder builder = Uri.parse(host).buildUpon();
        builder.scheme("http").appendPath("number")
                .appendPath(activity.getSearchQuery());
        String url = builder.build().toString();
        Log.d("AsyncFetch", String.format("Getting %s", url));

        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpRequest);
            if (response.getStatusLine().getStatusCode() == 404) {
                return null;
            } else {
                inStream = new BufferedReader(
                        new InputStreamReader(
                                response.getEntity().getContent()));

                StringBuffer buffer = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = inStream.readLine()) != null) {
                    buffer.append(line + NL);
                }
                inStream.close();
                String resString = buffer.toString();
                JSONObject jsonObject = new JSONObject(resString);
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        if (object != null) {
            Log.d("AsyncFetch", String.format("post execute %s", object.toString()));
            Intent intent = new Intent(activity, OptionActivity.class);
            intent.putExtra("companyJSON", object.toString());
            activity.startActivity(intent);
        } else {
            Log.d("AsyncFetch", "post execute, obj is null");
        }
    }
}
