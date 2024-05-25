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
        } catch (DataAccessException ignored) {
            throw new CodedException(500, "Server error");
        }
    }

    public GameData createGame(String gameName) throws CodedException {
        try {
            return dataAccess.newGame(gameName);
        } catch (DataAccessException ignored) {
            throw new CodedException(500, "Server error");
        }
    }

    public GameData joinGame(String username, ChessGame.TeamColor color, int gameId) throws CodedException {
        try {
            GameData loadedGame = dataAccess.readGame(gameId);
            if (loadedGame == null) {
                throw new CodedException(400, "Unknown game");
            } else if (color == null) {
                return loadedGame;
            } else if (loadedGame.isGameOver()) {
                throw new CodedException(403, "Game is over");
            } else {
                assignColorToPlayer(username, color, loadedGame);
                dataAccess.updateGame(loadedGame);
            }
            return loadedGame;
        } catch (DataAccessException ignored) {
            throw new CodedException(500, "Server error");
        }
    }

    private void assignColorToPlayer(String username, ChessGame.TeamColor color, GameData loadedGame) throws CodedException {
        if (color == ChessGame.TeamColor.WHITE) {
            if (loadedGame.whiteUsername() == null || loadedGame.whiteUsername().equals(username)) {
                loadedGame = loadedGame.setWhite(username);
            } else {
                throw new CodedException(403, "Color taken");
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (loadedGame.blackUsername() == null || loadedGame.blackUsername().equals(username)) {
                loadedGame = loadedGame.setBlack(username);
            } else {
                throw new CodedException(403, "Color taken");
            }
        }
    }
}