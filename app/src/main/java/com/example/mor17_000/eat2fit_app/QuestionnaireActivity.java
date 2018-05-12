package com.example.mor17_000.eat2fit_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import butterknife.BindView;

public class QuestionnaireActivity extends AppCompatActivity {
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

    }

    public void btnSaveData(View view){
        // TODO: CANNOT FIND THE SWITCHES
        Boolean switchKosherState = _switchKosher.isChecked();
        Boolean switchVeganState = _switchVegan.isChecked();
        Boolean switchVegetarianState = _switchVegetarian.isChecked();
        Boolean switchMeatState = _switchMeat.isChecked();
        Boolean switchDairyState = _switchDairy.isChecked();

        //TODO: send data to rest api
    }
}
