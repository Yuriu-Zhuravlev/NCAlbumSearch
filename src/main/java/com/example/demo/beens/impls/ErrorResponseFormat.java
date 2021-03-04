package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.ErrorResponse;
import com.example.demo.classes.Album;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorResponseFormat implements ErrorResponse {
    private static final Logger log = Logger.getLogger(ErrorResponseFormat.class);

    @Override
    public String createResponse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("error",5);
            jsonObject.put("message", "Incorrect Format");
        } catch (JSONException e) {
            log.error("Failed to form json response", e);
        }
        return jsonObject.toString();
    }
}
