package PVOutputData;

import android.text.TextUtils;
import android.widget.Toast;

import com.roygalet.www.solarnt.Monitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by royga on 5/11/2016.
 */
public class PVSystemsCollection {

    private PVAccountSettings mySettings;
    private PVSystem mySystem;
    private HashMap pvSystems;

    public PVSystemsCollection(PVAccountSettings mySettings) {
        this.mySettings = mySettings;
        pvSystems = new HashMap();
    }

    public String getMySystemName() {
        String mySystemName = "";

        String url = "http://pvoutput.org/service/r2/getsystem.jsp";
        url = url.concat("?sid=").concat(String.valueOf(mySettings.getSystemID()));
        url = url.concat("&key=").concat(mySettings.getKey());

        try {
            URL pvURL = new URL(url);
            URLConnection urlConnection = pvURL.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(pvURL.openStream()));

            String inputLine;
            inputLine = bufferedReader.readLine();
            String[] fields = inputLine.split(",");

            mySystemName = fields[0];
            mySettings.setPostCode(Integer.parseInt(fields[2]));


            bufferedReader.close();

        } catch (MalformedURLException ex) {
            Logger.getLogger(PVSystemsCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ArrayIndexOutOfBoundsException ex) {

        }
        return mySystemName;
    }

    public HashMap getNearbySystemsWithLatestOutputs(int distance, int maxNumber, boolean latestOnly) {
        String mySystemName = getMySystemName();
        String url = "http://pvoutput.org/service/r2/search.jsp";
        url = url.concat("?sid=").concat(String.valueOf(mySettings.getSystemID()));
        url = url.concat("&key=").concat(mySettings.getKey());
        url = url.concat("&q=").concat(String.valueOf(mySettings.getPostCode())).concat("%20").concat(String.valueOf(distance)).concat("km");

        System.out.println(url);
        try {
            URL pvURL = new URL(url);
            URLConnection urlConnection = pvURL.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(pvURL.openStream()));

            String inputLine;
            pvSystems.clear();
            while ((inputLine = bufferedReader.readLine()).trim().length() > 0) {
                PVSystem currentSystem = new PVSystem(inputLine, mySettings);
                if (currentSystem.getName().compareToIgnoreCase(mySystemName) == 0) {
                    mySystem = currentSystem;
                    mySystem.retrieveDailyData(mySettings);
                    mySystem.retrieveMonthlyData(mySettings);
                    mySystem.retriveYearlyData(mySettings);
                }else{
                    if (latestOnly && !(currentSystem.getLastOutput().compareToIgnoreCase("Today") == 0 || currentSystem.getLastOutput().compareToIgnoreCase("Yesterday") == 0)) {
                        continue;
                    }
                    pvSystems.put(currentSystem.getName(), currentSystem);
                }
            }

            while(pvSystems.size()>maxNumber){
                PVSystem largest = (PVSystem) pvSystems.get(pvSystems.keySet().toArray()[0]);
                for(int i = 1; i < pvSystems.size(); i++){
                    PVSystem currentSystem = (PVSystem) pvSystems.get(pvSystems.keySet().toArray()[i]);
                    Long currentSize = currentSystem.getSize();
                    currentSize = Math.abs(currentSize-mySystem.getSize());
                    Long largestSize = Math.abs(largest.getSize()-mySystem.getSize());
                    if(currentSize > largestSize){
                        largest = currentSystem;
                    }
                }
                pvSystems.remove(largest.getName());
            }

            for(int i=0; i<pvSystems.size(); i++){
                PVSystem currentSystem = (PVSystem) pvSystems.get(pvSystems.keySet().toArray()[i]);
                currentSystem.retrieveDailyData(mySettings);
                currentSystem.retrieveMonthlyData(mySettings);
                currentSystem.retriveYearlyData(mySettings);
            }

            bufferedReader.close();

        } catch (MalformedURLException ex) {
            Logger.getLogger(PVSystemsCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PVSystemsCollection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArrayIndexOutOfBoundsException ex) {
            Logger.getLogger(PVSystemsCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pvSystems;
    }

    private String generatedHeader(){
        return "<html>\n"
                + "\n"
                + "<head>\n"
                + "	<!--Load the AJAX API-->\n"
                + "	<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n"
                + "	<script type=\"text/javascript\">\n"
                + "		// Load the Visualization API and the corechart package.\n"
                + "		google.charts.load('current', {\n"
                + "			'packages': ['corechart']\n"
                + "		});\n"
                + "		// Set a callback to run when the Google Visualization API is loaded.\n"
                + "		google.charts.setOnLoadCallback(drawChart);\n"
                + "		// Callback that creates and populates a data table,\n"
                + "		// instantiates the pie chart, passes in the data and\n"
                + "		// draws it.\n"
                + "		function drawChart() {\n"
                + "			// Create the data table.\n"
                + "			var data = new google.visualization.DataTable();\n"
                + "			data.addColumn('string', 'Date');\n"
                + "			data.addColumn('number', 'Power Generated');\n"
                + "			data.addColumn('number', 'Efficiency');\n"
                + "			data.addRows([";
    }

    private String generatedFooter(){
        return "]);\n"
                + "			// Set chart options\n"
                + "			var options = {chartArea:{left:50,top:20,width:'80%',height:'80%'}, backgroundColor: 'transparent' ,\n"
                + "					colors: ['rgb(204, 255, 102)','rgb(51, 153, 51)'],\n"
                + "				'i3D':true,\n"
                + "				legend: { position: 'top', alignment: 'center' }\n"
                + "				, vAxis: {\n"
                + "					title: 'Power Generated kWh',\n"
                + "					format: '0',\n"
                + "					minValue:0\n"
                + "				}\n"
                + "				, hAxis: {\n"
                + "					lineWidth: 4\n"
                + "				}\n"
                + "				, seriesType: 'bars'\n"
                + "				, series: {\n"
                + "					1: {\n"
                + "						type: 'line',\n"
                + "						curveType: 'function',\n"
                + "						targetAxisIndex:1\n"
                + "					}\n"
                + "					, 'bar': {\n"
                + "						'groupWidth': \"61.8%\"\n"
                + "					}\n"
                + "				},\n"
                + "				vAxes: {\n"
                + "				1: {\n"
                + "				  title:'Efficiency kWh/kW',\n"
                + "				  format: '0',\n"
                + "					minValue:0\n"
                + "				}\n"
                + "			  }\n"
                + "			};\n"
                + "			// Instantiate and draw our chart, passing in some options.\n"
                + "			var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));\n"
                + "			chart.draw(data, options);\n"
                + "					}\n"
                + "	</script>\n"
                + "</head>\n"
                + "\n"
                + "<body>\n"
                + "	<!--Div that will hold the pie chart-->\n"
                + "	<div id=\"chart_div\" style=\"width:100%; height:100%\"></div>\n"
                + "</body>\n"
                + "\n"
                + "</html>";
    }
    public String generateMyDailyData(int numberOfDays) {
        String html = generatedHeader();
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        float power, efficiency;
        String year, month, day;
        String[] data = new String[numberOfDays];
        cal.setTime(date);
        cal.add(Calendar.DATE, -numberOfDays);
        for (int i = 0; i < numberOfDays; i++) {
            cal.add(Calendar.DATE, 1);
            year = String.valueOf(cal.get(Calendar.YEAR));
            month = String.valueOf(cal.get(Calendar.MONTH) + 1);
            if (month.length() < 2) {
                month = "0".concat(month);
            }
            day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if (day.length() < 2) {
                day = "0".concat(day);
            }
            if (mySystem.getDailyData().get(year.concat(month).concat(day)) == null) {
                power = 0;
                efficiency = 0;
            } else {
                power = ((PVDailyData) mySystem.getDailyData().get(year.concat(month).concat(day))).getEnergyGenerated() / 1000;
                efficiency = ((PVDailyData) mySystem.getDailyData().get(year.concat(month).concat(day))).getEfficiency();
            }
            String dateLabel = "";
            if (i == 0) {
                dateLabel = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)).concat(" ").concat((new DateFormatSymbols()).getMonths()[cal.get(Calendar.MONTH)].substring(0, 3)).concat(" ").concat(String.valueOf(cal.get(Calendar.YEAR)));
            } else if (i == numberOfDays - 1) {
                dateLabel = "Today";
            }else{
                dateLabel = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            }
            data[i] = "['".concat(dateLabel).concat("',").concat(String.valueOf(power)).concat(",").concat(String.valueOf(efficiency)).concat("]");
        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generatedFooter());
        return html;
    }

