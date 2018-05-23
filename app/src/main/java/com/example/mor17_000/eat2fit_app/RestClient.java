package com.example.mor17_000.eat2fit_app;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RestClient {
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    public String makeGetCall(String baseurl) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            URL url = new URL(baseurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                //Set methods and timeouts
                urlConnection.setRequestMethod(REQUEST_METHOD_GET);
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                urlConnection.connect();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return stringBuilder.toString();
        }
    }

    public String makePostCall(String baseurl, JSONObject dataJsonObj) {

        String newUserId = new String();
        try {
            // convert JSONObject to JSON to String
            String json = dataJsonObj.toString();

            URL url = new URL(baseurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                //Set methods and timeouts
                urlConnection.setRequestMethod(REQUEST_METHOD_POST);
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setReadTimeout(READ_TIMEOUT);
                urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);

                OutputStream os = urlConnection.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.close();

                // read the response
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                newUserId = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                in.close();
                return newUserId;

            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return newUserId;
        }
    }

}

