package us.ait.android.weatherinfo.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import us.ait.android.weatherinfo.weather_data.WeatherResult;

public interface WeatherAPI {
    @GET("data/2.5/weather")
    Call<WeatherResult> getWeather(@Query("q") String cityName,
                                 @Query("units") String units,
                                 @Query("appid") String appId);
}

// units=imperial

// http://api.openweathermap.org/data/2.5/weather
// ?
// q=Boston
// &
// appid=9172ffe4d802a65ea4b5cf2ccd0b00db