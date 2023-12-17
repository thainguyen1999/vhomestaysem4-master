package com.example.vhomestay.config;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class IPConfig {
    public Map<String, String> getIPData() {
        String apiUrl = "http://ip-api.com/json/?fields=24793";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);
        JSONObject jsonObject = JSONValue.parse(response, JSONObject.class);

        String status = (String) jsonObject.get("status");
        String country = (String) jsonObject.get("country");
        String city = (String) jsonObject.get("city");
        String lon = String.valueOf(jsonObject.get("lat"));
        String lat = String.valueOf(jsonObject.get("lon"));

        return Map.of(
                "status", status,
                "country", country,
                "city", city,
                "lat", lon,
                "lon", lat
        );
    }

    public static String getIP() {
        String apiUrl = "http://ip-api.com/json/?fields=query";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);
        JSONObject jsonObject = JSONValue.parse(response, JSONObject.class);
        return (String) jsonObject.get("query");
    }
}
