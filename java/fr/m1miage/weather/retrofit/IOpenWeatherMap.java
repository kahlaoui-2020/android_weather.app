package fr.m1miage.weather.retrofit;

import fr.m1miage.weather.model.Main;
import fr.m1miage.weather.model.Weather;
import fr.m1miage.weather.model.WeatherForecastResult;
import fr.m1miage.weather.model.WeatherResult;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {

    @GET("data/2.5/weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                 @Query("lon") String lng,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);

    @GET("weather")
    Call<WeatherResult> getCurrentWeather(@Query("lat") String lat,
                                    @Query("lon") String lon,
                                    @Query("units") String units,
                                    @Query("appid") String appid,
                                    @Query("lang") String lang);

    @GET("forecast")
    Call<WeatherForecastResult> getForecasttWeather(@Query("lat") String lat,
                                                    @Query("lon") String lon,
                                                    @Query("units") String units,
                                                    @Query("appid") String appid,
                                                    @Query("lang") String lang);

    @GET("weather")
    Call<WeatherResult> getCityWeather(@Query("q") String city,
                                          @Query("units") String units,
                                          @Query("appid") String appid,
                                          @Query("lang") String lang);
}
