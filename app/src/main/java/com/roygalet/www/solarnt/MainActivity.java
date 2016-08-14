package com.roygalet.www.solarnt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {
    private WeatherList weatherList;
    private String[] suburbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        (findViewById(R.id.mainCardStats)).setVisibility(View.GONE);

        CardView yesCard = (CardView) findViewById(R.id.mainCardYes);
        yesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickYes();
            }
        });

        loadSuburbs();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, suburbs);
        final AutoCompleteTextView autoText = (AutoCompleteTextView) findViewById(R.id.mainAutoTextSuburb);
        autoText.setAdapter(adapter);
        autoText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, autoText.getText(),Toast.LENGTH_LONG).show();
                WeatherData weatherData = weatherList.getWeatherDataByDisplayName(autoText.getText().toString());
                ((TextView)findViewById(R.id.mainTextTopMonthlyRainfall)).setText(String.valueOf(Math.round(weatherData.getRainmean())));
                ((TextView)findViewById(R.id.mainTextTopDailySolarRadiation)).setText(String.valueOf(BigDecimal.valueOf(Double.valueOf(String.valueOf(weatherData.getSolarmean()))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()));
                ((TextView)findViewById(R.id.mainTextBottomTemperature)).setText(String.valueOf(Math.round(weatherData.getTempminmean())).concat("-").concat(String.valueOf(Math.round(weatherData.getTempmaxmean()))));
                View focusedView = MainActivity.this.getCurrentFocus();
                if (focusedView != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                (findViewById(R.id.mainCardStats)).setVisibility(View.VISIBLE);

                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.postcode_surburb), autoText.getText().toString());
                editor.commit();
            }
        });

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String postCodeSuburb = sharedPref.getString(getString(R.string.postcode_surburb),"");
        if(postCodeSuburb!=""){
            WeatherData weatherData = weatherList.getWeatherDataByDisplayName(postCodeSuburb);
            ((AutoCompleteTextView) findViewById(R.id.mainAutoTextSuburb)).setText(postCodeSuburb);
            ((TextView)findViewById(R.id.mainTextTopMonthlyRainfall)).setText(String.valueOf(Math.round(weatherData.getRainmean())));
            ((TextView)findViewById(R.id.mainTextTopDailySolarRadiation)).setText(String.valueOf(BigDecimal.valueOf(Double.valueOf(String.valueOf(weatherData.getSolarmean()))).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()));
            ((TextView)findViewById(R.id.mainTextBottomTemperature)).setText(String.valueOf(Math.round(weatherData.getTempminmean())).concat("-").concat(String.valueOf(Math.round(weatherData.getTempmaxmean()))));

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoText.getWindowToken(), 0);

            (findViewById(R.id.mainCardStats)).setVisibility(View.VISIBLE);
        }

        ((ImageButton)findViewById(R.id.mainButtonClear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoText.setText("");
                (findViewById(R.id.mainCardStats)).setVisibility(View.GONE);
            }
        });

        ((TextView)findViewById(R.id.mainTextMoreWeatherInformation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("weather", weatherList.getWeatherDataByDisplayName(autoText.getText().toString()));
                Intent intent = new Intent(MainActivity.this, Weather.class);
                intent.putExtra("weather", bundle);
                startActivity(intent);
            }
        });

        ((CardView)findViewById(R.id.mainCardNo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoActivity.class);
                WeatherData weatherData = weatherList.getWeatherDataByDisplayName(((TextView)findViewById(R.id.mainAutoTextSuburb)).getText().toString());
                if(weatherData!=null){
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("weather", weatherData);
                    intent.putExtra("weather", bundle);
                }
                startActivity(intent);
            }
        });
    }

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

    public void onClickYes() {
        Intent intent = new Intent(this, YesActivity.class);
        WeatherData weatherData = weatherList.getWeatherDataByDisplayName(((TextView)findViewById(R.id.mainAutoTextSuburb)).getText().toString());
        if(weatherData!=null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("weather", weatherData);
            intent.putExtra("weather", bundle);
        }
        startActivity(intent);
    }
}
