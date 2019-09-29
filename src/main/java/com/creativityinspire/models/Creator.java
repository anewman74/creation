package com.creativityinspire.models;

public class Creator {
    private String _username;
    private String _email;
    private String _password;

    public Creator(String username, String email, String password) {
        _username = username;
        _email = email;
        _password = password;
    }

    public String getUsername() {
        return _username;
    }

    public String getEmail() {
        return _email;
    }

    public String getPassword() {
        return _password;
    }
}
