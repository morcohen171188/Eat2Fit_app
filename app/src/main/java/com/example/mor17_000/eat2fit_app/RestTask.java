package com.example.mor17_000.eat2fit_app;

/**
 * Created by mor17_000 on 18-May-18.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;

public class RestTask extends AsyncTask<Void, Void, String>{
    private static final String TAG = "AARestTask";
    public static final String REST_RESPONSE = "restResponse";

    private Context mContext;
    private String apiUrl = "";
    private String mAction;
    private JSONObject jsonData = new JSONObject();


    public RestTask(Context context, String action)
    {
        mContext = context;
        mAction = action;
    }

    public void SetUrl(String url) {
        apiUrl = url;
    }

    public void SetJsonData(JSONObject jsonDatafromUser){
        jsonData = jsonDatafromUser;
    }
    @Override
    protected String doInBackground(Void... urls)
    {

        try {
            RestClient client = new RestClient();

            if(mAction == "GET"){
                return client.makeGetCall(apiUrl);
            }
            else{
                return client.makePostCall(apiUrl,jsonData);
            }

        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * `onPostExecute` is run after `doInBackground`, and it's
     * run on the main/ui thread, so you it's safe to update ui
     * components from it. (this is the correct way to update ui
     * components.)
     */
    @Override
    protected void onPostExecute(String result)
    {
        Log.i(TAG, "RESULT = " + result);
        Intent intent = new Intent(mAction);
        intent.putExtra(REST_RESPONSE, result);

        // broadcast the completion
        mContext.sendBroadcast(intent);
    }

}
