package com.example.demo.beens.impls;


import com.example.demo.beens.interfaces.URLReplacer;
import org.springframework.stereotype.Component;

@Component("URLReplacer")
public class URLReplacerImpl implements URLReplacer {
    @Override
    public String replaceSpec(String text) {
        String result = text.replace("&","%26").replace("/","%2F")
                .replace("?","%3F").replace("=","%3D").replace("#","%2523");
        return result;
    }
}
