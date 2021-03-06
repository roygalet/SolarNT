package com.roygalet.www.solarnt;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Weather.WeatherData;
import Weather.WeatherList;

public class AppsActivity extends AppCompatActivity {
    private WeatherList weatherList;
    private String[] suburbs;
    JSONObject tempForecast = null;
    JSONObject radiationForecast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        loadSuburbs();

        try {
            tempForecast = new JSONObject(responseTemp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((ImageButton)findViewById(R.id.appsButtonHelp)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(AppsActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        new TemperatureReader().execute();
        new GeoLocator().execute();

        ((CardView)findViewById(R.id.appsCardDustDetect)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(AppsActivity.this, DustDetect.class);
                startActivity(intent);
            }
        });

        ((CardView)findViewById(R.id.appsCardWeather)).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(AppsActivity.this, WeatherActivity.class);
                WeatherData weatherData = weatherList.getWeatherDataByDisplayName("0800 Darwin");
                if(weatherData!=null){
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", weatherData);
                    bundle.putString("tempForecast", responseTemp);
                    intent.putExtra("weather", bundle);
                }
                startActivity(intent);
            }
        });

        ((CardView)findViewById(R.id.appsCardProjections)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppsActivity.this, ProjectionsActivity.class);
                WeatherData weatherData = weatherList.getWeatherDataByDisplayName("0800 Darwin");
                if(weatherData!=null){
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", weatherData);
                    intent.putExtra("weather", bundle);
                }
                startActivity(intent);
            }
        });

        ((CardView)findViewById(R.id.appsCardProviders)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppsActivity.this, ProvidersActivity.class));
            }
        });

        ((CardView)findViewById(R.id.appsCardOutputs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri = Uri.parse("http://138.80.64.225");
//                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                Intent intent = new Intent(AppsActivity.this, Monitor.class);
                startActivity(intent);
            }
        });
    }

    private class TemperatureReader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = "http://api.wunderground.com/api/a5d5665e6d63f78c/hourly/q/DRW.json";
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
//                    System.out.println("Response: > " + line);   //here u ll get whole responseTemp...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            JSONObject mainObject = null;
            try {
                responseTemp = aString;
                mainObject = (new JSONObject(aString)).getJSONArray("hourly_forecast").getJSONObject(0);
                ((ImageView)findViewById(R.id.appsImageWeather)).setImageResource(getResources().getIdentifier("@drawable/" + mainObject.getString("icon"), "id",getPackageName()));
                ((TextView)findViewById(R.id.appsTextTemp)).setText(mainObject.getJSONObject("temp").getInt("english") + " F\n" + mainObject.getJSONObject("temp").getInt("metric") + " C");
                tempForecast = mainObject;
                ((TextView)findViewById(R.id.appsTextForeCast)).setText(mainObject.getString("wx"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }



    private class GeoLocator extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = "http://api.wunderground.com/api/a5d5665e6d63f78c/geolookup/q/autoip.json";
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            JSONObject mainObject = null;
            try {
                mainObject = (new JSONObject(aString)).getJSONObject("location");
                ((TextView)findViewById(R.id.appsTextGeolocate)).setText(mainObject.getString("tz_long"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    String responseLocation = "{\n" +
            "    \"response\": {\n" +
            "        \"version\": \"0.1\"\n" +
            "        , \"termsofService\": \"http://www.wunderground.com/weather/api/d/terms.html\"\n" +
            "        , \"features\": {\n" +
            "            \"geolookup\": 1\n" +
            "        }\n" +
            "    }\n" +
            "    , \"location\": {\n" +
            "        \"type\": \"INTLCITY\"\n" +
            "        , \"country\": \"AU\"\n" +
            "        , \"country_iso3166\": \"AU\"\n" +
            "        , \"country_name\": \"Australia\"\n" +
            "        , \"state\": \"NTR\"\n" +
            "        , \"city\": \"Larrakeyah\"\n" +
            "        , \"tz_short\": \"ACST\"\n" +
            "        , \"tz_long\": \"Australia/Darwin\"\n" +
            "        , \"lat\": \"-12.457200\"\n" +
            "        , \"lon\": \"130.836594\"\n" +
            "        , \"zip\": \"00000\"\n" +
            "        , \"magic\": \"49\"\n" +
            "        , \"wmo\": \"94120\"\n" +
            "        , \"l\": \"/q/zmw:00000.49.94120\"\n" +
            "        , \"requesturl\": \"global/stations/94120.html\"\n" +
            "        , \"wuiurl\": \"https://www.wunderground.com/global/stations/94120.html\"\n" +
            "        \n" +
            "        }\n" +
            "    }\n" +
            "}";

    String responseTemp ="{\n" +
            "    \"response\": {\n" +
            "        \"version\": \"0.1\"\n" +
            "        , \"termsofService\": \"http://www.wunderground.com/weather/api/d/terms.html\"\n" +
            "        , \"features\": {\n" +
            "            \"hourly\": 1\n" +
            "        }\n" +
            "    }\n" +
            "    , \"hourly_forecast\": [\n" +
            "        {\n" +
            "            \"FCTTIME\": {\n" +
            "                \"hour\": \"13\"\n" +
            "                , \"hour_padded\": \"13\"\n" +
            "                , \"min\": \"30\"\n" +
            "                , \"min_unpadded\": \"30\"\n" +
            "                , \"sec\": \"0\"\n" +
            "                , \"year\": \"2017\"\n" +
            "                , \"mon\": \"3\"\n" +
            "                , \"mon_padded\": \"03\"\n" +
            "                , \"mon_abbrev\": \"Mar\"\n" +
            "                , \"mday\": \"12\"\n" +
            "                , \"mday_padded\": \"12\"\n" +
            "                , \"yday\": \"70\"\n" +
            "                , \"isdst\": \"0\"\n" +
            "                , \"epoch\": \"1489291200\"\n" +
            "                , \"pretty\": \"1:30 PM ACST on March 12, 2017\"\n" +
            "                , \"civil\": \"1:30 PM\"\n" +
            "                , \"month_name\": \"March\"\n" +
            "                , \"month_name_abbrev\": \"Mar\"\n" +
            "                , \"weekday_name\": \"Sunday\"\n" +
            "                , \"weekday_name_night\": \"Sunday Night\"\n" +
            "                , \"weekday_name_abbrev\": \"Sun\"\n" +
            "                , \"weekday_name_unlang\": \"Sunday\"\n" +
            "                , \"weekday_name_night_unlang\": \"Sunday Night\"\n" +
            "                , \"ampm\": \"PM\"\n" +
            "                , \"tz\": \"\"\n" +
            "                , \"age\": \"\"\n" +
            "                , \"UTCDATE\": \"\"\n" +
            "            }\n" +
            "            , \"temp\": {\n" +
            "                \"english\": \"89\"\n" +
            "                , \"metric\": \"32\"\n" +
            "            }\n" +
            "            , \"dewpoint\": {\n" +
            "                \"english\": \"74\"\n" +
            "                , \"metric\": \"23\"\n" +
            "            }\n" +
            "            , \"condition\": \"Mostly Cloudy\"\n" +
            "            , \"icon\": \"mostlycloudy\"\n" +
            "            , \"icon_url\": \"http://icons.wxug.com/i/c/k/mostlycloudy.gif\"\n" +
            "            , \"fctcode\": \"3\"\n" +
            "            , \"sky\": \"72\"\n" +
            "            , \"wspd\": {\n" +
            "                \"english\": \"7\"\n" +
            "                , \"metric\": \"11\"\n" +
            "            }\n" +
            "            , \"wdir\": {\n" +
            "                \"dir\": \"W\"\n" +
            "                , \"degrees\": \"259\"\n" +
            "            }\n" +
            "            , \"wx\": \"Mostly Cloudy\"\n" +
            "            , \"uvi\": \"11\"\n" +
            "            , \"humidity\": \"62\"\n" +
            "            , \"windchill\": {\n" +
            "                \"english\": \"-9999\"\n" +
            "                , \"metric\": \"-9999\"\n" +
            "            }\n" +
            "            , \"heatindex\": {\n" +
            "                \"english\": \"98\"\n" +
            "                , \"metric\": \"37\"\n" +
            "            }\n" +
            "            , \"feelslike\": {\n" +
            "                \"english\": \"98\"\n" +
            "                , \"metric\": \"37\"\n" +
            "            }\n" +
            "            , \"qpf\": {\n" +
            "                \"english\": \"0.0\"\n" +
            "                , \"metric\": \"0\"\n" +
            "            }\n" +
            "            , \"snow\": {\n" +
            "                \"english\": \"0.0\"\n" +
            "                , \"metric\": \"0\"\n" +
            "            }\n" +
            "            , \"pop\": \"24\"\n" +
            "            , \"mslp\": {\n" +
            "                \"english\": \"29.76\"\n" +
            "                , \"metric\": \"1008\"\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    String googleURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=-12.457200,130.836594&key=AIzaSyDE-clfKy0WQBj6V6BGMS3YTzi6vrHZiBQ";

    private void loadSuburbs(){
        InputStreamReader is = null;
        try {
            is = new InputStreamReader(getAssets().open("data.csv"));
            BufferedReader reader = new BufferedReader(is);
            weatherList = new WeatherList();
            try {
                reader.readLine();
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split(",");
                        weatherList.addWeatherData(Integer.parseInt(data[0]), "0".concat(data[1]), data[2], Float.parseFloat(data[3]), Float.parseFloat(data[4]), Integer.parseInt(data[5]), data[6], Float.parseFloat(data[7]), Float.parseFloat(data[8]), Float.parseFloat(data[9]), Float.parseFloat(data[10]), Integer.parseInt(data[11]), data[12], Float.parseFloat(data[13]), Float.parseFloat(data[14]), Float.parseFloat(data[15]), Float.parseFloat(data[16]), Integer.parseInt(data[17]), data[18], Float.parseFloat(data[19]), Float.parseFloat(data[20]), Float.parseFloat(data[21]), Float.parseFloat(data[22]), Float.parseFloat(data[23]), Float.parseFloat(data[24]), Float.parseFloat(data[25]));
                    }
                    suburbs = new String[weatherList.getCount()];
                    for(int index = 0; index < suburbs.length; index++){
                        suburbs[index] = weatherList.getWeatherDataByIndex(index).getPostcode().concat(" ").concat(weatherList.getWeatherDataByIndex(index).getSuburb());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
