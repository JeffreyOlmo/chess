package model;

import chess.ChessGame;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // toString method using Gson
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
