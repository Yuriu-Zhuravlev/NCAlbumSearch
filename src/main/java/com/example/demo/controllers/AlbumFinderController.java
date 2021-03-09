package com.example.demo.controllers;

import com.example.demo.beens.factories.AlbumConverterFactory;
import com.example.demo.beens.factories.ErrorResponseFactory;
import com.example.demo.beens.factories.ErrorResponseTypes;
import com.example.demo.beens.interfaces.AlbumConverter;
import com.example.demo.beens.interfaces.SaveToDock;
import com.example.demo.classes.Album;
import com.example.demo.services.interfaces.GetByOneParameterService;
import com.example.demo.services.interfaces.GetByTwoParametersService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
public class AlbumFinderController {
    private static final Logger log = Logger.getLogger(AlbumFinderController.class);
    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    @Qualifier("errorResponse")
    ErrorResponseFactory errorResponseFactory;
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
    @Autowired
    @Qualifier("albumToDock")
    SaveToDock saveToDock;

    @Async("asyncExecutor")
    @CacheEvict("getByTrackAndArtist")
    @RequestMapping (path = "/byArtistAndTrack")
    public CompletableFuture<ResponseEntity<?>> getByArtistAndTrack(
            @RequestParam(value = "artist") String artist,
            @RequestParam(value = "track") String track,
            @RequestParam(value = "format", defaultValue="json") String format,
            @RequestParam(value = "download", defaultValue="false") boolean download
    )
    {
        log.info("/byArtistAndTrack called with params: artist = " + artist +", track = " + track + ", format = " + format);
        AlbumConverter albumConverter;
        try {
            albumConverter = albumConverterFactory.build(format);
        } catch (IllegalArgumentException e){
            log.error("Wrong format",e);
            return CompletableFuture.completedFuture(ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.INVALID_FORMAT).createResponse()));
        }
        CompletableFuture<Album> albumCompletableFuture = getByTrackAndArtist.getAlbum(track,artist);
        albumCompletableFuture.join();
        Album album = null;
        try {
            album = albumCompletableFuture.get();
            if (album == null){
                log.info("Didn't find anything suitable");
                if (format.equals("xml"))
                    return CompletableFuture.completedFuture(ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.NOTHING_FOUND_XML).createResponse()));
                else
                    return CompletableFuture.completedFuture(ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.NOTHING_FOUND_JSON).createResponse()));
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get album from CompletableFuture",e);
        }
        List<Album> albums = new LinkedList<>();
        albums.add(album);
        if(download) {
            ResponseEntity<?> responseEntity = null;
            String path = saveToDock.save(albums, artist + "_" + track);
            File file = new File(path);
            try {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                responseEntity = ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(responseEntity);
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.ok(albumConverter.convert(albums)));
        }
    }

    @Async("asyncExecutor")
    @CacheEvict("getByArtist")
    @RequestMapping (path = "/byArtist")
    public CompletableFuture<ResponseEntity<?>> getByArtist(
            @RequestParam(value = "artist") String artist,
            @RequestParam(value = "format", defaultValue="json") String format,
            @RequestParam(value = "download", defaultValue="false") boolean download
    )
    {
        log.info("/byArtist called with params: artist = " + artist + ", format = " + format);
        return CompletableFuture.completedFuture(getResponseEntity(artist, format, getByArtist, download));
    }

    @Async("asyncExecutor")
    @CacheEvict("getByTrack")
    @RequestMapping (path = "/byTrack")
    public CompletableFuture<ResponseEntity<?>> getByTrack(
            @RequestParam(value = "track") String track,
            @RequestParam(value = "format", defaultValue="json") String format,
            @RequestParam(value = "download", defaultValue="false") boolean download
    )
    {
        log.info("/byTrack called with params: track = " + track + ", format = " + format);
        return CompletableFuture.completedFuture(getResponseEntity(track, format, getByTrack, download));
    }

    private ResponseEntity<?> getResponseEntity(String param, String format, GetByOneParameterService byOneParam, boolean download) {
        AlbumConverter albumConverter;
        try {
            albumConverter = albumConverterFactory.build(format);
        } catch (IllegalArgumentException e){
            log.error("Wrong format",e);
            return ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.INVALID_FORMAT).createResponse());
        }
        CompletableFuture<List<Album>> albumCompletableFuture = byOneParam.getAlbums(param);
        albumCompletableFuture.join();
        List<Album> albums = null;
        try {
            albums = albumCompletableFuture.get();
            if (albums == null || albums.size() == 0){
                log.info("Didn't find anything suitable");
                if (format.equals("xml"))
                    return ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.NOTHING_FOUND_XML).createResponse());
                else
                    return ResponseEntity.ok(errorResponseFactory.build(ErrorResponseTypes.NOTHING_FOUND_JSON).createResponse());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get albums from CompletableFuture",e);
        }
        if (download) {
            String path = saveToDock.save(albums, param);
            File file = new File(path);
            ResponseEntity<?> responseEntity = null;
            try {
                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                responseEntity = ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .contentLength(file.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return responseEntity;
        } else {
            return ResponseEntity.ok(albumConverter.convert(albums));
        }
    }
}
