package com.example.mor17_000.eat2fit_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.concurrent.ExecutionException;

public class RateActivity extends AppCompatActivity {
    SharedPreferences userPref;
    LinearLayout mLinearLayout;
    int userId;
    OnSwipeTouchListener onSwipeTouchListener;
    private  String UPDATE_PREVIOUSLY_LIKED = "https://eat2fit-restapi.herokuapp.com/user/%d/updatepreviously";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);
        userId = userPref.getInt("userId", 0);

        showToast("Swipe right to like, left to dislike.");
        mLinearLayout = (LinearLayout) findViewById(R.id.cardLayout);
       // fillInDishesToRate();
        createCardWiew("hello",1);
        createCardWiew("hello",2);
        createCardWiew("goodbye",3);
        createCardWiew("hello",4);
        createCardWiew("goodbye",5);
        createCardWiew("hello",6);
        createCardWiew("goodbye",7);
        createCardWiew("goodbye",8);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        onSwipeTouchListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void fillInDishesToRate(){
        FileHandler fileHandler = new FileHandler();
        String fileData = "";
        try {
            fileData = fileHandler.readFromFile(getApplicationContext());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String[] arrDishes =  fileData.split("\n");
        for (String DishData : arrDishes){
            createCardWiew(DishData, 1);
        }
    }

    @SuppressLint("ResourceType")
    private void createCardWiew(String DishData, int id){
        android.support.v7.widget.CardView card = new CardView(getApplicationContext());
        // Set the CardView layoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params.setMargins(16,16,16,16);
        card.setLayoutParams(params);
        card.setId(1);
        card.setRadius(20);
        card.setMinimumHeight(130);
        card.setCardBackgroundColor(Color.TRANSPARENT);

        // Initialize a new TextView to put in CardView
        TextView tv = new TextView(getApplicationContext());
        tv.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        tv.setLayoutParams(params2);

        // get the data
        /*String[] arrDishes =  DishData.split("\n");
        String restaurantName = arrDishes[0];
        String dishName = arrDishes[1];
        String imgUrl = arrDishes[2];
        tv.setText(restaurantName + "\n" + dishName);*/
        tv.setText(DishData);

        // swipe
        onSwipeTouchListener = new OnSwipeTouchListener() {
            public boolean onSwipeTop() {
                return true;
            }
            public boolean onSwipeRight() {
                View myView = getView();
                Toast.makeText(RateActivity.this, "right:" + view.getId(), Toast.LENGTH_SHORT).show();
                return true;
            }
            public boolean onSwipeLeft() {
                Toast.makeText(RateActivity.this, "left", Toast.LENGTH_SHORT).show();
                return true;
            }
            public boolean onSwipeBottom() {
                return true;
            }
        };
        card.setOnTouchListener(onSwipeTouchListener);
        // Put the TextView in CardView
        card.addView(tv);

        mLinearLayout.addView(card);

    }




    /// rest call task func
    public void makeRestCall(){
        // TODO: CREATE THE JSON
        JSONObject userPreferencesJsonObj = new JSONObject();

        RestTask task = new RestTask(getApplicationContext(),"POST");
        task.SetUrl(String.format(UPDATE_PREVIOUSLY_LIKED, userId));
        task.SetJsonData(userPreferencesJsonObj);
        task.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO: POST OR GET?
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
            String responseData = intent.getStringExtra(RestTask.REST_RESPONSE);
        }
    };
}