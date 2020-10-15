package com.example.sampleweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private TextView cityName, resultTextView;
    private Button searchButton;

    class Weather extends AsyncTask<String, Void, String> { // 1st String means URL is an string, Void means nothing, 3rd String means return type will be in string.

        @Override

        protected String doInBackground(String... address) {

            //String... means multiple address can be sent. It acts as array.
            try {

                URL url = new URL(address[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                //Establish connection with address
                httpURLConnection.connect();

                //retrieve data from url
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                //Retrieve data and return it as String
                int data = inputStreamReader.read();
                String content = "";
                char ch;

                while (data != -1){

                    ch = (char) data;
                    content = content + ch;
                    data = inputStreamReader.read();
                }
                return content;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public void search(View view) {

        cityName = findViewById(R.id.cityName);
        resultTextView = findViewById(R.id.result);
        searchButton = findViewById(R.id.searchButton);

        String cname = cityName.getText().toString().trim();
        String content;

        Weather weather = new Weather();

        if (cname.isEmpty()) {

            cityName.setError("Please type a city name.");
            cityName.requestFocus();

            return;
        }

        try {

            content = weather.execute("https://openweathermap.org/data/2.5/weather?q=" + cname + "&appid=439d4b804bc8187953eb36d2a8c26a02").get();

            // At first we will check data is retrieved successfully or not.
            Log.i("ContentData", content);

            // JSON
            JSONObject jsonObject = new JSONObject(content);

            String weatherData = jsonObject.getString("weather");
            String mainTemperature  = jsonObject.getString("main"); //This main is not a part of weather array. It's a separate variable like weather.
            double visibility;
            Log.i("WeatherData", weatherData);

            // Weather data is in array.
            JSONArray jsonArray = new JSONArray(weatherData);

            String main = "", description = "", temperature = "";

            for (int i = 0; i<jsonArray.length(); i++) {

                JSONObject weatherPart = jsonArray.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");
            }

            JSONObject mainPart = new JSONObject(mainTemperature);
            temperature = mainPart.getString("temp");

            visibility = Double.parseDouble(jsonObject.getString("visibility"));

            //By default, visibility is in metre unit.Now we'll convert it into kilometre.
            int visibilityKM = (int) (visibility/1000);

            Log.i("Main", main);
            Log.i("Description", description);
            Log.i("Temperature", temperature);

            String resultText = "Main : " + main + "\n" +
                    "Description : " + description + "\n" +
                    "Temperature : " + temperature + "°C" + "\n" +
                    "Visibility : "+visibilityKM + "KM";
            resultTextView.setText(resultText);

            // Now we will show these values on screen.
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
