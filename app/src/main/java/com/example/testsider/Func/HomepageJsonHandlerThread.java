package com.example.testsider.Func;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HomepageJsonHandlerThread extends Thread {
    private static final String TAG = "JsonHandlerThread";
    // URL to get contacts JSON file

    private String languageCode = "en";

    static String jsonUrl = "https://api.open-meteo.com/v1/forecast?latitude=22.316668&longitude=114.183334&current=temperature_2m,relative_humidity_2m,is_day,precipitation,wind_gusts_10m&daily=temperature_2m_max,temperature_2m_min&timezone=Asia%2FSingapore&forecast_days=1";
    public void homejsonUrl(){
        jsonUrl = "https://api.open-meteo.com/v1/forecast?latitude=22.316668&longitude=114.183334&current=temperature_2m,relative_humidity_2m,is_day,precipitation,wind_gusts_10m&daily=temperature_2m_max,temperature_2m_min&timezone=Asia%2FSingapore&forecast_days=1";
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void homejsonUrl(String latitude, String longitude) {
        jsonUrl = "https://api.open-meteo.com/v1/forecast?latitude="+ latitude +"&longitude="
                + longitude + "&current=temperature_2m,relative_humidity_2m,is_day,precipitation,wind_gusts_10m&daily=temperature_2m_max,temperature_2m_min&timezone=Asia%2FSingapore&forecast_days=1"
                + "&current=temperature_2m,relative_humidity_2m,is_day,precipitation,wind_gusts_10m"
                + "&daily=temperature_2m_max,temperature_2m_min"
                + "&timezone=Asia%2FSingapore"
                + "&forecast_days=1"
                + "&lang=" + this.languageCode;
    }

    public static String makeRequest() {
        String response = null;
        try {
            URL url = new URL(jsonUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Read the response
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = inputStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    // download of JSON file from the url to the app
    private static String inputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }
        return sb.toString();
    }

    public void run() {
        // "contactStr" variable store the json file content
        String contactStr = makeRequest();
        Log.e(TAG, "Response from url: " + contactStr);

        if (contactStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(contactStr);

                // Getting JSON Array node
                JSONArray homeData = jsonObj.getJSONArray("homeData");

                // looping through All Contacts
                for (int i = 0; i < homeData.length(); i++) {
                    JSONObject c = homeData.getJSONObject(i);


                    JSONObject current = c.getJSONObject("current");
                    String date_time = current.getString("time");
                    String[] datetime = date_time.split("T");
                    String temp_avg = current.getString("temperature_2m");
                    String humidity = current.getString("relative_humidity_2m");
                    String wind_gusts_10m = current.getString("wind_gusts_10m");
                    String precipitation = current.getString("precipitation");

                    JSONObject daily = c.getJSONObject("current");
                    String temp_up = daily.getString("temperature_2m_max");
                    String temp_down = daily.getString("temperature_2m_min");


                    HomepageWeatherInfo.addhomeData(datetime[0], datetime[1], temp_avg, humidity, wind_gusts_10m, precipitation, temp_up, temp_down);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }
}