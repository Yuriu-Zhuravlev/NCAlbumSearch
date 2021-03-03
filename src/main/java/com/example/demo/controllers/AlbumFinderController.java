package com.example.demo.controllers;

import com.example.demo.beens.factories.AlbumConverterFactory;
import com.example.demo.beens.interfaces.AlbumConverter;
import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByOneParameterService;
import com.example.demo.services.interfaces.GetByTwoParametersService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AlbumFinderController {
    @Autowired
    @Qualifier("albumConverterFactory")
    AlbumConverterFactory albumConverterFactory;
    @Autowired
    @Qualifier("byTrackAndArtist")
    GetByTwoParametersService getByTrackAndArtist;
    @Autowired
    @Qualifier("byArtist")
    GetByOneParameterService getByArtist;
    @Autowired
    @Qualifier("byTrack")
    GetByOneParameterService getByTrack;

    @RequestMapping (path = "/byArtistAndTrack")
    public ResponseEntity<?> getByArtistAndTrack(
            @RequestParam(value = "artist") String artist,
            @RequestParam(value = "track") String track,
            @RequestParam(value = "format", defaultValue="json") String format
    )
    {
        System.out.println(getByTrackAndArtist);
        CompletableFuture<Album> albumCompletableFuture = getByTrackAndArtist.getAlbum(track,artist);
        albumCompletableFuture.join();
        Album album = null;
        try {
            album = albumCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        List<Album> albums = new LinkedList<>();
        albums.add(album);
        AlbumConverter albumConverter = albumConverterFactory.build(format);
        return ResponseEntity.ok(albumConverter.convert(albums));
    }

    @RequestMapping (path = "/byArtist")
    public ResponseEntity<?> getByArtist(
            @RequestParam(value = "artist") String artist,
            @RequestParam(value = "format", defaultValue="json") String format
    )
    {
        CompletableFuture<List<Album>> albumCompletableFuture = getByArtist.getAlbums(artist);
        albumCompletableFuture.join();
        List<Album> albums = null;
        try {
            albums = albumCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        AlbumConverter albumConverter = albumConverterFactory.build(format);
        return ResponseEntity.ok(albumConverter.convert(albums));
    }

    @RequestMapping (path = "/byTrack")
    public ResponseEntity<?> getByTrack(
            @RequestParam(value = "track") String track,
            @RequestParam(value = "format", defaultValue="json") String format
    )
    {
        CompletableFuture<List<Album>> albumCompletableFuture = getByTrack.getAlbums(track);
        albumCompletableFuture.join();
        List<Album> albums = null;
        try {
            albums = albumCompletableFuture.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        AlbumConverter albumConverter = albumConverterFactory.build(format);
        return ResponseEntity.ok(albumConverter.convert(albums));
    }
}
