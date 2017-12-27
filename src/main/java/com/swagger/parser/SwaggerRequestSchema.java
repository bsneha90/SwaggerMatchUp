package com.swagger.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsneha on 18/07/17.
 */
public class SwaggerRequestSchema {

    private ArrayList<RequestParameter> parameters;
    private String ErrorMessage;
    private List<String> consumes;

    public SwaggerRequestSchema() {
        this.parameters = new ArrayList<>();
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public ArrayList<RequestParameter> getParameters() {
        return parameters;
    }

    public List<String> getConsumes() {
        return consumes;
    }

    public void setConsumes(List<String> consumes) {
        this. consumes=consumes;
    }
}

