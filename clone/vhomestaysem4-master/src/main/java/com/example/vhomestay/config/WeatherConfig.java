package com.example.vhomestay.config;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Configuration
public class WeatherConfig {
    private static final String API_KEY = "e2f0c6b269af78b400c28ca25bf4ace7";
    public static final String LAT_VILLAGE = "23.1670564";
    public static final String LON_VILLAGE = "105.3951626";
    private static final String LANGUAGE = "vi";

    public Map<String, String> getWeather(String lat, String lon) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&lang=" + LANGUAGE + "&units=metric";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(apiUrl, String.class);
        JSONObject jsonObject = JSONValue.parse(response, JSONObject.class);

        JSONObject main = (JSONObject) jsonObject.get("main");
        int temperature = (int) Math.round((Double) main.get("temp"));

        JSONObject weatherObject = (JSONObject) ((JSONArray) jsonObject.get("weather")).get(0);
        int weatherId = (int) weatherObject.get("id");
        String warning = warning(weatherId);
        String weatherMain = (String) weatherObject.get("main");
        String weatherDescription =  (String) weatherObject.get("description");
        String weatherIcon = "https://storage.googleapis.com/hbs_bucket1/weather/" + weatherObject.get("icon") + "%404x.png";

        return Map.of(
                "temperature", String.valueOf(temperature),
                "weatherMain", weatherMain,
                "weatherDescription", weatherDescription,
                "warning", warning,
                "weatherIcon", weatherIcon
        );
    }

    public String warning(int weatherId) {
        switch (weatherId) {
            case 200, 201, 202, 210, 211, 212, 221, 230, 231, 232 -> {
                return "Thời tiết xấu, hãy cẩn thận khi ra ngoài";
            }
            case 300, 301, 302, 310, 311, 312, 313, 314, 321 -> {
                return "Tầm nhìn xa bị hạn chế, bạn nên mang theo áo mưa và di chuyển chậm khi ra ngoài";
            }
            case 500, 501, 502, 503, 504, 520, 521, 522, 531 -> {
                return "Hãy mang theo ô và áo mưa khi ra ngoài";
            }
            case 511, 600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622 -> {
                return "Hãy mặc ấm và cẩn thận với đường trơn trượt khi ra ngoài";
            }
            case 701, 711, 721, 731, 741, 751, 761, 762, 771, 781 -> {
                return "Thời tiết cực đoan, hãy hạn chế đi ra ngoài";
            }
            case 800 -> {
                return "Thời tiết đẹp, phù hợp cho các hoạt động ngoài trời. Thật tuyệt với khi đến với Làng H'Mông Pả Vi vào ngày hôm nay";
            }
            case 801, 802, 803, 804 -> {
                return "Thời tiết mát mẻ, có khả năng mưa. Hãy mang theo ô và áo mưa khi ra ngoài";
            }
            default -> {
                return null;
            }
        }
    }

}
