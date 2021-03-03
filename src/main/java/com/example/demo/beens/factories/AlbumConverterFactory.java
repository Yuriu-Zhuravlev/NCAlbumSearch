package com.example.demo.beens.factories;

import com.example.demo.beens.impls.AlbumConverterJson;
import com.example.demo.beens.impls.AlbumConverterXml;
import com.example.demo.beens.interfaces.AlbumConverter;
import org.springframework.stereotype.Component;

@Component("albumConverterFactory")
public class AlbumConverterFactory {
    public AlbumConverter build(String format){
        if (format.equals("xml")){
            return new AlbumConverterXml();
        } else {
            if (format.equals("json")){
                return new AlbumConverterJson();
            } else {
                throw new IllegalArgumentException("Incorrect format");
            }
        }
    }
}
