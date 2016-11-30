package Weather;

import java.util.ArrayList;

/**
 * Created by Roy on 12-Aug-16.
 */
public class WeatherList {
    private ArrayList<WeatherData> weatherList;

    public WeatherList(){
        weatherList = new ArrayList<WeatherData>();
    }

    public int getCount(){
        return weatherList.size();
    }

    public void addWeatherData(int id, String postcode, String suburb, float latitude, float longitude, int solarstation, String solarname, float solardistance, float solarwet, float solardry, float solarmean, int rainstation, String rainname, float raindistance, float rainwet, float raindry, float rainmean, int tempstation, String tempname, float tempdistance, float tempmaxwet, float tempmaxdry, float tempmaxmean, float tempminwet, float tempmindry, float tempminmean){
        WeatherData newWeatherData = new WeatherData(id, postcode, suburb, latitude, longitude, solarstation, solarname, solardistance, solarwet, solardry, solarmean, rainstation, rainname, raindistance, rainwet, raindry, rainmean, tempstation, tempname, tempdistance, tempmaxwet, tempmaxdry, tempmaxmean, tempminwet, tempmindry, tempminmean);
        weatherList.add(newWeatherData);
    }

    public WeatherData getWeatherDataByIndex(int index){
        return weatherList.get(index);
    }

    public WeatherData getWeatherDataByID(int id){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getId() == id){
                weatherData = (WeatherData) weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }

    public WeatherData getWeatherDataByPostCodeAndSuburb(String postcode, String suburb){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getPostcode().compareToIgnoreCase(postcode) == 0 && weatherList.get(index).getSuburb().compareToIgnoreCase(suburb) == 0){
                weatherData =  weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }

    public WeatherData getWeatherDataByDisplayName(String displayName){
        WeatherData weatherData = null;
        for (int index = 0; index < weatherList.size(); index++){
            if(weatherList.get(index).getPostcode().concat(" ").concat(weatherList.get(index).getSuburb()).compareToIgnoreCase(displayName) == 0){
                weatherData = weatherList.get(index);
                break;
            }
        }
        return weatherData;
    }
}
