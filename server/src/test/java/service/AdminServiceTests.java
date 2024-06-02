package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.CodedException;

import java.util.ArrayList;

public class AdminServiceTests {

    private MySqlDataAccess dataAccess;
    private AdminService service;

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        service = new AdminService(dataAccess);
    }

    @Test
    public void clearApplicationDataTest() throws Exception {
        dataAccess.writeUser(new UserData("user", "pass", "user@mail.com"));
        dataAccess.newGame("Chess game");

        Assertions.assertNotNull(dataAccess.readUser("user"));
        Assertions.assertFalse(new ArrayList<>(dataAccess.listGames()).isEmpty());

        service.clearApplication();

        Assertions.assertNull(dataAccess.readUser("user"));
        Assertions.assertTrue(new ArrayList<>(dataAccess.listGames()).isEmpty());
    }

    @Test
    public void clear() throws Exception {
        var memoryDataAccess = new MySqlDataAccess();
        var userService = new UserService(memoryDataAccess);
        var user = new UserData("jeff", "password", "jeff@byu.edu");
        var authData = userService.registerUser(user);

        var gameService = new GameService(memoryDataAccess);
        gameService.createGame("testGame");

        var service = new AdminService(memoryDataAccess);
        Assertions.assertDoesNotThrow(service::clearApplication);

        var authService = new AuthService(memoryDataAccess);
        Assertions.assertThrows(CodedException.class, () -> authService.createSession(user));
        Assertions.assertNull(authService.getAuthData(authData.getAuthToken()));

        var games = gameService.listGames();
        Assertions.assertEquals(0, games.size());
    }
}
