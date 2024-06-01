package model;

import com.google.gson.Gson;

public class UserData {
    public String username;
    public String password;
    public String email;

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
