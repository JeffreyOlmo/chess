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
    public void testWriteUser_Positive() {
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
    public void testWriteUser_Negative() {
        try {
            UserData user = new UserData("testUser", "testPassword", "test@example.com");
            dataAccess.writeUser(user);
            Assertions.assertThrows(DataAccessException.class, () -> dataAccess.writeUser(user));
        } catch(DataAccessException e) {
            // Handle first exception, print stack trace
        }
    }

    @Test
    public void testReadUser_Positive() {
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
    public void testReadUser_Negative() {
        String wrongUsername = "wrongUser";
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.readUser(wrongUsername));
    }

    @Test
    public void testWriteAuth_Positive() {
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
    public void testReadAuth_Positive() {
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
    public void testReadAuth_Negative() {
        String wrongAuthToken = "wrongToken";
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.readAuth(wrongAuthToken));
    }

    @Test
    public void testDeleteAuth_Positive() {
        try {
            String username = "testUser";
            AuthData authData = dataAccess.writeAuth(username);

            // Deleting should not throw an exception
            Assertions.assertDoesNotThrow(() -> dataAccess.deleteAuth(authData.getAuthToken()));
            // Trying to retrieve it afterward should throw an exception
            Assertions.assertThrows(DataAccessException.class, () -> dataAccess.readAuth(authData.getAuthToken()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    @Test
    public void testDeleteAuth_Negative() {
        String wrongAuthToken = "wrongToken";
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth(wrongAuthToken));
    }

    private void verifyGameData(GameData original, GameData fromDb) {
        // Updated to compare GameState as well
        Assertions.assertNotNull(fromDb);
        Assertions.assertEquals(original.getGameID(), fromDb.getGameID());
        Assertions.assertEquals(original.getGameName(), fromDb.getGameName());
        Assertions.assertEquals(original.getState(), fromDb.getState());
        // Add other fields to verify as required
    }

    private GameData createNewGame() {
        int gameID = 1; // This would normally be generated by your database.
        String whiteUsername = "username1"; // Use existing users from your tests.
        String blackUsername = "username2"; // Use existing users from your tests.
        String gameName = "testGameName";
        ChessGame game = new ChessGame();
        GameData.State state = GameData.State.UNDECIDED; // Initial game state is usually undecided.

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game, state);
    }

    @Test
    public void testNewGame_Positive() {
        try {
            GameData gameData = createNewGame();
            verifyGameData(gameData, dataAccess.readGame(gameData.getGameID()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    // Attempt to create a new game with a null game name
    @Test
    public void testNewGame_Negative() {
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.newGame(null));
    }

    @Test
    public void testReadGame_Positive() {
        try {
            GameData originalGameData = createNewGame();
            verifyGameData(originalGameData, dataAccess.readGame(originalGameData.getGameID()));
        } catch (DataAccessException e) {
            // Handle exception, print stack trace
        }
    }

    // Attempt to read a game that does not exist
    @Test
    public void testReadGame_Negative() {
        int nonExistentGameId = -1;
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.readGame(nonExistentGameId));
    }

    @Test
    public void testUpdateGame_Positive() {
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
    public void testUpdateGame_Negative() {
        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.updateGame(null));
    }
}