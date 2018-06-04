package com.example.mor17_000.eat2fit_app;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class RateActivity extends AppCompatActivity {
    SharedPreferences userPref;
    ArrayList<RestaurantDish> arrayOfDishes;
    DishesAdapter adapter;
    ListView listView;
    int userId;
    OnSwipeTouchListener onSwipeTouchListener;
    private  String UPDATE_PREVIOUSLY_LIKED = "https://eat2fit-restapi.herokuapp.com/user/%d/updatepreviously";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        this.getSupportActionBar().hide();
        userPref = getSharedPreferences("userPref", Context.MODE_PRIVATE);
        userId = userPref.getInt("userId", 0);

        showToast("Swipe right to like, left to dislike.");

        // Construct the data source
        arrayOfDishes = new ArrayList<RestaurantDish>();
        // Create the adapter to convert the array to views
        adapter = new DishesAdapter(this, arrayOfDishes);
        // Attach the adapter to a ListView

        listView = (ListView) findViewById(R.id.cardLayout);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext(),
                listView, arrayOfDishes));

        // Add item to adapter
        fillInDishesToRate();

    }
    public void saveData(View view){
        FileHandler.deleteInternalFile(getApplicationContext());
        JSONArray likedDishesArray = new JSONArray();
        for (RestaurantDish dish : arrayOfDishes){
            if (dish.imgLike.equals("LIKE")){
                String dishNameWithoutRate = dish.dishName.split(" - ")[0];
                likedDishesArray.put(dish.restaurantName + ":" + dishNameWithoutRate);
            }
            else if (!dish.imgLike.equals("DISLIKE")){
                FileHandler fh = new FileHandler();
                fh.writeToFile(getApplicationContext(), dish.restaurantName + "!" + dish.dishName + "!" + dish.imgUrl);

            }
        }

        adapter.clear();
        fillInDishesToRate();
        JSONObject previouslyLiked = new JSONObject();
        try {
            previouslyLiked.accumulate("LIKED", likedDishesArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestTask task = new RestTask(getApplicationContext(),"POST");
        task.SetUrl(String.format(UPDATE_PREVIOUSLY_LIKED, userId));
        task.SetJsonData(previouslyLiked);
        task.execute();

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            String[] DishDataText = DishData.split("!");
            if (DishDataText.length > 0) {
                String restName = DishDataText[0].replace("%20", " ");
                RestaurantDish newDish = new RestaurantDish(restName, DishDataText[1], DishDataText[2]);
                adapter.add(newDish);
            }
        }
    }


    /// rest call task func
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
            String responseData = intent.getStringExtra(RestTask.REST_RESPONSE);
        }
    };
}