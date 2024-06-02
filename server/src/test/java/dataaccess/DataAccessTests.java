package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import passoff.server.TestServerFacade;
import server.Server;

import java.util.List;

public class DataAccessTests {

    private DataAccess initializeDatabase(Class<? extends DataAccess> databaseClass) throws Exception {
        DataAccess db = databaseClass.getDeclaredConstructor().newInstance();
        db.clear();
        return db;
    }

    private static TestServerFacade serverFacade;
    private static Server server;


    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MySqlDataAccess.class})
    public void testWriteAndReadUser(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = initializeDatabase(dbClass);
        UserData user = new UserData("alice", "password123", "alice@byu.com");

        Assertions.assertEquals(user, db.readUser(user.getUsername()));
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MySqlDataAccess.class})
    public void testWriteAndReadAuth(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = initializeDatabase(dbClass);
        UserData user = new UserData("bob", "secret789", "bob@byu.com");

        var authData = db.writeAuth(user.getUsername());
        Assertions.assertEquals(user.getUsername(), authData.getUsername());
        Assertions.assertFalse(authData.getAuthToken().isEmpty());

        var retrievedAuthData = db.readAuth(authData.getAuthToken());
        Assertions.assertEquals(user.getUsername(), retrievedAuthData.getUsername());
        Assertions.assertEquals(authData.getAuthToken(), retrievedAuthData.getAuthToken());

        var newAuthData = db.writeAuth(user.getUsername());
        Assertions.assertEquals(user.getUsername(), newAuthData.getUsername());
        Assertions.assertNotEquals(authData.getAuthToken(), newAuthData.getAuthToken());
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MySqlDataAccess.class})
    public void testWriteReadAndUpdateGame(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = initializeDatabase(dbClass);

        var game = db.newGame("classic");
        var updatedGame = game.setBlack("charlie");
        db.updateGame(updatedGame);

        var retrievedGame = db.readGame(game.getGameID());
        Assertions.assertEquals(updatedGame, retrievedGame);
    }

    @ParameterizedTest
    @ValueSource(classes = {MySqlDataAccess.class, MySqlDataAccess.class})
    public void testListGames(Class<? extends DataAccess> dbClass) throws Exception {
        DataAccess db = initializeDatabase(dbClass);

        var games = List.of(db.newGame("rapid"), db.newGame("bullet"), db.newGame("blitz"));
        var retrievedGames = db.listGames();
        Assertions.assertIterableEquals(games, retrievedGames);
    }
}
