package com.example.demo.services.impls;

import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByTwoParametersService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service("byTrackAndArtist")
public class GetByTrackAndArtistService implements GetByTwoParametersService {
    @Autowired
    @Qualifier("byAlbumAndArtist")
    GetByTwoParametersService getByAlbumAndArtist;

    @Value("${lastfm.url}")
    String urlString;
    @Value("${lastfm.param.method}")
    String methodParam;
    @Value("${lastfm.param.method.get-track-info}")
    String methodValue;
    @Value("${lastfm.param.api-key}")
    String apiKeyParam;
    @Value("${lastfm.param.api-key.value}")
    String apiKeyValue;
    @Value("${lastfm.param.format}")
    String formatParam;
    @Value("${lastfm.param.format.value}")
    String formatValue;
    @Value("${lastfm.param.track}")
    String trackParam;
    @Value("${lastfm.param.artist}")
    String artistParam;

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<Album> getAlbum(String param1, String artist) {
        RestTemplate restTemplate = new RestTemplate();
        String url = urlString + '?' +
                methodParam + '=' + methodValue +
                '&' + apiKeyParam + '=' + apiKeyValue +
                '&' + artistParam + '=' + artist +
                '&' + trackParam + '=' + param1 +
                '&' + formatParam + '=' + formatValue;
        String responseString = restTemplate.getForObject(url, String.class);
        String albumName = null;
        try {
            JSONObject json = new JSONObject(responseString);
            json = json.getJSONObject("track");
            json = json.getJSONObject("album");
            albumName = json.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CompletableFuture<Album> album = getByAlbumAndArtist.getAlbum(albumName,artist);
        return album;
    }
}
