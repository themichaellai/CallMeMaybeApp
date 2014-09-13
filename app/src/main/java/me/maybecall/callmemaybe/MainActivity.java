package me.maybecall.callmemaybe;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private List<String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = new ArrayList<String>();
        final Button searchButton = (Button)findViewById(R.id.search_button);
        final EditText searchField = (EditText)findViewById(R.id.search_term);
        final ListView searchResults = (ListView)findViewById(R.id.search_results);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, items);
        searchResults.setAdapter(adapter);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String text = searchField.getText().toString();
                adapter.add(text);
                Log.d("MainActivity", text);
            }
        });
        new GetAsync().execute(searchResults);
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

class GetAsync extends AsyncTask<ListView, Void, JSONObject> {
    ListView lv;

    @Override
    protected JSONObject doInBackground(ListView... params) {
        this.lv = params[0];
        return fetch();
    }

    protected JSONObject fetch() {
        String url = "http://www.reddit.com/.json";
        BufferedReader inStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpRequest = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpRequest);
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
    }
}
