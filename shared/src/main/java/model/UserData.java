package model;

import com.google.gson.Gson;

public class UserData {
    private String username;
    private String password;
    private String email;

    // Constructor
    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // toString method using Gson
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
