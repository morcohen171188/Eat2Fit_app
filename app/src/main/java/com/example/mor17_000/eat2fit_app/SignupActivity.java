package com.example.mor17_000.eat2fit_app;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String addNewUserApiCall = "https://eat2fit-restapi.herokuapp.com/user";
    SharedPreferences userPref;
    private int newUserId;
    private ProgressDialog progressDialog;
    private Boolean isSignUpSuccsessful = false;

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getSupportActionBar().hide();
        ButterKnife.bind(this);
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(receiver, new IntentFilter("POST"));
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
            newUserId = Integer.parseInt(response);
            SharedPreferences.Editor editor = userPref.edit();

            editor.putInt("userId", newUserId);  // set values
            editor.putBoolean("logged",true).apply();
            isSignUpSuccsessful = true;

            Log.i(TAG, "RESPONSE = " + newUserId);

            if (isSignUpSuccsessful)
            {
                onSignupSuccess();
            }else {
                onSignupFailed();
            }

            progressDialog.hide();
        }
    };

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // creating basic Json data without preferences
        JSONArray previouslyDefaultEmptyArray = new JSONArray();
        JSONArray previouslyDislikedDefaultEmptyArray = new JSONArray();
        JSONObject userPreferencesJsonObj = new JSONObject();
        JSONArray LikedJsonArray = new JSONArray();
        JSONArray DislikedJsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            userPreferencesJsonObj.accumulate("KOSHER", "0");
            userPreferencesJsonObj.accumulate("VEGETARIAN", "0");
            userPreferencesJsonObj.accumulate("VEGAN", "0");
            userPreferencesJsonObj.accumulate("LIKED", LikedJsonArray);
            userPreferencesJsonObj.accumulate("DISLIKED", DislikedJsonArray);

            // build jsonObject

            jsonObject.accumulate("userId", "replace");
            jsonObject.accumulate("userName", name);
            jsonObject.accumulate("userEmail", email);
            jsonObject.accumulate("userPass", password);
            jsonObject.accumulate("userPreferences", userPreferencesJsonObj);
            jsonObject.accumulate("previouslyLiked", previouslyDefaultEmptyArray);
            jsonObject.accumulate("previouslyDisliked", previouslyDislikedDefaultEmptyArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestTask task = new RestTask(getApplicationContext(),"POST");
        task.SetUrl(addNewUserApiCall);
        task.SetJsonData(jsonObject);
        task.execute();
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        SharedPreferences.Editor editor = userPref.edit();

        editor.putInt("userId", newUserId);  // set values
        editor.putBoolean("logged",true).apply();

        Intent intent = new Intent(getApplicationContext(), QuestionnaireActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Failed to sign up.", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}
