package ui.facade;


import chess.ChessGame;

public class JoinRequestData {
    private ChessGame.TeamColor playerColor;
    private int gameID;

    public JoinRequestData(ChessGame.TeamColor playerColor, int gameID) {
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

