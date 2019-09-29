package com.creativityinspire.models;

import java.util.List;
import java.util.Map;

public class ConnectResponse {
    private Map<String, String> _responseValues;
    private List<Creation> _creations;

    public ConnectResponse(Map<String, String> responseValues, List<Creation> creations) {
        _responseValues = responseValues;
        _creations = creations;
    }

    public ConnectResponse() {}

    public Map<String, String> getResponseValues() {
        return _responseValues;
    }

    public List<Creation> getCreations() {
        return _creations;
    }

}
