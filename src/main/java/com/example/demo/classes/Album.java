package com.example.demo.classes;

import java.util.Map;

public class Album {
    private String name;
    private String author;
    private String genre;
    private String pictureURL;
    private Map<String,Integer> songs;

    public Album(String name, String author, String genre, String pictureURL, Map<String, Integer> songs) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.pictureURL = pictureURL;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public Map<String, Integer> getSongs() {
        return songs;
    }
}
