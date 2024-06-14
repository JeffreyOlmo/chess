package client;

import chess.ChessGame;
import chess.ChessGameDeserializer;
import chess.ChessGameSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.facade.ResponseException;
import ui.facade.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import ui.facade.Response;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;
    private final Gson defaultGson = new Gson();
    private final Gson customGson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameSerializer())
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

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
    public void testClearSuccess() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    public void testRegisterSuccess() throws ResponseException {
        // Arrange
        String username = "testuser7";
        String password = "testpassword7";
        String email = "test@example.com";

        // Act
        AuthData authData = serverFacade.register(username, password, email);

        // Assert
        assertNotNull(authData);
        assertNotNull(authData.getAuthToken());
        assertEquals(username, authData.getUsername());
    }

    @Test
    public void testLoginSuccess() throws ResponseException {
        // Arrange
        String username = "testuser12";
        String password = "testpassword12";

        // Act
        serverFacade.register(username, password, "");
        AuthData authData = serverFacade.login(username, password);

        // Assert
        assertNotNull(authData);
        assertNotNull(authData.getAuthToken());
        assertEquals(username, authData.getUsername());
    }

    @Test
    public void testRegisterInvalidInputThrowsResponseException() {
        // Arrange
        String username = ""; // Empty username
        String password = "testpassword";
        String email = "test@example.com";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.register(username, password, email));
    }

    @Test
    public void testLogoutSuccess() throws ResponseException {
        // Arrange
        String username = "testuser5";
        String password = "testpassword5";
        serverFacade.register(username, password, "");
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();

        serverFacade.logout(authToken);
    }

    @Test
    public void testCreateGameSuccess() throws ResponseException {
        // Arrange
        String username = "testuser";
        String password = "testpassword";
        String gameName = "Test Game";
        serverFacade.register(username, password, "");
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
    public void testCreateGameInvalidAuthTokenThrowsResponseException() {
        // Arrange
        String gameName = "Test Game";
        String invalidAuthToken = "invalid_token";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.createGame(invalidAuthToken, gameName));
    }

    @Test
    public void testListGamesSuccess() throws ResponseException {
        // Arrange
        String username = "testuser15";
        String password = "testpassword15";
        serverFacade.register(username, password, "");
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();
        serverFacade.createGame(authToken, "TestGame");
        serverFacade.createGame(authToken, "TestGame2");

        // Act
        GameData[] games = serverFacade.listGames(authToken);
        System.out.println("Number of games returned: " + games.length);

        // Assert
        assertNotNull(games, "Games array should not be null");
        assertEquals(2, games.length, "Should return exactly two games");

        // Further assertions to check detailed game data
        // Check first game
        GameData firstGame = games[0];
        assertNotNull(firstGame, "First game should not be null");
        assertEquals("TestGame", firstGame.getGameName(), "Game name should match");
        assertNotNull(firstGame.getGame(), "Game object should not be null");
        assertNotNull(firstGame.getGame().getBoard(), "Game board should not be null");
        assertEquals(ChessGame.TeamColor.WHITE, firstGame.getGame().getTeamTurn(), "Team turn should be WHITE for the first game");

        // Check second game
        GameData secondGame = games[1];
        assertNotNull(secondGame, "Second game should not be null");
        assertEquals("TestGame2", secondGame.getGameName(), "Game name should match");
        assertNotNull(secondGame.getGame(), "Game object should not be null");
        assertNotNull(secondGame.getGame().getBoard(), "Game board should not be null");
        assertEquals(ChessGame.TeamColor.WHITE, secondGame.getGame().getTeamTurn(), "Team turn should be WHITE for the second game");

        // Output some detailed properties for debugging
        System.out.println("First Game Details: " + firstGame.getGame().getBoard().toString());
        System.out.println("Second Game Details: " + secondGame.getGame().getBoard().toString());
    }

    @Test
    public void testGsonDeserialization() {
        String testJson = "{\"games\":[{\"gameID\":967,\"gameName\":\"TestGame\",\"game\":{\"board\":{\"board\":{\"21\":{\"pieceColor\":\"WHITE\",\"type\":\"PAWN\",\"hasMoved\":false},\"87\":{\"pieceColor\":\"BLACK\",\"type\":\"KNIGHT\",\"hasMoved\":false}}},\"teamTurn\":\"WHITE\"}}]}";
        try {
            Response response = customGson.fromJson(testJson, Response.class);
            System.out.println("Number of games: " + (response.getGames() != null ? response.getGames().length : "null"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void testListGamesInvalidAuthTokenThrowsResponseException() {
        // Arrange
        String invalidAuthToken = "invalid_token";

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.listGames(invalidAuthToken));
    }

    @Test
    public void testJoinGameSuccess() throws ResponseException {
        // Arrange
        String username = "testuser6";
        String password = "testpassword6";
        String gameName = "Test Game2";
        serverFacade.register(username, password, "");
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
    public void testJoinGameInvalidAuthTokenThrowsResponseException() {
        // Arrange
        String invalidAuthToken = "invalid_token";
        int gameID = 123; // Arbitrary game ID

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(invalidAuthToken, gameID, ChessGame.TeamColor.WHITE));
    }

    @Test
    public void testJoinGameInvalidGameIDThrowsResponseException() throws ResponseException {
        // Arrange
        String username = "testuser13";
        String password = "testpassword13";
        serverFacade.register(username, password, "");
        AuthData authData = serverFacade.login(username, password);
        String authToken = authData.getAuthToken();
        int invalidGameID = -1; // Invalid game ID

        // Act and Assert
        assertThrows(ResponseException.class, () -> serverFacade.joinGame(authToken, invalidGameID, ChessGame.TeamColor.WHITE));
    }

}
