package client;

import chess.ChessGame;
import ui.facade.ResponseException;
import ui.facade.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testClear_Success() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void testRegister_Success() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String email = "test@example.com";

        // Act
        AuthData authData = serverFacade.register(username, password, email);

        // Assert
        assertNotNull(authData);
        assertNotNull(authData.getAuthToken());
        assertEquals(username, authData.getUsername());
    }

    @Test
    public void testLogin_Success() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";

        // Act
        AuthData authData = serverFacade.login(username, password);

        // Assert
        assertNotNull(authData);
        assertNotNull(authData.getAuthToken());
        assertEquals(username, authData.getUsername());
    }

    @Test
    public void testRegister_InvalidInput_ThrowsResponseException() {
        // Arrange
        String username = ""; // Empty username
        String password = "testpassword";
        String email = "test@example.com";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.register(username, password, email));
    }

    @Test
    public void testLogout_Success() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();

        serverFacade.logout(authToken);
    }

    @Test
    public void testCreateGame_Success() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String gameName = "Test Game";
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();

        // Act
        GameData gameData = serverFacade.createGame(authToken, gameName);

        // Assert
        assertNotNull(gameData);
        assertNull(gameData.getWhiteUsername());
        assertNull(gameData.getBlackUsername());
    }

    @Test
    public void testCreateGame_InvalidAuthToken_ThrowsResponseException() {
        // Arrange
        String gameName = "Test Game";
        String invalidAuthToken = "invalid_token";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.createGame(invalidAuthToken, gameName));
    }

    @Test
    public void testListGames_Success() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();

        // Act
        GameData[] games = serverFacade.listGames(authToken);

        // Assert
        assertNotNull(games);
        // Add more assertions based on the expected behavior of listGames
    }

    @Test
    public void testListGames_InvalidAuthToken_ThrowsResponseException() {
        // Arrange
        String invalidAuthToken = "invalid_token";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.listGames(invalidAuthToken));
    }

    @Test
    public void testJoinGame_Success() throws ResponseException {
        // Arrange
        String username = "testuser1";
        String password = "testpassword1";
        String gameName = "Test Game";
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();
        GameData gameData = serverFacade.createGame(authToken, gameName);
        int gameID = gameData.getGameID();

        // Act
        GameData joinedGameData = serverFacade.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE);

        // Assert
        assertNotNull(joinedGameData);
        assertEquals(gameID, joinedGameData.getGameID());
        assertEquals(username, joinedGameData.getWhiteUsername());
        assertNull(joinedGameData.getBlackUsername());
        assertEquals(GameData.State.UNDECIDED, joinedGameData.getState());
    }

    @Test
    public void testJoinGame_InvalidAuthToken_ThrowsResponseException() {
        // Arrange
        String invalidAuthToken = "invalid_token";
        int gameID = 123; // Arbitrary game ID

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(invalidAuthToken, gameID, ChessGame.TeamColor.WHITE));
    }

    @Test
    public void testJoinGame_InvalidGameID_ThrowsResponseException() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();
        int invalidGameID = -1; // Invalid game ID

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(authToken, invalidGameID, ChessGame.TeamColor.WHITE));
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}
