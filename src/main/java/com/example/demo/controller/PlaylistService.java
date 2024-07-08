package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.json.JSONObject;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@Service
public class PlaylistService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    private static final Map<String, String[]> WEATHER_PLAYLISTS = new HashMap<>();
    static {
        WEATHER_PLAYLISTS.put("cloud", new String[] { "0bMcktKQyVsZf8GPo40Arg", "37i9dQZF1E8MqKxPDve0HQ", "4vVjm4upSFRdHvboFowdBL" });
        WEATHER_PLAYLISTS.put("thunderstorm", new String[] { "37i9dQZF1DWXKbJeFbii64", "7pCOnZzg7E2wewGLO2isv7", "37i9dQZF1DZ06evO0VyLrW" });
        WEATHER_PLAYLISTS.put("rain", new String[] { "00CnRinqtpf4wT6dUo98QB", "3r82Jvzw3SSGKKiKf3dXMM" });
        WEATHER_PLAYLISTS.put("snow", new String[] { "37i9dQZF1DWSoLBQNkZBKc", "7BvpemLQl4mMNrhCGBthQ7", "3s3Egi9w63a7pNblMdXk5G" });
        WEATHER_PLAYLISTS.put("mist", new String[] { "3qvjuS5kBwdIUTJ8xd78Xg", "7MWBMlrwidebjrtkJ5IRMi", "3KKKF29VPzTWMAr4b9qG8l" });
        WEATHER_PLAYLISTS.put("smoke", new String[] { "4pu0hpcPfKPna44Ee3ZRhy", "5oW8fS6ro0YdZR1svGqajr" });
        WEATHER_PLAYLISTS.put("haze", new String[] { "6bR56iRdpsFKrYPeReXUzC", "37i9dQZF1DZ06evO3Rodvq" });
        WEATHER_PLAYLISTS.put("sand", new String[] { "0QR0OOsDIuL7sLFPcd0uG3", "2yfxtauoEIpeBqApGJA9ya", "7IGgZbBQiT4DThMyGxAlBX" });
        WEATHER_PLAYLISTS.put("dust whirls", new String[] { "0QR0OOsDIuL7sLFPcd0uG3", "2yfxtauoEIpeBqApGJA9ya", "7IGgZbBQiT4DThMyGxAlBX" });
        WEATHER_PLAYLISTS.put("fog", new String[] { "3SfCLlEbXujHz7vPcetTVb" });
        WEATHER_PLAYLISTS.put("volcanic ash", new String[] { "0uJ7JZ7arNMSzQFdiOWZ1f" });
        WEATHER_PLAYLISTS.put("tornado", new String[] { "6Tc97RwuxHRLyGZSdsXPfR" });
        WEATHER_PLAYLISTS.put("squalls", new String[] { "7ydON6rHHy0WSWzGR0k3pm" });
        WEATHER_PLAYLISTS.put("clear sky", new String[] { "1ioN4Mwrv6G7DGP2indDqK", "2hmLDliFT9mW84XHxRUzwx", "5M6OSOdx9q2Pcp3oIgbvL9" });
    }

    private String getSpotifyToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://accounts.spotify.com/api/token";

        String credentials = clientId + ":" + clientSecret;
        String base64Credentials = new String(Base64.getEncoder().encode(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCodeValue() != 200) {
            System.err.println("Error obtaining token. Status Code: " + response.getStatusCodeValue());
            System.err.println("Response content: " + response.getBody());
            return null;
        }

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String token = jsonResponse.getString("access_token");
        System.out.println("Obtained token: " + token);
        return token;
    }

    private String searchPlaylist(String token, String playlistId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.spotify.com/v1/playlists/" + playlistId;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (response.getStatusCodeValue() != 200) {
            System.err.println("Error searching playlist. Status Code: " + response.getStatusCodeValue());
            System.err.println("Response content: " + response.getBody());
            return null;
        }

        JSONObject jsonResponse = new JSONObject(response.getBody());
        String playlistUrl = jsonResponse.getJSONObject("external_urls").getString("spotify");
        System.out.println("Retrieved playlist_url: " + playlistUrl);
        return playlistUrl;
    }

    public String getRandomPlaylist(String weatherDescription) {
        for (Map.Entry<String, String[]> entry : WEATHER_PLAYLISTS.entrySet()) {
            if (weatherDescription.contains(entry.getKey())) {
                String[] playlists = entry.getValue();
                Random random = new Random();
                String selectedPlaylist = playlists[random.nextInt(playlists.length)];

                String token = getSpotifyToken();
                if (token != null) {
                    return searchPlaylist(token, selectedPlaylist);
                }
            }
        }
        System.out.println("No matching playlist found for weather_desc: " + weatherDescription);
        return null;
    }
}
