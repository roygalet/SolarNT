package com.roygalet.www.solarnt;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DailyActivity extends AppCompatActivity {
    private String data;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PVDataDownloader dl = new PVDataDownloader();

        barChart = (BarChart)findViewById(R.id.dailyBarChart);

        dl.execute();

    }

    private class PVDataDownloader extends AsyncTask<String, Integer, String> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute()
        {
            data = "";
            pDialog = new ProgressDialog(DailyActivity.this);
            pDialog.setMessage("Processing Request...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(String... urls) {
            String urlStr =
                    "http://pvoutput.org/service/r2/getoutput.jsp?sid=47892&key=cb51d79dd8b6c0944f235ea29e735cfc74bf7136";
            URL url = null;
            String data = "";
            try {
                url = new URL(urlStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn =
                    null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                data = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();

            try
            {
                data=result;
                String[] records = data.split(";");
                String[][] pvoutput = new String[records.length][];
                ArrayList<BarEntry> powerData = new ArrayList<>();
                ArrayList<String> dateData = new ArrayList<>();
                int year,month,day;
                for(int index=0; index<7; index++){

                    pvoutput[index] = records[6-index].split(",");
                    powerData.add(new BarEntry(Float.valueOf( pvoutput[index][1]),index));

                    year = Integer.parseInt(pvoutput[index][0].substring(0, 4));
                    month = Integer.parseInt(pvoutput[index][0].substring(4, 6));
                    day = Integer.parseInt(pvoutput[index][0].substring(6));

                    Calendar cal = Calendar.getInstance();
                    cal.set(year, month-1, day);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
                    String dateLabel = df.format(cal.getTime());

                    dateData.add(dateLabel);
                }
                BarDataSet barDataSet = new BarDataSet(powerData,"Power Generated");
                barDataSet.setColor(Color.argb(128,255,255,255));

                BarData barData = new BarData(dateData, barDataSet);
//                barData.setBarWidth(0.9f);
                barChart.setData(barData);
                barData.setValueTextColor(Color.WHITE);

//                barChart.setFitBars(true);
                XAxis xAxis = barChart.getXAxis();
                barChart.getAxisLeft().setDrawGridLines(false);
                barChart.getXAxis().setDrawGridLines(false);
                barChart.getXAxis().setTextColor(Color.WHITE);
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGridColor(Color.WHITE);
                barChart.getAxisLeft().setGridColor(Color.WHITE);
                barChart.setDescription("");
                barChart.getLegend().setTextColor(Color.WHITE);
                barChart.getAxisLeft().setTextColor(Color.WHITE);
                barChart.getAxisRight().setEnabled(false);
                barChart.setTouchEnabled(true);
                barChart.setDragEnabled(true);
                barChart.invalidate();
//
//                TextView txtData = (TextView)findViewById(R.id.txtData);
//                txtData.setText(records.length + " records found");
            }
            catch (Exception e)
            {

            }
            super.onPostExecute(result);
        }
    }

}
