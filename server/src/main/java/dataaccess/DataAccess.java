package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;


public interface DataAccess {
    void clear() throws DataAccessException;

    UserData writeUser(UserData user) throws DataAccessException;

    UserData readUser(String userName) throws DataAccessException;

    AuthData writeAuth(String username) throws DataAccessException;

    AuthData readAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    GameData newGame(String gameName) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    GameData readGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;
}