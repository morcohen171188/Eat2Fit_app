package com.example.mor17_000.eat2fit_app;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends EasyLocationAppCompatActivity {
    SharedPreferences userPref;
    ArrayList<String> restLocations = new ArrayList<>();
    Boolean isButtonPressed = false;
    Boolean isRestAutoFind = false;
    TextView tvNavigate;
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
    static final String CALC_RECCOMENDED_API_URL = "https://eat2fit-restapi.herokuapp.com/restaurant/%s&id=%d";
    static final String GET_RESTAURANTS_API_CALL = "https://eat2fit-restapi.herokuapp.com/restaurants";
    RestTask taskRest;
    RestTask taskReccomend;
    SearchView searchView;
    pl.droidsonroids.gif.GifTextView gif;

    String restaurantName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO : DESIGN
        // TODO : RATE DISH
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);
        gif = (pl.droidsonroids.gif.GifTextView) findViewById(R.id.loader_gif);
        tvNavigate = (TextView) findViewById(R.id.tvNavigate);
        tvNavigate.setVisibility(View.INVISIBLE);
        searchView = (SearchView)findViewById(R.id.searchView);
        resultHeader = (TextView) findViewById(R.id.tvResultsHeader);


        // Create a rest task to get all users data
        taskRest = new RestTask(getApplicationContext(),"GET");
        taskRest.SetUrl(GET_RESTAURANTS_API_CALL);
        try {
            String response = taskRest.execute().get();
            parseRestLocationData(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        startLocationIdentifier();

        // results
        tableViewsInitialization();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchButtonClicked(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void searchButtonClicked(String restName){
        restaurantName = restName;
        isButtonPressed = true;
        setTableVisabilityGone();
        gif.setVisibility(View.VISIBLE);
        taskReccomend = new RestTask(getApplicationContext(),"GET");
        taskReccomend.SetUrl(String.format(CALC_RECCOMENDED_API_URL,restaurantName,userPref.getInt("userId", 0)));
        taskReccomend.execute();
    }

    public void myTableRowClickHandler(View view) {
        TableRow tableRow = (TableRow) findViewById(view.getId());
        ImageView rowImage = (ImageView)tableRow.getChildAt(0);
        TextView rowText = (TextView)tableRow.getChildAt(1);
        String text = rowText.getText().toString();
        // TODO : WRITE TO FILE
        FileHandler fileHandler = new FileHandler();
        fileHandler.writeToFile(getApplicationContext(), "here");
        fileHandler.writeToFile(getApplicationContext(), "mor");
        try {
            fileHandler.readFromFile(getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void NavigationTextClicked(View view) {
        SharedPreferences.Editor editor = userPref.edit();

        String restLatitude = "";
        String restLongitude = "";
        for (int restIndex = 0; restIndex < restLocations.size(); restIndex++) {
            if(restLocations.get(restIndex).contains(restaurantName)) {
                String restLocationData = restLocations.get(restIndex);
                String[] separatedData = restLocationData.split(":");
                restLatitude = separatedData[1].toString();
                restLongitude = separatedData[2].toString();
            }
        }

        if (!restLatitude.isEmpty() && !restLongitude.isEmpty()) {
            editor.putString("restaurantName", restaurantName);  // set values
            editor.putString("locationLat", restLatitude);
            editor.putString("locationLng", restLongitude).apply();

            // Start the Signup activity
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);

            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }else{
            showToast("Could not find a restaurant in that name.");
        }



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
        try {
            for (int restIndex = 0; restIndex < restLocations.size(); restIndex++){
                String restLocationData = restLocations.get(restIndex);
                String[] separatedData = restLocationData.split(":");
                String  restName = separatedData[0];
                double restLatitude = Double.parseDouble(separatedData[1]);
                double restLongitude = Double.parseDouble(separatedData[2]);

                if(isCloseLocation(restLatitude, restLongitude,
                                   location.getLatitude(), location.getLongitude())){
                    restaurantName = restName;
                    isRestAutoFind = true;
                    searchButtonClicked(restaurantName);
                    break;
                }
            }

            if (!isRestAutoFind){
                showToast("Could not find a restaurant in your location. \n Please enter a restaurant name.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationProviderEnabled() {

    }

    @Override
    public void onLocationProviderDisabled() {

    }

    private Boolean isCloseLocation(double restLatitude, double restLongitude, double userLatitude, double userLongitude){
        double earthRadius = 6371; // in  kilometers

        double dLat = Math.toRadians(userLatitude-restLatitude);
        double dLng = Math.toRadians(userLongitude-restLongitude);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(restLatitude)) * Math.cos(Math.toRadians(userLatitude));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;
        if (dist < 0.1){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(receiver, new IntentFilter("GET"));
    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // logic for the reciver of the rest task returned data
            String responseData = intent.getStringExtra(RestTask.REST_RESPONSE);
            if(isButtonPressed) {
                fillInTableWithData(responseData);
                gif.setVisibility(View.GONE);
            }
        }
    };

    private void setTableVisabilityGone(){
        resultTable.setVisibility(View.GONE);
        tvRow1.setVisibility(View.GONE);
        tvRow2.setVisibility(View.GONE);
        tvRow3.setVisibility(View.GONE);
        tvRow4.setVisibility(View.GONE);
        tvRow5.setVisibility(View.GONE);
        imgRow1.setVisibility(View.GONE);
        imgRow2.setVisibility(View.GONE);
        imgRow3.setVisibility(View.GONE);
        imgRow4.setVisibility(View.GONE);
        imgRow5.setVisibility(View.GONE);
    }

    private void setTableVisabilityOn(){
        resultTable.setVisibility(View.VISIBLE);
        tvRow1.setVisibility(View.VISIBLE);
        tvRow2.setVisibility(View.VISIBLE);
        tvRow3.setVisibility(View.VISIBLE);
        tvRow4.setVisibility(View.VISIBLE);
        tvRow5.setVisibility(View.VISIBLE);
        imgRow1.setVisibility(View.VISIBLE);
        imgRow2.setVisibility(View.VISIBLE);
        imgRow3.setVisibility(View.VISIBLE);
        imgRow4.setVisibility(View.VISIBLE);
        imgRow5.setVisibility(View.VISIBLE);

    }

    private void parseRestLocationData(String locationData){
        try {
            JSONArray jsonArr = new JSONArray(locationData.toString());
            for (int restIndex = 0; restIndex < jsonArr.length(); restIndex++){
                String restName = ((JSONObject)jsonArr.get(restIndex)).optString("restName");
                String restLatitude = ((JSONObject)jsonArr.get(restIndex)).optString("restLatitude");
                String restLongitude = ((JSONObject)jsonArr.get(restIndex)).optString("restLongitude");
                String restLocationData = restName+":"+restLatitude+":"+restLongitude;
                restLocations.add(restLocationData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            restLocations.add("::");
        }

    }

    private void fillInTableWithData(String data){
        if(data == null) {
            showToast("THERE WAS AN ERROR");
        }
        else {

            if (!isRestAutoFind) {
                tvNavigate.setVisibility(View.VISIBLE);
            }

            resultHeader.setText(restaurantName);
            setTableVisabilityOn();
            Log.i("INFO", data);
            try {
                JSONArray jsonArr = new JSONArray(data.toString());
                //tvRow1.setText(jsonArr.get(0));
                JSONObject jobj = new JSONObject(jsonArr.get(0).toString());
                Iterator<String> keys = jobj.keys();
                String str_Name = keys.next();
                String value = jobj.optString(str_Name);


                tvRow1.setText(str_Name + "\n" + value + "%");
                jsonArr.length();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void startLocationIdentifier(){

        LocationRequest locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);
        EasyLocationRequest easyLocationRequest = new EasyLocationRequestBuilder()
                .setLocationRequest(locationRequest)
                .setFallBackToLastLocationTime(3000)
                .build();
        requestSingleLocationFix(easyLocationRequest);
    }

    private void tableViewsInitialization(){
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
        setTableVisabilityGone();
    }
}
