package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;
import service.AuthService;
import service.UserService;
import util.CodedException;

public class AuthServiceTests {

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

    @Test
    public void createSessionPositive() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        Assertions.assertDoesNotThrow(() -> authService.createSession(user));
    }

    @Test
    public void createSessionNegative() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var authService = new AuthService(dataAccess);
        var nonRegisteredUser = new UserData("nonRegisteredUser", "password", "user@byu.edu");
        Assertions.assertThrows(CodedException.class, () -> authService.createSession(nonRegisteredUser));
    }

    @Test
    public void deleteSessionPositive() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        AuthData authData = authService.createSession(user);

        Assertions.assertDoesNotThrow(() -> authService.deleteSession(authData.getAuthToken()));
    }

    @Test
    public void deleteSessionNegative() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var authService = new AuthService(dataAccess);

        Assertions.assertDoesNotThrow(() -> authService.deleteSession("invalidToken"));
    }

    @Test
    public void getAuthDataPositiveTest() throws CodedException, DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        AuthData authDataExpected = authService.createSession(user);
        Assertions.assertDoesNotThrow(() -> authService.getAuthData(authDataExpected.getAuthToken()));
    }

    @Test
    public void getAuthDataNegativeTest() throws DataAccessException {
        var dataAccess = new MySqlDataAccess();
        var authService = new AuthService(dataAccess);

        try {
            var result = authService.getAuthData("invalidToken");
            Assertions.assertNull(result);
        } catch (Exception e) {
            Assertions.fail("getAuthData threw an exception with an invalid token");
        }
    }
}