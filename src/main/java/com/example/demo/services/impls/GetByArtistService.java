package com.example.demo.services.impls;

import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByOneParameterService;
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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service("byArtist")
public class GetByArtistService implements GetByOneParameterService {
    @Autowired
    @Qualifier("byAlbumAndArtist")
    GetByTwoParametersService getByAlbumAndArtist;

    @Value("${lastfm.url}")
    String urlString;
    @Value("${lastfm.param.method}")
    String methodParam;
    @Value("${lastfm.param.method.get-top-albums}")
    String methodValue;
    @Value("${lastfm.param.api-key}")
    String apiKeyParam;
    @Value("${lastfm.param.api-key.value}")
    String apiKeyValue;
    @Value("${lastfm.param.format}")
    String formatParam;
    @Value("${lastfm.param.format.value}")
    String formatValue;
    @Value("${lastfm.param.artist}")
    String artistParam;
    @Value("${lastfm.param.limit}")
    String limitParam;
    @Value("${lastfm.param.limit.value}")
    int limitValue;

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<List<Album>> getAlbums(String param) {
        RestTemplate restTemplate = new RestTemplate();
        String url = urlString + '?' +
                methodParam + '=' + methodValue +
                '&' + apiKeyParam + '=' + apiKeyValue +
                '&' + artistParam + '=' + param +
                '&' + limitParam + '=' + limitValue +
                '&' + formatParam + '=' + formatValue;
        String responseString = restTemplate.getForObject(url, String.class);
        List<CompletableFuture<Album>> completableFutureList = new LinkedList<>();
        int total = 1;
        try {
            JSONObject json = new JSONObject(responseString);
            json = json.getJSONObject("topalbums");
            total = Integer.parseInt(json.getJSONObject("@attr").getString("total"));
            JSONArray jsonAlbums = json.getJSONArray("album");
            for (int i = 0; i < jsonAlbums.length(); i++){
                String albumName = jsonAlbums.getJSONObject(i).getString("name");
                completableFutureList.add(getByAlbumAndArtist.getAlbum(albumName,param));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<Album> albums = new LinkedList<>();
        for (CompletableFuture<Album> completableFuture: completableFutureList) {
            completableFuture.join();
            try {
                albums.add(completableFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        albums.get(0).setSimilarResults(total);
        return CompletableFuture.completedFuture(albums);
    }
}
