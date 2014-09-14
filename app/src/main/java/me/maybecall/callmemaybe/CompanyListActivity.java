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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class CompanyListActivity extends Activity {
    final List<Company> companyList = new ArrayList<Company>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_list);
        Intent intent = getIntent();
        TextView title = (TextView)findViewById(R.id.companies_list_title);
        if (intent.getExtras() != null && intent.getExtras().containsKey("companiesJSON")) {
            try {
                Log.d("CompanyListActivity", "had cached companies");
                setCompanies(new JSONArray(intent.getStringExtra("companiesJSON")));
                title.setText("search");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            new ListQueryAsync().execute(this);
        }
        ListView companyListView = (ListView)findViewById(R.id.company_list);
        companyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Company company = companyList.get(i);
                Intent newIntent = new Intent(CompanyListActivity.this, OptionActivity.class);
                newIntent.putExtra("companyJSON", company.getObj().toString());
                startActivity(newIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.company_list, menu);
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

    public void setCompanies(JSONArray companies) {
        companyList.addAll(extractCompanies(companies));
        ArrayAdapter<Company> adapter = new ArrayAdapter<Company>(
                this, android.R.layout.simple_list_item_1, companyList);
        ListView companyList = (ListView)findViewById(R.id.company_list);
        companyList.setAdapter(adapter);
    }

    private static List<Company> extractCompanies(JSONArray companies) {
        List<Company> res = new ArrayList<Company>();
        for (int i = 0; i < companies.length(); i++) {
            try {
                JSONObject companyObj = companies.getJSONObject(i);
                res.add(new Company(companyObj));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
}
class ListQueryAsync extends AsyncTask<Object, Void, JSONArray> {
    CompanyListActivity activity;

    @Override
    protected JSONArray doInBackground(Object... params) {
        this.activity = (CompanyListActivity) params[0];
        return fetch();
    }

    protected JSONArray fetch() {
        //String host = "http://158.130.169.168:3000/";
        String host = "http://young-gorge-7543.herokuapp.com/";
        Uri.Builder builder = Uri.parse(host).buildUpon();
        builder.scheme("http").appendPath("number");
        String url = builder.build().toString();
        Log.d("ListQueryAsync", String.format("Getting %s", url));

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
                JSONArray jsonArray = new JSONArray(resString);
                return jsonArray;
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
    protected void onPostExecute(JSONArray arr) {
        if (arr != null) {
            Log.d("CompanyListActivity", String.format("post execute %s", arr.toString()));
            activity.setCompanies(arr);
        } else {
            Log.d("CompanyListActivity", "post execute, obj is null");
        }
    }
}
