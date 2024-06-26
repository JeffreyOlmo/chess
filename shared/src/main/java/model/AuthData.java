package model;

import com.google.gson.Gson;
import java.util.UUID;

public class AuthData {
    private String authToken;
    private String username;

    // Constructor
    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getAuthToken() {
        return authToken;
    }


    public String getUsername() {
        return username;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}