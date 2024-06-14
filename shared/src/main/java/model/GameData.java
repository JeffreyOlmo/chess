package model;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GameData {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private ChessGame game;
    private State state;

    public enum State {
        WHITE,
        BLACK,
        DRAW,
        UNDECIDED
    }

    // Constructor
    public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, State state) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
        this.state = state;
    }

    // Getters
    public int getGameID() {
        return gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public ChessGame getGame() {
        return game;
    }

    public State getState() {
        return state;
    }

    public GameData setState(State state) {
        return new GameData(this.gameID, this.whiteUsername, this.blackUsername, this.gameName, this.game, state);
    }

    // Methods from original record
    public boolean isGameOver() {
        return state != State.UNDECIDED;
    }

    public GameData setWhite(String userName) {
        return new GameData(this.gameID, userName, this.blackUsername, this.gameName, this.game, this.state);
    }

    public GameData setBlack(String userName) {
        return new GameData(this.gameID, this.whiteUsername, userName, this.gameName, this.game, this.state);
    }
}