package com.roygalet.www.solarnt;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
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

import static android.R.attr.value;

public class WeatherActivity extends AppCompatActivity {
    WebView webView;
    JSONObject tempForecast;
    WeatherData weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather2);
        new RadiationReader().execute();

        Bundle bundle = getIntent().getExtras();
        weatherData = bundle.getBundle("weather").getParcelable("weather");
        ((TextView)findViewById(R.id.weatherTextPostCodeSuburb)).setText(weatherData.getPostcode() + " " + weatherData.getSuburb());
        ((TextView)findViewById(R.id.weatherTextRadiation)).setText(weatherData.getSolarmean() + "");
        try {
            tempForecast = (new JSONObject(bundle.getBundle("weather").getString("tempForecast"))).getJSONArray("hourly_forecast").getJSONObject(0);
            ((ImageView)findViewById(R.id.appsImageWeather)).setImageResource(getResources().getIdentifier("@drawable/" + tempForecast.getString("icon"), "id",getPackageName()));
            ((TextView)findViewById(R.id.appsTextTemp)).setText(tempForecast.getJSONObject("temp").getInt("english") + " F\n" + tempForecast.getJSONObject("temp").getInt("metric") + " C");
            ((TextView)findViewById(R.id.appsTextForeCast)).setText(tempForecast.getString("wx"));

//            System.out.println(tempForecast);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int temp = 0;
        float min = weatherData.getTempmindry();
        float max = weatherData.getTempmaxwet();

        try {
            temp = tempForecast.getJSONObject("temp").getInt("metric");
        } catch (JSONException e) {
            temp = (int) (max+min)/2;
            e.printStackTrace();
        }

        webView = (WebView)findViewById(R.id.weatherWebGuage);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadDataWithBaseURL("", generateGauge(temp, min, max), "text/html", "UTF-8", "");
    }

    String generateGauge(int value, float min, float max){
        String htmlTemp = "<html>\n" +
                "  <head>\n" +
                "   <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "   <script type=\"text/javascript\">\n" +
                "      google.charts.load('current', {'packages':['gauge']});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      function drawChart() {\n" +
                "\n" +
                "        var data = google.visualization.arrayToDataTable([\n" +
                "          ['Label', 'Value'],\n" +
                "          ['Temp C', " + value + "],\n" +
                "        ]);\n" +
                "\n" +
                "        var options = {min:"+Math.floor(min)+",max:"+Math.ceil(max)+",greenColor:'#86d4f1',redColor:'#eb9c71', redFrom: " + (max-(max-min)/3) + ", redTo: " + (max) + ", greenFrom:"+(min)+", greenTo: "+(min + (max-min)/3)+", minorTicks: 5};\n" +
                "\n" +
                "        var chart = new google.visualization.Gauge(document.getElementById('chart_div'));\n" +
                "\n" +
                "        chart.draw(data, options);\n" +
                "\n" +
                "        setInterval(function() {\n" +
                "          data.setValue(0, 1, "+value+" + Math.round(0 * Math.random()));\n" +
                "          chart.draw(data, options);\n" +
                "        }, 13000);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"chart_div\" style=\"width: 100%; height: 100%;text-align:center;\"></div>\n" +
                "  </body>\n" +
                "</html>";

        return htmlTemp;
    }

    String responseRadiation = "{\"forecasts\":[{\"ghi\":0,\"ghi90\":0,\"ghi10\":0,\"ebh\":0,\"dni\":0,\"dni10\":0,\"dni90\":0,\"dhi\":0,\"air_temp\":17,\"zenith\":132,\"azimuth\":-137,\"cloud_opacity\":79,\"period_end\":\"2017-03-13T16:30:00.0000000Z\",\"period\":\"PT30M\"}]}";

    private class RadiationReader extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String urlString = "https://api.solcast.com.au/radiation/forecasts?longitude=149.117&latitude=-35.277&api_key=f_uSmb6y_aXCua_Wal8UJXKMdDK_JmGi&format=json";
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
                responseRadiation = aString;
                System.out.println(responseRadiation);
                mainObject = (new JSONObject(aString)).getJSONArray("forecasts").getJSONObject(0);
                WebView webViewRadiation = (WebView)findViewById(R.id.weatherWebGuageRadiation);
                webViewRadiation.getSettings().setJavaScriptEnabled(true);
                webViewRadiation.loadDataWithBaseURL("", generateRadiation(mainObject.getInt("dni"), 0, 1000), "text/html", "UTF-8", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    String generateRadiation(int value, float min, float max){
        String htmlTemp = "<html>\n" +
                "  <head>\n" +
                "   <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n" +
                "   <script type=\"text/javascript\">\n" +
                "      google.charts.load('current', {'packages':['gauge']});\n" +
                "      google.charts.setOnLoadCallback(drawChart);\n" +
                "\n" +
                "      function drawChart() {\n" +
                "\n" +
                "        var data = google.visualization.arrayToDataTable([\n" +
                "          ['Label', 'Value'],\n" +
                "          ['Solar Radiation', " + value + "],\n" +
                "        ]);\n" +
                "\n" +
                "        var options = {min:"+Math.floor(min)+",max:"+Math.ceil(max)+",redColor:'#86d4f1',greenColor:'#eb9c71', redFrom: " + (max-(max-min)/3) + ", redTo: " + (max) + ", greenFrom:"+(min)+", greenTo: "+(min + (max-min)/3)+", minorTicks: 5};\n" +
                "\n" +
                "        var chart = new google.visualization.Gauge(document.getElementById('chart_div'));\n" +
                "\n" +
                "        chart.draw(data, options);\n" +
                "\n" +
                "        setInterval(function() {\n" +
                "          data.setValue(0, 1, "+value+" + Math.round(0 * Math.random()));\n" +
                "          chart.draw(data, options);\n" +
                "        }, 13000);\n" +
                "      }\n" +
                "    </script>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"chart_div\" style=\"width: 100%; height: 100%;text-align:center;\"></div>\n" +
                "  </body>\n" +
                "</html>";

        return htmlTemp;
    }
}
