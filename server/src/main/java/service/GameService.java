package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import util.CodedException;

import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }


    public Collection<GameData> listGames() throws CodedException {
        try {
            return dataAccess.listGames();
        } catch (DataAccessException ex) {
            throw new CodedException(500, "Server error");
        }
    }

    public GameData createGame(String gameName) throws CodedException {
        try {
            return dataAccess.newGame(gameName);
        } catch (DataAccessException ex) {
            throw new CodedException(500, "Server error");
        }
    }

    public GameData joinGame(String username, ChessGame.TeamColor color, int gameID) throws CodedException {
        try {
            var gameData = dataAccess.readGame(gameID);
            if (gameData == null) {
                throw new CodedException(400, "Unknown game");
            } else if (color == null) {
                return gameData;
            } else if (gameData.isGameOver()) {
                throw new CodedException(403, "Game is over");
            } else {
                if (color == ChessGame.TeamColor.WHITE) {
                    if (gameData.whiteUsername() == null || gameData.whiteUsername().equals(username)) {
                        gameData = gameData.setWhite(username);
                    } else {
                        throw new CodedException(403, "Color taken");
                    }
                } else if (color == ChessGame.TeamColor.BLACK) {
                    if (gameData.blackUsername() == null || gameData.blackUsername().equals(username)) {
                        gameData = gameData.setBlack(username);
                    } else {
                        throw new CodedException(403, "Color taken");
                    }
                }
                dataAccess.updateGame(gameData);
            }
            return gameData;
        } catch (DataAccessException ignored) {
            throw new CodedException(500, "Server error");
        }
    }

}
