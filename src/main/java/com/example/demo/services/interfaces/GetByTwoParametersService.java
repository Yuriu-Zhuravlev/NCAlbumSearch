package com.example.demo.services.interfaces;

import com.example.demo.classes.Album;

import java.util.concurrent.CompletableFuture;

public interface GetByTwoParametersService {
    CompletableFuture<Album> getAlbum(String param1, String artist);
}
