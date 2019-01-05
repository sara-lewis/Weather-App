package us.ait.android.weatherinfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.ait.android.weatherinfo.adapter.CityRecyclerAdapter;
import us.ait.android.weatherinfo.network.WeatherAPI;
import us.ait.android.weatherinfo.weather_data.WeatherResult;

public class WeatherInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        TextView tvCityName = findViewById(R.id.tvCityName);
        final TextView tvWeatherDetails = findViewById(R.id.tvWeatherDetails);
        final ImageView ivWeather = findViewById(R.id.ivWeather);

        String cityName = getIntent().getStringExtra(CityRecyclerAdapter.KEY_CITY);
        tvCityName.setText(cityName);

        setUpRetrofit(tvWeatherDetails, ivWeather, cityName);
    }

    private void setUpRetrofit(final TextView tvWeatherDetails, final ImageView ivWeather, String cityName) {
        final WeatherAPI weatherAPI = initRetrofit();

        weatherAPI.getWeather(cityName, getString(R.string.imperial), getString(R.string.app_id)
        ).enqueue(new Callback<WeatherResult>() {
            @Override
            public void onResponse(Call<WeatherResult> call, Response<WeatherResult> response) {
                parseAndDisplayWeatherData(response, ivWeather, tvWeatherDetails);
            }

            @Override
            public void onFailure(Call<WeatherResult> call, Throwable t) {
                tvWeatherDetails.setText(t.getMessage());
            }
        });
    }

    private WeatherAPI initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherAPI.class);
    }

    private void parseAndDisplayWeatherData(Response<WeatherResult> response, ImageView ivWeather, TextView tvWeatherDetails) {
        WeatherResult result = response.body();
        Glide.with(WeatherInfoActivity.this).
                load(String.format("%s%s.png", getString(R.string.img_url), result.getWeather().get(0).getIcon()))
                .into(ivWeather);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s\n", getString(R.string.weather_des), result.getWeather().get(0).getDescription()));
        sb.append(String.format("%s %s %s\n", getString(R.string.current_temp), result.getMain().getTemp(), getString(R.string.degrees)));
        sb.append(String.format("%s %s %s\n", getString(R.string.min_temp), result.getMain().getTempMin(), getString(R.string.degrees)));
        sb.append(String.format("%s %s %s\n", getString(R.string.max_temp), result.getMain().getTempMax(), getString(R.string.degrees)));

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        sb.append(String.format("%s %s\n", getString(R.string.sunrise_time), timeFormat.format((1000 * result.getSys().getSunrise()))));
        sb.append(String.format("%s %s\n", getString(R.string.sunset_time), timeFormat.format(1000 * result.getSys().getSunset())));

        tvWeatherDetails.setText(sb);
    }
}
