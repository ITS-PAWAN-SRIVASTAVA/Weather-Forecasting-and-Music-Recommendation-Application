package com.example.demo.controller;

import com.example.demo.controller.PlaylistService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class WeatherController {

    private final String API_KEY = "71742e7f9e446671bbc4fb40b66f8045"; // Replace this with your actual API key

    @Autowired
    private PlaylistService playlistService;

    @GetMapping("/weather")
    public String weatherForm() {
        return "weather";
    }

    @PostMapping("/weather")
    public String getWeather(@RequestParam("city") String city, Model model) {
        System.out.println("Received request for city: " + city);
        RestTemplate restTemplate = new RestTemplate();
        String weatherUrl = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric", city, API_KEY);
        String weatherData = restTemplate.getForObject(weatherUrl, String.class);

        // Assuming coordinates are fetched from weather data
        JSONObject weatherJson = new JSONObject(weatherData);
        double lat = weatherJson.getJSONObject("coord").getDouble("lat");
        double lon = weatherJson.getJSONObject("coord").getDouble("lon");

        // Extracting current weather data
        JSONObject currentWeather = new JSONObject();
        if (weatherJson.has("main")) {
            JSONObject main = weatherJson.getJSONObject("main");
            currentWeather.put("temperature", main.optDouble("temp", 0.0));
            currentWeather.put("feels_like", main.optDouble("feels_like", 0.0));
            currentWeather.put("min_temp", main.optDouble("temp_min", 0.0));
            currentWeather.put("max_temp", main.optDouble("temp_max", 0.0));
            currentWeather.put("pressure", main.optInt("pressure", 0));
            currentWeather.put("humidity", main.optInt("humidity", 0));
        }
        if (weatherJson.has("weather") && weatherJson.getJSONArray("weather").length() > 0) {
            JSONObject weather = weatherJson.getJSONArray("weather").getJSONObject(0);
            currentWeather.put("condition", weather.optString("main", "Unknown"));
            currentWeather.put("description", weather.optString("description", "Unknown"));
        }

        // Add current weather data to model
        model.addAttribute("currentWeather", currentWeather.toMap());

        // Extract location information
        JSONObject locationInfo = new JSONObject();
        if (weatherJson.has("name")) {
            locationInfo.put("name", weatherJson.optString("name", "Unknown"));
        }
        if (weatherJson.has("sys") && weatherJson.getJSONObject("sys").has("country")) {
            locationInfo.put("country", weatherJson.getJSONObject("sys").optString("country", "Unknown"));
        }
        if (weatherJson.has("coord")) {
            JSONObject coord = weatherJson.getJSONObject("coord");
            if (coord.has("lat")) {
                locationInfo.put("latitude", coord.optDouble("lat", 0.0));
            }
            if (coord.has("lon")) {
                locationInfo.put("longitude", coord.optDouble("lon", 0.0));
            }
        }

        if (!locationInfo.isEmpty()) {
            model.addAttribute("location", locationInfo.toMap());
        }

        // Add air quality data to model
        String airQualityUrl = String.format("http://api.openweathermap.org/data/2.5/air_pollution?lat=%f&lon=%f&appid=%s", lat, lon, API_KEY);
        String airQualityData = restTemplate.getForObject(airQualityUrl, String.class);
        AirQualityData airQuality = parseAirQualityData(airQualityData);
        model.addAttribute("airQuality", airQuality);

        // Add hourly forecast data to model
        model.addAttribute("hourlyForecast", getHourlyForecast(lat, lon));

        // Add local time to model
        model.addAttribute("localTime", timestampToLocalTime(weatherJson.optLong("dt", 0)));

        // Log model attributes after adding attributes to the model
        System.out.println("Model Attributes: " + model);
        System.out.println("Current Weather: " + model.getAttribute("currentWeather"));
        System.out.println("Air Quality: " + model.getAttribute("airQuality"));
        System.out.println("Hourly Forecast: " + model.getAttribute("hourlyForecast"));
        System.out.println("Local Time: " + model.getAttribute("localTime"));

        // Get playlist URL based on weather description
        String weatherDescription = currentWeather.getString("description");
        String playlistUrl = playlistService.getRandomPlaylist(weatherDescription);
        model.addAttribute("playlistUrl", playlistUrl);
        // After adding playlist URL to model
        System.out.println("Playlist URL: " + model.getAttribute("playlistUrl"));

        // After fetching weather data
        System.out.println("Weather Data: " + weatherData);

// After parsing current weather
        System.out.println("Current Weather: " + currentWeather);

// After adding current weather to model
        System.out.println("Model Attributes: " + model);
        System.out.println("Current Weather in Model: " + model.getAttribute("currentWeather"));


        return "weather";
    }

    // Method to parse air quality data
    private AirQualityData parseAirQualityData(String airQualityData) {
        JSONObject json = new JSONObject(airQualityData);

        // Extracting coordinates from JSON response
        double lat = json.getJSONObject("coord").getDouble("lat");
        double lon = json.getJSONObject("coord").getDouble("lon");

        // Extracting air quality index (AQI) and components
        JSONObject listObject = json.getJSONArray("list").getJSONObject(0);
        int aqi = listObject.getJSONObject("main").getInt("aqi");
        JSONObject componentsJson = listObject.getJSONObject("components");

        // Creating Components object
        Components components = new Components();
        components.setCo(componentsJson.getDouble("co"));
        components.setNo(componentsJson.getDouble("no"));
        components.setNo2(componentsJson.getDouble("no2"));
        components.setO3(componentsJson.getDouble("o3"));
        components.setPm2_5(componentsJson.getDouble("pm2_5"));
        components.setPm10(componentsJson.getDouble("pm10"));
        components.setSo2(componentsJson.getDouble("so2"));
        components.setNh3(componentsJson.getDouble("nh3"));

        // Creating AirQualityData object
        AirQualityData airQuality = new AirQualityData();
        airQuality.setAqi(aqi);
        Coordinates coord = new Coordinates();
        coord.setLat(lat);
        coord.setLon(lon);
        airQuality.setCoord(coord);
        airQuality.setComponents(components);

        // Populate particles map with detailed information
        airQuality.setParticles(createParticlesMap(componentsJson));

        // Placeholder for suggestions
        airQuality.setSuggestions(new HashMap<>());

        return airQuality;
    }

    // Method to create particles map with detailed information
    private Map<String, Map<String, Object>> createParticlesMap(JSONObject componentsJson) {
        Map<String, Map<String, Object>> particles = new HashMap<>();
        particles.put("pm2_5", createParticleInfo(componentsJson.getDouble("pm2_5"), "pm2_5"));
        particles.put("pm10", createParticleInfo(componentsJson.getDouble("pm10"), "pm10"));
        particles.put("co", createParticleInfo(componentsJson.getDouble("co"), "co"));
        particles.put("o3", createParticleInfo(componentsJson.getDouble("o3"), "o3"));
        particles.put("no2", createParticleInfo(componentsJson.getDouble("no2"), "no2"));
        particles.put("so2", createParticleInfo(componentsJson.getDouble("so2"), "so2"));
        particles.put("nh3", createParticleInfo(componentsJson.getDouble("nh3"), "nh3"));
        return particles;
    }

    // Method to create particle information
    private Map<String, Object> createParticleInfo(double index, String type) {
        Map<String, Object> particleInfo = new HashMap<>();
        particleInfo.put("index", index);
        particleInfo.put("description", getDescription(type));
        particleInfo.put("health_concern", getHealthConcern(type, index));
        return particleInfo;
    }

    // Method to get description for a given particle type
    private String getDescription(String type) {
        switch (type) {
            case "pm2_5":
                return "Fine Particulate Matter are inhalable pollutant particles with a diameter less than 2.5 micrometers that can enter the lungs and bloodstream, resulting in serious health issues. The most severe impacts are on the lungs and heart. Exposure can result in coughing or difficulty breathing, aggravated asthma, and the development of chronic respiratory disease.";
            case "pm10":
                return "Particulate Matter are inhalable pollutant particles with a diameter less than 10 micrometers. Particles that are larger than 2.5 micrometers can be deposited in airways, resulting in health issues. Exposure can result in eye and throat irritation, coughing or difficulty breathing, and aggravated asthma. More frequent and excessive exposure can result in more serious health effects.";
            case "co":
                return "Carbon Monoxide is a colorless and odorless gas. When inhaled at high levels, it can cause headache, nausea, dizziness, and vomiting. Repeated long-term exposure can lead to heart disease.";
            case "o3":
                return "Ground-level Ozone can aggravate existing respiratory diseases and also lead to throat irritation, headaches, and chest pain.";
            case "no2":
                return "Breathing in high levels of Nitrogen Dioxide increases the risk of respiratory problems. Coughing and difficulty breathing are common, and more serious health issues such as respiratory infections can occur with longer exposure.";
            case "so2":
                return "Exposure to Sulfur Dioxide can lead to throat and eye irritation and aggravate asthma as well as chronic bronchitis.";
            case "nh3":
                return "Ammonia exposure can cause irritation of the eyes, nose, and throat. High levels can lead to coughing and wheezing.";
            default:
                return "";
        }
    }

    // Method to get health concern for a given particle type and index
    private String getHealthConcern(String type, double index) {
        switch (type) {
            case "pm2_5":
                if (index < 10) return "Good";
                if (index < 25) return "Fair";
                if (index < 50) return "Moderate";
                if (index < 75) return "Poor";
                return "Very Poor";
            case "pm10":
                if (index < 20) return "Good";
                if (index < 50) return "Fair";
                if (index < 100) return "Moderate";
                if (index < 200) return "Poor";
                return "Very Poor";
            case "co":
                if (index < 4400) return "Good";
                if (index < 9400) return "Fair";
                if (index < 12400) return "Moderate";
                if (index < 15400) return "Poor";
                return "Very Poor";
            case "o3":
                if (index < 60) return "Good";
                if (index < 100) return "Fair";
                if (index < 140) return "Moderate";
                if (index < 180) return "Poor";
                return "Very Poor";
            case "no2":
                if (index < 40) return "Good";
                if (index < 70) return "Fair";
                if (index < 150) return "Moderate";
                if (index < 200) return "Poor";
                return "Very Poor";
            case "so2":
                if (index < 20) return "Good";
                if (index < 80) return "Fair";
                if (index < 250) return "Moderate";
                if (index < 350) return "Poor";
                return "Very Poor";
            case "nh3":
                // Define health concern levels for NH3 if available.
                return "Health concern information for NH3 is not available";
            default:
                return "Unknown";
        }
    }

    // Method to get hourly forecast
    private List<HourlyForecast> getHourlyForecast(double latitude, double longitude) {
        String base_url = "https://api.openweathermap.org/data/2.5/forecast";
        String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=metric", base_url, latitude, longitude, API_KEY);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        JSONObject data = new JSONObject(response);

        List<HourlyForecast> hourlyForecast = new ArrayList<>();
        if (data.has("list")) {
            JSONArray forecasts = data.getJSONArray("list");
            for (int i = 0; i < forecasts.length(); i++) {
                JSONObject forecast = forecasts.getJSONObject(i);
                long timestamp = forecast.getLong("dt");
                String formattedTime = timestampToLocalTime(timestamp);
                hourlyForecast.add(new HourlyForecast(
                        timestamp,
                        forecast.getJSONObject("main").getDouble("temp"),
                        forecast.getJSONArray("weather").getJSONObject(0).getString("main"),
                        forecast.getJSONArray("weather").getJSONObject(0).getString("description"),
                        forecast.getJSONArray("weather").getJSONObject(0).getString("icon"), // Icon field
                        formattedTime
                ));
            }
        }

        return hourlyForecast;
    }

    // Method to convert UTC timestamp to local time
    private String timestampToLocalTime(long timestamp) {
        LocalDateTime utcDateTime = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
        ZoneId istZone = ZoneId.of("Asia/Kolkata");
        LocalDateTime istDateTime = utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(istZone).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return istDateTime.format(formatter);
    }
}
