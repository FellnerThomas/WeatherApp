package WeatherParser;

/**
 * Created by Ramin on 19.03.2015.
 */
public class ThreeHourlyWeather {

    private String id;
    private String starting_hour;
    private String climate;
    private String temperature_celsius;
    private String windSpeed;
    private String windDirection;

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getStarting_hour() {
        return starting_hour;
    }

    public void setStarting_hour(String starting_hour) {
        this.starting_hour = starting_hour;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getTemperature_celsius() {
        return temperature_celsius;
    }

    public void setTemperature_celsius(String temperature_celsius) {
        this.temperature_celsius = temperature_celsius;
    }
}
