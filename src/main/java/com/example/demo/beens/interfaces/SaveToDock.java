package com.example.demo.beens.interfaces;

import com.example.demo.classes.Album;

import java.util.List;

public interface SaveToDock {
    String save(List<Album> albums, String name);
}
