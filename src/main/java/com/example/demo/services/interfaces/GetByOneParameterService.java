package com.example.demo.services.interfaces;

import com.example.demo.classes.Album;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface GetByOneParameterService {
    CompletableFuture<List<Album>> getAlbums(String param);
}
