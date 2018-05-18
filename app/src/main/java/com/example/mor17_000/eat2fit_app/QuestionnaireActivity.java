package com.example.mor17_000.eat2fit_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionnaireActivity extends AppCompatActivity {
    private static final String TAG = "QuestionnaireActivity";
    private  String updatePreferencesApiCall = "https://eat2fit-restapi.herokuapp.com/user/%d/update";
    SharedPreferences userPref;
    int userId;

    @BindView(R.id.btnSendData) Button _saveButton;
    @BindView(R.id.SwitchKosher) Switch _switchKosher;
    @BindView(R.id.SwitchVegan) Switch _switchVegan;
    @BindView(R.id.SwitchVegetarian) Switch _switchVegetarian;
    @BindView(R.id.SwitchMeat) Switch _switchMeat;
    @BindView(R.id.SwitchDairy) Switch _switchDairy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        ButterKnife.bind(this);
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);
        userId = userPref.getInt("userId", 0);


    }

    public void btnSaveData(View view){
        Boolean switchKosherState = _switchKosher.isChecked();
        Boolean switchVeganState = _switchVegan.isChecked();
        Boolean switchVegetarianState = _switchVegetarian.isChecked();
        Boolean switchMeatState = _switchMeat.isChecked();
        Boolean switchDairyState = _switchDairy.isChecked();

        // creating basic Json data without preferences
        JSONObject userPreferencesJsonObj = new JSONObject();
        JSONArray LikedJsonArray = new JSONArray();
        JSONArray DislikedJsonArray = new JSONArray();

        if(switchDairyState){
            LikedJsonArray.put("DAIRY");
        }
        else{
            DislikedJsonArray.put("DAIRY");
        }

        if(switchMeatState){
            LikedJsonArray.put("MEAT");
        }
        else{
            DislikedJsonArray.put("MEAT");
        }

        try {
            userPreferencesJsonObj.accumulate("KOSHER", switchKosherState);
            userPreferencesJsonObj.accumulate("VEGAN", switchVeganState);
            userPreferencesJsonObj.accumulate("VEGETARIAN", switchVegetarianState);
            userPreferencesJsonObj.accumulate("LIKED", LikedJsonArray);
            userPreferencesJsonObj.accumulate("DISLIKED", DislikedJsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestTask task = new RestTask(getApplicationContext(),"POST");
        task.SetUrl(String.format(updatePreferencesApiCall, userId));
        task.SetJsonData(userPreferencesJsonObj);
        task.execute();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
            String response = intent.getStringExtra(RestTask.REST_RESPONSE);

        }
    };
}
