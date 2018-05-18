package com.example.mor17_000.eat2fit_app;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akhgupta.easylocation.EasyLocationAppCompatActivity;
import com.akhgupta.easylocation.EasyLocationRequest;
import com.akhgupta.easylocation.EasyLocationRequestBuilder;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MainActivity extends EasyLocationAppCompatActivity {

    EditText restText;
    ProgressBar progressBar;
    TableLayout resultTable;
    TextView resultHeader;
    TextView tvRow1;
    TextView tvRow2;
    TextView tvRow3;
    TextView tvRow4;
    TextView tvRow5;
    ImageView imgRow1;
    ImageView imgRow2;
    ImageView imgRow3;
    ImageView imgRow4;
    ImageView imgRow5;
    static final String API_URL = "https://eat2fit-restapi.herokuapp.com/restaurant/testRest&id=2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: test location
        // TODO : DESIGN
        // TODO : RATE DISH
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(3000)
                .build();
        requestSingleLocationFix(easyLocationRequest);



        restText = (EditText) findViewById(R.id.restText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        resultHeader = (TextView) findViewById(R.id.tvResultsHeader);

        // results
        resultTable = (TableLayout) findViewById(R.id.resultTableLayout);
        tvRow1 = (TextView) findViewById(R.id.tvRow1);
        tvRow2 = (TextView) findViewById(R.id.tvRow2);
        tvRow3 = (TextView) findViewById(R.id.tvRow3);
        tvRow4 = (TextView) findViewById(R.id.tvRow4);
        tvRow5 = (TextView) findViewById(R.id.tvRow5);
        imgRow1 = (ImageView) findViewById(R.id.imgRow1);
        imgRow2 = (ImageView) findViewById(R.id.imgRow2);
        imgRow3 = (ImageView) findViewById(R.id.imgRow3);
        imgRow4 = (ImageView) findViewById(R.id.imgRow4);
        imgRow5 = (ImageView) findViewById(R.id.imgRow5);

        // button
        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveFeedTask().execute();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationPermissionGranted() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationReceived(Location location) {
        // TODO: FINISH LOCATION TEST
        showToast(location.getProvider() + "," + location.getLatitude() + "," + location.getLongitude());
        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(Void... urls) {
            String restName = restText.getText().toString();

            try {
                RestClient client = new RestClient();
                return client.makeGetCall(API_URL); // TODO:  + restName)
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            resultTable.setVisibility(View.VISIBLE);
            Log.i("INFO", response);
            try {
                JSONArray jsonArr = new JSONArray(response.toString());
                //tvRow1.setText(jsonArr.get(0));
                JSONObject jobj = new JSONObject(jsonArr.get(0).toString());
                Iterator<String> keys = jobj.keys();
                String str_Name=keys.next();
                String value = jobj.optString(str_Name);


                tvRow1.setText(str_Name + "\n" + value + "%");
                jsonArr.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
