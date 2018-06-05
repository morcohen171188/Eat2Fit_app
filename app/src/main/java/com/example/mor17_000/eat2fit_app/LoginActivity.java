package com.example.mor17_000.eat2fit_app;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String GET_ALL_USERS_API_CALL = "https://eat2fit-restapi.herokuapp.com/users";
    SharedPreferences userPref;
    RestClient client = new RestClient();
    String AllUsersData;
    int userAppId;
    Boolean isLoginSuccsessful = false;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);

        // Check if the user already logged in
     /*  if(userPref.getBoolean("logged",false)){
           //FileHandler.deleteInternalFile(getApplicationContext());
            // go to the main activity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }*/

        // Create a rest task to get all users data
        RestTask task = new RestTask(getApplicationContext(),"GET");
        task.SetUrl(GET_ALL_USERS_API_CALL);
        task.execute();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);

                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
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
            AllUsersData = intent.getStringExtra(RestTask.REST_RESPONSE);
            Log.i(TAG, "RESPONSE = " + AllUsersData);

        }
    };

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        // Inisialize authentication message
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // get data from user
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        try {
            // parse all user data
            JSONArray jsonArr = new JSONArray(AllUsersData);
            // go throught every user and check if the data matches to any user
            for (int userId = 0 ; userId < jsonArr.length(); userId++){
                JSONObject jsonUser = new JSONObject(jsonArr.get(userId).toString());
                if ( jsonUser.optString("userEmail").equalsIgnoreCase(email)){
                    if (jsonUser.optString("userPass").equalsIgnoreCase(password)){
                        // set matched
                        isLoginSuccsessful = true;
                        // save the user id
                        userAppId = userId;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // execute after delay:
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (isLoginSuccsessful)
                        {
                            onLoginSuccess();
                        }else {
                            onLoginFailed();
                        }
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                onLoginSuccess();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        SharedPreferences.Editor editor = userPref.edit();

        editor.putInt("userId", userAppId);  // set values
        editor.putBoolean("logged",true).apply();

        // go to the main activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

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

        return valid;
    }
}

