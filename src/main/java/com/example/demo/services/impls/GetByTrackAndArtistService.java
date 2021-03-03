package com.example.demo.services.impls;

import com.example.demo.beens.interfaces.URLReplacer;
import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByTwoParametersService;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service("byTrackAndArtist")
public class GetByTrackAndArtistService implements GetByTwoParametersService {
    private static final Logger log = Logger.getLogger(GetByTrackAndArtistService.class);

    @Autowired
    @Qualifier("byAlbumAndArtist")
    GetByTwoParametersService getByAlbumAndArtist;

    @Autowired
    @Qualifier("URLReplacer")
    URLReplacer replacer;

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
                '&' + artistParam + '=' + replacer.replaceSpec(artist) +
                '&' + trackParam + '=' + replacer.replaceSpec(param1) +
                '&' + formatParam + '=' + formatValue +
                '&' + apiKeyParam + '=' + apiKeyValue;
        String responseString = restTemplate.getForObject(url, String.class);
        String albumName;
        String artistName;
        try {
            JSONObject json = new JSONObject(responseString);
            json = json.getJSONObject("track");
            json = json.getJSONObject("album");
            albumName = json.getString("title");
            artistName = json.getString("artist");
            return getByAlbumAndArtist.getAlbum(albumName,artistName);
        } catch (JSONException e) {
            log.error("Failed to extract fields from json",e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "GetByTrackAndArtistService";
    }
}
