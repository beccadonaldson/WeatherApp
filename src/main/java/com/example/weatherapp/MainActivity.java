package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    TextView weatherData;

    EditText cityName;
    public class DownloadTask extends AsyncTask<String, Void, String>{
        @Override

        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                // input stream to gather data as its coming through
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                // keep moving onto next item til theres none left
                while(data != -1){
                    // taking in data letter by letter and adding it to the result
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
        @Override
        // string that comes through here is the string/url from doinbackground
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // when doinbackground is finished, this will run.
            // can make changes to UI here
            // take string and convert it to JSON data
            String addToView = "";
            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");
                //gives back an array of whats contained in 'weather' section of JSON

                JSONObject tempObject = (JSONObject) jsonObject.get("main");
                String temp = tempObject.get("temp").toString();

                double tempInt = Double.parseDouble(temp);

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    addToView = addToView + jsonPart.getString("main") + ": " + jsonPart.getString("description") + "\n";
                }
                DecimalFormat df = new DecimalFormat("#.##");
                addToView = addToView + "Temperature" + ": " + (df.format(tempInt-273)) + "°C" + "\n";


                weatherData.setText(addToView);
                weatherData.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                weatherData.setText("City doesn't exist");
                weatherData.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
        }
    }

    public void getWeather(View view) {
        DownloadTask task = new DownloadTask();
        String result = null;
        String city = cityName.getText().toString().toLowerCase();

        try{
            // add .get() to the end. this grabs the String at the end of the
            // <> above, the string returned when calling for the URL

            //how to turn spaces into %20 (space in ur terms) - should do this for you
            //String encodedCityName = URLEncoder.encode(cityName.getText().toString().toLowerCase(), "UTF-8");

            result = task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=2df7869b0edea354d0f127f2f0ff12f0").get();

        }catch(Exception e){
            Log.i("Error: ", "Could not get URL");
        }

        //get rid of keyboard when user clicks button
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherData = findViewById(R.id.weatherData);

        cityName = (EditText) findViewById(R.id.cityName);

    }
}