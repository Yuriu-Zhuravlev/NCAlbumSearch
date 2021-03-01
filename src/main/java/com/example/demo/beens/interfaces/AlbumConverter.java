package com.example.demo.beens.interfaces;

import com.example.demo.classes.Album;

import java.util.List;

public interface AlbumConverter {
    String toXML(List<Album> albums);
    String toJSON(List<Album> albums);
}
