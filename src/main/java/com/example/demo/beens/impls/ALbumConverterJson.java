package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.AlbumConverter;
import com.example.demo.classes.Album;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ALbumConverterJson implements AlbumConverter {
    @Override
    public String convert(List<Album> albums) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONArray albumsJson = new JSONArray();
            for (Album album: albums) {
                albumsJson.put(albumToJson(album));
            }
            jsonObject.put("albums", albumsJson);
            jsonObject.put("total albums",albums.get(0).getSimilarResults());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public JSONObject albumToJson(Album album){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("album",album.getName());
            jsonObject.put("artist",album.getAuthor());
            jsonObject.put("picture", album.getPictureURL());
            jsonObject.put("genre",album.getGenre());
            JSONArray tracks = new JSONArray();
            Map<String, Integer> tracksMap = album.getSongs();
            for (Map.Entry<String, Integer> entry : tracksMap.entrySet()) {
                JSONObject track = new JSONObject();
                track.put("name",entry.getKey());
                track.put("duration",entry.getValue());
                tracks.put(track);
            }
            jsonObject.put("tracks",tracks);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
