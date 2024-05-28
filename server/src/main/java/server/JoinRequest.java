package server;

import chess.ChessGame;

public class JoinRequest {
    private ChessGame.TeamColor playerColor;
    private int gameID;

    public JoinRequest(ChessGame.TeamColor playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }

    public int getGameID() {
        return gameID;
    }
}
