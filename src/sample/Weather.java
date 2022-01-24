package sample;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.net.MalformedURLException;

public class Weather {

    private final String cityAPI = "https://www.metaweather.com/api/location/search/?query=";

    private final String weatherAPI = "https://www.metaweather.com/api/location/";

    private final String weatherIconAPI = "https://www.metaweather.com/static/img/weather/png/";
    // continue with [WEATHER_ABBREV].png
    // eg https://www.metaweather.com/static/img/weather/png/64/sn.png

    private String woeID_KualaLumpur = "1154781";

    String getWeatherText() throws MalformedURLException {
        JSONObject todaysWeather = GetTodaysWeatherInformation(woeID_KualaLumpur);
        return todaysWeather.get("weather_state_name").toString();
    }

    String getWeatherIcon() throws MalformedURLException {
        JSONObject todaysWeather = GetTodaysWeatherInformation(woeID_KualaLumpur);
        String weatherAbbreviation = todaysWeather.get("weather_state_abbr").toString();
        return (weatherIconAPI + weatherAbbreviation + ".png");
    }

    String getWeatherTemp() throws MalformedURLException {
        JSONObject todaysWeather = GetTodaysWeatherInformation(woeID_KualaLumpur);
        String strTemp = todaysWeather.get("the_temp").toString();
        return String.valueOf(Math.round(Float.parseFloat(strTemp)));
    }

    public JSONObject GetTodaysWeatherInformation(String woeid) throws MalformedURLException {
        APIConnector apiConnectorWeather = new APIConnector(weatherAPI);

        JSONObject weatherJSONObject = apiConnectorWeather.getJSONObject(woeid + "/");

        JSONArray weatherArray = (JSONArray) weatherJSONObject.get("consolidated_weather");

        return  (JSONObject) weatherArray.get(0);
    }

}