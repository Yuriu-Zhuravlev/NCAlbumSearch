package com.example.demo.beens.factories;

import com.example.demo.beens.impls.ErrorResponseFormat;
import com.example.demo.beens.impls.ErrorResponseNothingFoundJson;
import com.example.demo.beens.impls.ErrorResponseNothingFoundXml;
import com.example.demo.beens.interfaces.ErrorResponse;
import org.springframework.stereotype.Component;

@Component("errorResponse")
public class ErrorResponseFactory {
    public ErrorResponse build(ErrorResponseTypes type){
        switch (type){
            case INVALID_FORMAT:
                return new ErrorResponseFormat();
            case NOTHING_FOUND_XML:
                return new ErrorResponseNothingFoundXml();
            case NOTHING_FOUND_JSON:
                return new ErrorResponseNothingFoundJson();
            default:
                throw new IllegalArgumentException();
        }
    }
}
