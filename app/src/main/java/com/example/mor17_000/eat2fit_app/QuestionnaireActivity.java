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
        this.getSupportActionBar().hide();
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

        JSONObject userPreferencesJsonObj = generatePreferencesJson(switchKosherState,
                                                                    switchVeganState,
                                                                    switchVegetarianState,
                                                                    LikedJsonArray,
                                                                    DislikedJsonArray);

        executeSavePreferencesTask(userPreferencesJsonObj);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSwitchViewClicked(View view){
        if(view.getId() == _switchVegetarian.getId() ||
                view.getId() == _switchVegan.getId()){
            fixConflictsInPreferences_V();
        }
        else if(view.getId() == _switchDairy.getId() ||
                view.getId() == _switchMeat.getId()){
            fixConflictsInPreferences_DairyMeat();
        }
    }

    private void fixConflictsInPreferences_DairyMeat(){
        if(_switchDairy.isChecked()){
            _switchVegan.setChecked(false);
        }

        if(_switchMeat.isChecked()){
            _switchVegan.setChecked(false);
            _switchVegetarian.setChecked(false);
        }
    }

    private void fixConflictsInPreferences_V(){
        if(_switchVegan.isChecked()){
            _switchDairy.setChecked(false);
            _switchMeat.setChecked(false);
        }

        if(_switchVegetarian.isChecked()){
            _switchMeat.setChecked(false);
        }
    }

    private JSONObject generatePreferencesJson(Boolean KosherFlag, Boolean VeganFlag, Boolean VegetarianFlag,
                                               JSONArray LikedArray,JSONArray DislikedArray){
        JSONObject userPreferencesJsonObj = new JSONObject();

        // if the arrays are empty initialize with none
        if (LikedArray.length() == 0){
            LikedArray.put("none");
        }

        if (DislikedArray.length() == 0){
            DislikedArray.put("none");
        }

        try {
            userPreferencesJsonObj.accumulate("KOSHER", KosherFlag);
            userPreferencesJsonObj.accumulate("VEGAN", VeganFlag);
            userPreferencesJsonObj.accumulate("VEGETARIAN", VegetarianFlag);
            userPreferencesJsonObj.accumulate("LIKED", LikedArray);
            userPreferencesJsonObj.accumulate("DISLIKED", DislikedArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return userPreferencesJsonObj;
    }

    private void executeSavePreferencesTask(JSONObject userPreferences){
        RestTask task = new RestTask(getApplicationContext(),"POST");
        task.SetUrl(String.format(updatePreferencesApiCall, userId));
        task.SetJsonData(userPreferences);
        task.execute();
    }

    @Override
    public void onBackPressed() {
        // creating default Json data
        JSONArray LikedJsonArray = new JSONArray();
        JSONArray DislikedJsonArray = new JSONArray();

        LikedJsonArray.put("DAIRY");
        LikedJsonArray.put("MEAT");

        JSONObject autoGeneratedPreferences = generatePreferencesJson(false,
                                                                      false,
                                                                      false,
                                                                      LikedJsonArray,
                                                                      DislikedJsonArray);

        executeSavePreferencesTask(autoGeneratedPreferences);

        super.onBackPressed();
        finish();
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
