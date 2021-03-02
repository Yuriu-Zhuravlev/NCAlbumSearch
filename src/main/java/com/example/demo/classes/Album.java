package com.example.demo.classes;

import java.util.Map;

public class Album {
    private String name;
    private String author;
    private String genre;
    private String pictureURL;
    private Map<String,Integer> songs;
    private int similarResults;

    public Album(String name, String author, String genre, String pictureURL, Map<String, Integer> songs) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.pictureURL = pictureURL;
        this.songs = songs;
        similarResults = 1;
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

    public int getSimilarResults() {
        return similarResults;
    }

    public void setSimilarResults(int similarResults) {
        this.similarResults = similarResults;
    }
}
