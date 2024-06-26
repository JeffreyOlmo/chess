package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.GameService;
import util.CodedException;
import chess.ChessGame;

public class GameServiceTests {

    @Test
    public void createGamePositive() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        Assertions.assertDoesNotThrow(() -> gameService.createGame("Chess Game 1"));
    }

    @Test
    public void createGameNegative() {
        // It's unclear what a negative test for createGame might look like.
    }

    @Test
    public void joinGamePositive() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        GameData gameData = gameService.createGame("Chess Game 2");
        Assertions.assertDoesNotThrow(() -> gameService.joinGame("bob", ChessGame.TeamColor.WHITE, gameData.getGameID()));
    }

    @Test
    public void joinGameNegativeInvalidGame() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        Assertions.assertThrows(CodedException.class, () -> gameService.joinGame("bob", ChessGame.TeamColor.WHITE, 999999));
    }

    @Test
    public void joinGameNegativeInvalidColor() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        GameData gameData = gameService.createGame("Chess Game 3");
        gameService.joinGame("bob", ChessGame.TeamColor.WHITE, gameData.getGameID());
        Assertions.assertThrows(CodedException.class, () -> gameService.joinGame("alice", ChessGame.TeamColor.WHITE, gameData.getGameID()));
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        Assertions.assertDoesNotThrow(gameService::listGames);
    }

    @Test
    public void joinGamePositiveAssignColor() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        GameData gameData = gameService.createGame("Chess Game X1");
        Assertions.assertDoesNotThrow(() -> gameService.joinGame("bob", ChessGame.TeamColor.WHITE, gameData.getGameID()));
    }

    @Test
    public void joinGameNegativeAssignColor() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var gameService = new GameService(dataAccess);
        GameData gameData = gameService.createGame("Chess Game X2");
        gameService.joinGame("bob", ChessGame.TeamColor.WHITE, gameData.getGameID());
        Assertions.assertThrows(CodedException.class, () -> gameService.joinGame("alice", ChessGame.TeamColor.WHITE, gameData.getGameID()));
    }

}