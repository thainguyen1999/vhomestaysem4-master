package com.example.vhomestay.model.dto.response;

import lombok.Data;

@Data
public class LocationAndWeatherDto {
    private String location;
    private String weatherIconUrl;
    private String weatherTemp;
    private String weatherDescription;
    private String weatherWarning;
}
