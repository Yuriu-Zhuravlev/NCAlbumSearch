package com.example.demo.services.impls;

import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByTwoParametersService;
import netscape.javascript.JSObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Service("byAlbumAndArtist")
public class GetByAlbumAndArtistService implements GetByTwoParametersService {


    @Value("${lastfm.url}")
    String urlString;
    @Value("${lastfm.param.method}")
    String methodParam;
    @Value("${lastfm.param.method.get-album}")
    String methodValue;
    @Value("${lastfm.param.api-key}")
    String apiKeyParam;
    @Value("${lastfm.param.api-key.value}")
    String apiKeyValue;
    @Value("${lastfm.param.format}")
    String formatParam;
    @Value("${lastfm.param.format.value}")
    String formatValue;
    @Value("${lastfm.param.album}")
    String albumParam;
    @Value("${lastfm.param.artist}")
    String artistParam;
    @Override
    public Album getAlbum(String param1, String artist) {
        StringBuilder builder = new StringBuilder(urlString);
        builder.append('?');
        builder.append(methodParam).append('=').append(methodValue);
        builder.append('&').append(apiKeyParam).append('=').append(apiKeyValue);
        builder.append('&').append(artistParam).append('=').append(artist);
        builder.append('&').append(albumParam).append('=').append(param1);
        builder.append('&').append(formatParam).append('=').append(formatValue);
        RestTemplate restTemplate = new RestTemplate();
        String responseString = restTemplate.getForObject(builder.toString(), String.class);
        Album album = null;
        try {
            JSONObject json = new JSONObject(responseString);
            json = json.getJSONObject("album");
            String albumName = json.getString("name");
            String artistName = json.getString("artist");
            JSONArray pictures = json.getJSONArray("image");
            int i = 0;
            while (!pictures.getJSONObject(i).getString("size").equals("large"))
                i++;
            String imageURL =  pictures.getJSONObject(i).getString("#text");
            JSONArray tracks = json.getJSONObject("tracks").getJSONArray("track");
            Map<String,Integer> tracksMap = new HashMap<>();
            for(int ind = 0; ind < tracks.length(); ind++){
                JSONObject track = tracks.getJSONObject(ind);
                tracksMap.put(track.getString("name"),Integer.parseInt(track.getString("duration")));
            }
            String genre = json.getJSONObject("tags").getJSONArray("tag").getJSONObject(0).getString("name");
            album = new Album(albumName,artistName,genre,imageURL,tracksMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return album;
    }
}