    public String generateMyMonthlyData(int numberOfMonths) {
        String html = generatedHeader();
        float power, efficiency;
        String year, month;
        if (numberOfMonths > mySystem.getMonthlyData().size()) {
            numberOfMonths = mySystem.getMonthlyData().size();
        }
        String[] data = new String[numberOfMonths];

        Set<String> keys = mySystem.getMonthlyData().keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        String temp = "";
        for (int i = 0; i < keys.size() - 1; i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                if (Integer.parseInt(array[i]) > Integer.parseInt(array[j])) {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }

        for (int i = array.length-numberOfMonths; i < array.length; i++) {
            year = array[i].substring(0, 4);
            month = array[i].substring(4);
            if (month.length() < 2) {
                month = "0".concat(month);
            }
            if (mySystem.getMonthlyData().get(year.concat(month)) == null) {
                power = 0;
                efficiency = 0;
            } else {
                power = ((PVSummarizedData) mySystem.getMonthlyData().get(year.concat(month))).getEnergyGenerated() / 1000;
                efficiency = ((PVSummarizedData) mySystem.getMonthlyData().get(year.concat(month))).getEfficiency();
            }
            String dateLabel = "";
            if (i == array.length-numberOfMonths) {
                dateLabel = (new DateFormatSymbols()).getMonths()[Integer.parseInt(month)-1].substring(0, 3).concat(" ").concat(year);
            } else if (i == array.length - 1) {
                dateLabel = "This Month";
            } else{
                dateLabel = (new DateFormatSymbols()).getMonths()[Integer.parseInt(month)-1].substring(0, 3);
            }
            data[i-array.length+numberOfMonths] = "['".concat(dateLabel).concat("',").concat(String.valueOf(power)).concat(",").concat(String.valueOf(efficiency)).concat("]");
        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generatedFooter());
        return html;
    }

    public String generateMyYearlyData(int numberOfYears) {
        String html = generatedHeader();
        float power, efficiency;
        String year;
        if (numberOfYears > mySystem.getYearlyData().size()) {
            numberOfYears = mySystem.getYearlyData().size();
        }
        String[] data = new String[numberOfYears];

        Set<String> keys = mySystem.getYearlyData().keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        String temp = "";
        for (int i = 0; i < keys.size() - 1; i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                if (Integer.parseInt(array[i]) > Integer.parseInt(array[j])) {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }

        for (int i = array.length-numberOfYears; i < array.length; i++) {
            year = array[i];

            if (mySystem.getYearlyData().get(year) == null) {
                power = 0;
                efficiency = 0;
            } else {
                power = ((PVSummarizedData) mySystem.getYearlyData().get(year)).getEnergyGenerated() / 1000;
                efficiency = ((PVSummarizedData) mySystem.getYearlyData().get(year)).getEfficiency();
            }
            String dateLabel = "";
            dateLabel = year;
            data[i-array.length+numberOfYears] = "['".concat(dateLabel).concat("',").concat(String.valueOf(power)).concat(",").concat(String.valueOf(efficiency)).concat("]");
        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generatedFooter());
        return html;
    }

    private String generateHeader(String systemName){
        return "<html>\n"
                + "\n"
                + "<head>\n"
                + "	<!--Load the AJAX API-->\n"
                + "	<script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>\n"
                + "	<script type=\"text/javascript\">\n"
                + "		// Load the Visualization API and the corechart package.\n"
                + "		google.charts.load('current', {\n"
                + "			'packages': ['corechart']\n"
                + "		});\n"
                + "		// Set a callback to run when the Google Visualization API is loaded.\n"
                + "		google.charts.setOnLoadCallback(drawChart);\n"
                + "		// Callback that creates and populates a data table,\n"
                + "		// instantiates the pie chart, passes in the data and\n"
                + "		// draws it.\n"
                + "		function drawChart() {\n"
                + "			// Create the data table.\n"
                + "			var data = new google.visualization.DataTable();\n"
                + "			data.addColumn('string', 'Date');\n"
                + "			data.addColumn('number', 'My System');\n"
                + "			data.addColumn('number', '');\n"
                + "			data.addColumn('number', '" + systemName + "');\n"
                + "			data.addColumn('number', '');\n"
                + "			data.addRows([";
    }

    private String generateFooter(String systemName){
        return  "]);\n"
                + "			// Set chart options\n"
                + "			var options = {chartArea:{left:50,top:20,width:'80%',height:'80%'},\n"
                + "					colors: ['rgb(204, 255, 102)','rgb(51, 153, 51)','rgb(255, 142, 142)','rgb(153, 51, 51)'],\n"
                + "				'i3D':true,\n"
                + "				legend: { position: 'top', alignment: 'center' }\n"
                + "				, vAxis: {\n"
                + "					title: 'Power Generated kWh',\n"
                + "					format: '0',\n"
                + "					minValue:0\n"
                + "				}\n"
                + "				, hAxis: {\n"
                + "					lineWidth: 4\n"
                + "				}\n"
                + "				, seriesType: 'bars'\n"
                + "				, series: {\n"
                + "					1: {\n"
                + "						type: 'line',\n"
                + "						curveType: 'function',\n"
                + "						targetAxisIndex:1\n"
                + "					}\n"
                + "					,3: {\n"
                + "						type: 'line',\n"
                + "						curveType: 'function',\n"
                + "						targetAxisIndex:1\n"
                + "					}\n"
                + "					, 'bar': {\n"
                + "						'groupWidth': \"95%\"\n"
                + "					}\n"
                + "				},\n"
                + "				vAxes: {\n"
                + "				1: {\n"
                + "				  title:'Efficiency kWh/kW',\n"
                + "				  format: '0',\n"
                + "					minValue:0\n"
                + "				}\n"
                + "			  }\n"
                + "			};\n"
                + "			// Instantiate and draw our chart, passing in some options.\n"
                + "			var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));\n"
                + "			chart.draw(data, options);\n"
                + "					}\n"
                + "	</script>\n"
                + "</head>\n"
                + "\n"
                + "<body>\n"
                + "	<!--Div that will hold the pie chart-->\n"
                + "	<div id=\"chart_div\" style=\"width:100%; height:100%\"></div>\n"
                + "</body>\n"
                + "\n"
                + "</html>";

    }

    public String compareDaily(String systemName, int numberOfDays) {
        String html = generateHeader(systemName);
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        float myPower, myEfficiency, sysPower, sysEfficiency;
        String year, month, day;
        String[] data = new String[numberOfDays];
        cal.setTime(date);
        cal.add(Calendar.DATE, -numberOfDays);

        PVSystem otherSystem = (PVSystem) pvSystems.get(systemName);

        for (int i = 0; i < numberOfDays; i++) {
            cal.add(Calendar.DATE, 1);
            year = String.valueOf(cal.get(Calendar.YEAR));
            month = String.valueOf(cal.get(Calendar.MONTH) + 1);
            if (month.length() < 2) {
                month = "0".concat(month);
            }
            day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            if (day.length() < 2) {
                day = "0".concat(day);
            }
            if (mySystem.getDailyData().get(year.concat(month).concat(day)) == null) {
                myPower = 0;
                myEfficiency = 0;
            } else {
                myPower = ((PVDailyData) mySystem.getDailyData().get(year.concat(month).concat(day))).getEnergyGenerated() / 1000;
                myEfficiency = ((PVDailyData) mySystem.getDailyData().get(year.concat(month).concat(day))).getEfficiency();
            }

            if (otherSystem.getDailyData().get(year.concat(month).concat(day)) == null) {
                sysPower = 0;
                sysEfficiency = 0;
            } else {
                sysPower = ((PVDailyData) otherSystem.getDailyData().get(year.concat(month).concat(day))).getEnergyGenerated() / 1000;
                sysEfficiency = ((PVDailyData) otherSystem.getDailyData().get(year.concat(month).concat(day))).getEfficiency();
            }

            String dateLabel = "";
            if (i % 5 == 0) {
                dateLabel = String.valueOf(cal.get(Calendar.DAY_OF_MONTH)).concat(" ").concat((new DateFormatSymbols()).getMonths()[cal.get(Calendar.MONTH)].substring(0, 3));
            } else if (i == numberOfDays - 1) {
                dateLabel = "Today";
            }
            data[i] = "['".concat(dateLabel).concat("',").concat(String.valueOf(myPower)).concat(",").concat(String.valueOf(myEfficiency)).concat(",").concat(String.valueOf(sysPower)).concat(",").concat(String.valueOf(sysEfficiency)).concat("]");

        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generateFooter(systemName));
        return html;
    }

    public String compareMonthly(String systemName, int numberOfMonths) {
        String html = generateHeader(systemName);
        float myPower, myEfficiency,sysPower, sysEfficiency;
        String year, month;
        if (numberOfMonths > mySystem.getMonthlyData().size()) {
            numberOfMonths = mySystem.getMonthlyData().size();
        }
        String[] data = new String[numberOfMonths];

        Set<String> keys = mySystem.getMonthlyData().keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        String temp = "";
        for (int i = 0; i < keys.size() - 1; i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                if (Integer.parseInt(array[i]) > Integer.parseInt(array[j])) {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }

        PVSystem otherSystem = (PVSystem) pvSystems.get(systemName);

        for (int i = array.length-numberOfMonths; i < array.length; i++) {
            year = array[i].substring(0, 4);
            month = array[i].substring(4);
            if (month.length() < 2) {
                month = "0".concat(month);
            }
            if (mySystem.getMonthlyData().get(year.concat(month)) == null) {
                myPower = 0;
                myEfficiency = 0;
            } else {
                myPower = ((PVSummarizedData) mySystem.getMonthlyData().get(year.concat(month))).getEnergyGenerated() / 1000;
                myEfficiency = ((PVSummarizedData) mySystem.getMonthlyData().get(year.concat(month))).getEfficiency();
            }

            if (otherSystem.getMonthlyData().get(year.concat(month)) == null) {
                sysPower = 0;
                sysEfficiency = 0;
            } else {
                sysPower = ((PVSummarizedData) otherSystem.getMonthlyData().get(year.concat(month))).getEnergyGenerated() / 1000;
                sysEfficiency = ((PVSummarizedData) otherSystem.getMonthlyData().get(year.concat(month))).getEfficiency();
            }

            String dateLabel = "";
            if (i == array.length-numberOfMonths) {
                dateLabel = (new DateFormatSymbols()).getMonths()[Integer.parseInt(month)-1].substring(0, 3).concat(" ").concat(year);
            } else if (i == array.length - 1) {
                dateLabel = "This Month";
            } else{
                dateLabel = (new DateFormatSymbols()).getMonths()[Integer.parseInt(month)-1].substring(0, 3);
            }
            data[i-array.length+numberOfMonths] = "['".concat(dateLabel).concat("',").concat(String.valueOf(myPower)).concat(",").concat(String.valueOf(myEfficiency)).concat(",").concat(String.valueOf(sysPower)).concat(",").concat(String.valueOf(sysEfficiency)).concat("]");

        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generateFooter(systemName));
        return html;
    }

    public String compareYearly(String systemName, int numberOfYears) {
        String html = generateHeader(systemName);
        float myPower, myEfficiency,sysPower, sysEfficiency;
        String year;
        if (numberOfYears > mySystem.getYearlyData().size()) {
            numberOfYears = mySystem.getYearlyData().size();
        }
        String[] data = new String[numberOfYears];

        Set<String> keys = mySystem.getYearlyData().keySet();
        String[] array = keys.toArray(new String[keys.size()]);
        String temp = "";
        for (int i = 0; i < keys.size() - 1; i++) {
            for (int j = i + 1; j < keys.size(); j++) {
                if (Integer.parseInt(array[i]) > Integer.parseInt(array[j])) {
                    temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }

        PVSystem otherSystem = (PVSystem) pvSystems.get(systemName);

        for (int i = 0; i < numberOfYears; i++) {
            year = array[i];

            if (mySystem.getYearlyData().get(year) == null) {
                myPower = 0;
                myEfficiency = 0;
            } else {
                myPower = ((PVSummarizedData) mySystem.getYearlyData().get(year)).getEnergyGenerated() / 1000;
                myEfficiency = ((PVSummarizedData) mySystem.getYearlyData().get(year)).getEfficiency();
            }

            if (otherSystem.getYearlyData().get(year) == null) {
                sysPower = 0;
                sysEfficiency = 0;
            } else {
                sysPower = ((PVSummarizedData) otherSystem.getYearlyData().get(year)).getEnergyGenerated() / 1000;
                sysEfficiency = ((PVSummarizedData) otherSystem.getYearlyData().get(year)).getEfficiency();
            }

            String dateLabel = "";
            dateLabel = year;
            data[i] = "['".concat(dateLabel).concat("',").concat(String.valueOf(myPower)).concat(",").concat(String.valueOf(myEfficiency)).concat(",").concat(String.valueOf(sysPower)).concat(",").concat(String.valueOf(sysEfficiency)).concat("]");
        }
        html = html.concat(TextUtils.join(",", data));
//          .concat("")
        html = html.concat(generateFooter(systemName));
        return html;
    }

    public PVSystem getMySystem() {
        return mySystem;
    }

    public HashMap getPvSystems() {
        return pvSystems;
    }
}
