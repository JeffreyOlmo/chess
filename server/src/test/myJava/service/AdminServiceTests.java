package myJava.service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AdminService;
import service.AuthService;
import service.GameService;
import service.UserService;
import util.CodedException;

import java.util.ArrayList;

public class AdminServiceTests {

    private MemoryDataAccess dataAccess;
    private AdminService service;

    @BeforeEach
    public void setup(){
        dataAccess = new MemoryDataAccess();
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
        var memoryDataAccess = new MemoryDataAccess();
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
