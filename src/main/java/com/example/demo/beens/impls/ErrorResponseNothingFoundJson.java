package com.example.demo.beens.impls;

import com.example.demo.beens.interfaces.ErrorResponse;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class ErrorResponseNothingFoundJson implements ErrorResponse {
    private static final Logger log = Logger.getLogger(ErrorResponseNothingFoundJson.class);
    @Override
    public String createResponse() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("error",6);
            jsonObject.put("message", "Nothing Found");
        } catch (JSONException e) {
            log.error("Failed to form json response", e);
        }
        return jsonObject.toString();
    }
}
