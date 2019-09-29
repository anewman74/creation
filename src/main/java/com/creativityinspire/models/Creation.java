package com.creativityinspire.models;

public class Creation {
    private String _title;
    private String _poem;
    private String _username;
    private String _userValue;
    private boolean _isPrivate;

    public Creation(String title, String poem, String username, String userValue, boolean isPrivate) {
        _title = title;
        _poem = poem;
        _username = username;
        _userValue = userValue;
        _isPrivate = isPrivate;
    }

    public String getTitle() {
        return _title;
    }

    public String getPoem() {
        return _poem;
    }

    public String getUsername() {
        return _username;
    }

    public String getUserValue() {
        return _userValue;
    }

    public boolean getIsPrivate() {
        return _isPrivate;
    }
}
