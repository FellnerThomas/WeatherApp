package WeatherParser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ramin on 19.03.2015.
 */
public class DailyWeather {
    private ArrayList<ThreeHourlyWeather> threeHourlyWeatherData;

    public DailyWeather() {
        threeHourlyWeatherData = new ArrayList<ThreeHourlyWeather>();
    }


    public ArrayList<ThreeHourlyWeather> getThreeHourlyWeatherData() {
        return threeHourlyWeatherData;
    }

    public void addThreeHourlyWeatherData (ThreeHourlyWeather thw) {
        threeHourlyWeatherData.add(thw);
    }


}
