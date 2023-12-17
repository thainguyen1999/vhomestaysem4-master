package com.example.vhomestay.service.impl;

import com.example.vhomestay.config.IPConfig;
import com.example.vhomestay.config.WeatherConfig;
import com.example.vhomestay.model.dto.response.LocationAndWeatherDto;
import com.example.vhomestay.service.LocationAndWeatherService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LocationAndWeatherServiceImpl implements LocationAndWeatherService {

    @Override
    public LocationAndWeatherDto getLocationAndWeather() {
        LocationAndWeatherDto locationAndWeatherDto = new LocationAndWeatherDto();
        WeatherConfig weatherConfig = new WeatherConfig();
        Map<String, String> weatherData;
        weatherData = weatherConfig.getWeather(WeatherConfig.LAT_VILLAGE, WeatherConfig.LON_VILLAGE);
        locationAndWeatherDto.setLocation("Làng H'Mông Pả Vi");
        locationAndWeatherDto.setWeatherIconUrl(weatherData.get("weatherIcon"));
        locationAndWeatherDto.setWeatherTemp(weatherData.get("temperature"));
        locationAndWeatherDto.setWeatherDescription(weatherData.get("weatherDescription"));
        locationAndWeatherDto.setWeatherWarning(weatherData.get("warning"));
        return locationAndWeatherDto;
    }
}
