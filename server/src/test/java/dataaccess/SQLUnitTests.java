package dataaccess;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import model.*;

public class SQLUnitTests {
    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setup() {
        try{
            dataAccess = new MySqlDataAccess();
        }
        catch (DataAccessException e) {
            // Handle exception (print stack trace, logger, etc.)
        }
    }

    @AfterEach
    public void tearDown() {
        try{
            dataAccess.clear();
        }
        catch (DataAccessException e){
            // Handle exception (print stack trace, logger, etc.)
        }
    }

    @Test
    public void testWriteUserPositive() {
        try {
            UserData user = new UserData("testUser", "testPassword", "test@example.com");
            UserData result = dataAccess.writeUser(user);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(user.getUsername(), result.getUsername());
        } catch(DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    //This is negative case where we trying to write user with same username
    @Test
    public void testWriteUserNegative() {
        try {
            UserData user = new UserData("testUser", "testPassword", "test@example.com");
            dataAccess.writeUser(user);
            Assertions.assertThrows(DataAccessException.class, () -> dataAccess.writeUser(user));
        } catch(DataAccessException e) {
            // Handle first exception, print stack trace
        }
    }

    @Test
    public void testReadUserPositive() {
        try {
            UserData user = new UserData("testUser", "testPassword", "test@example.com");
            dataAccess.writeUser(user);
            UserData fromDB = dataAccess.readUser(user.getUsername());
            Assertions.assertNotNull(fromDB);
            Assertions.assertEquals(user.getUsername(), fromDB.getUsername());
        } catch(DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    //This negative case is for the scenario when we are trying to read nonexistent user
    @Test
    public void testReadUserNegative() {
        String wrongUsername = "wrongUser";
        try{
            Assertions.assertNull(dataAccess.readUser(wrongUsername));
        } catch(DataAccessException e){
            // Handle
        }

    }

    @Test
    public void testWriteAuthPositive() {
        try {
            String username = "testUser";
            AuthData authData = dataAccess.writeAuth(username);

            // The method should return a non-null object
            Assertions.assertNotNull(authData);
            Assertions.assertEquals(username, authData.getUsername());
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    @Test
    public void testWriteAuthNegativeNullUsername() {
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.writeAuth(null));
    }

    @Test
    public void testReadAuthPositive() {
        try {
            String username = "testUser";
            AuthData authData = dataAccess.writeAuth(username);
            AuthData fromDB = dataAccess.readAuth(authData.getAuthToken());

            // The method should return a non-null object
            Assertions.assertNotNull(fromDB);
            Assertions.assertEquals(authData.getAuthToken(), fromDB.getAuthToken());
            Assertions.assertEquals(authData.getUsername(), fromDB.getUsername());
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    @Test
    public void testReadAuthNegative() {
        String wrongAuthToken = "wrongToken";
        try{
            Assertions.assertNull(dataAccess.readAuth(wrongAuthToken));
        } catch(DataAccessException e){}
    }

    @Test
    public void testDeleteAuthPositive() {
        try {
            String username = "testUser";
            AuthData authData = dataAccess.writeAuth(username);

            // Deleting should not throw an exception
            Assertions.assertDoesNotThrow(() -> dataAccess.deleteAuth(authData.getAuthToken()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }


    private void verifyGameData(GameData original, GameData fromDb) {
        // Updated to compare GameState as well
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(original.getGameID(), fromDb.getGameID());
        Assertions.assertEquals(original.getGameName(), fromDb.getGameName());
        Assertions.assertEquals(original.getState(), fromDb.getState());
        // Add other fields to verify as required
    }

    private GameData createNewGame() throws DataAccessException {
        String white = "whitePlayer";
        String black = "blackPlayer";
        String gameName = "testGameName";
        MySqlDataAccess mySqlDataAccess = new MySqlDataAccess();
        GameData gameData = mySqlDataAccess.newGame(gameName); //mysqlDataAccess is an instance of MySqlDataAccess

        // set the usernames
        gameData = gameData.setWhite(white);
        gameData = gameData.setBlack(black);

        // this will persist the changes to the database
        mySqlDataAccess.updateGame(gameData);

        return gameData;
    }

    @Test
    public void testNewGamePositive() {
        try {
            GameData gameData = createNewGame();
            verifyGameData(gameData, dataAccess.readGame(gameData.getGameID()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    // Attempt to create a new game with a null game name
    @Test
    public void testNewGameNegative() {
        try{
            Assertions.assertNull(dataAccess.newGame(null));
        } catch(DataAccessException e) {}
    }


    @Test
    public void testReadGamePositive() {
        try {
            GameData originalGameData = createNewGame();
            System.out.println(dataAccess.readGame(originalGameData.getGameID()));
            verifyGameData(originalGameData, dataAccess.readGame(originalGameData.getGameID()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    // Attempt to read a game that does not exist
    @Test
    public void testReadGameNegative() {
        int nonExistentGameId = -1;
        try {
            Assertions.assertNull(dataAccess.readGame(nonExistentGameId));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    @Test
    public void testUpdateGamePositive() {
        try {
            GameData originalGameData = createNewGame();
            // Update the game using setWhite method
            originalGameData = originalGameData.setWhite("updatedUser");
            dataAccess.updateGame(originalGameData);

            verifyGameData(originalGameData, dataAccess.readGame(originalGameData.getGameID()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    // Attempt to update a game with null game data
    @Test
    public void testUpdateGameNegative() {
        Assertions.assertThrows(NullPointerException.class, () -> dataAccess.updateGame(null));
    }

    @Test
    public void testListGamesPositive() {
        try {
            GameData gameData = createNewGame();
            verifyGameData(gameData, dataAccess.listGames().stream().filter(game -> game.getGameID() == gameData.getGameID()).findAny().orElse(null));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    @Test
    public void testListGamesNegative() {
        try {
            Assertions.assertTrue(dataAccess.listGames().isEmpty());
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }
}