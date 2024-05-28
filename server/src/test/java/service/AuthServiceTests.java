package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.AuthService;
import service.UserService;
import util.CodedException;

public class AuthServiceTests {

    @Test
    public void createSessionPositive() throws CodedException {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        Assertions.assertDoesNotThrow(() -> authService.createSession(user));
    }

    @Test
    public void createSessionNegative() {
        var dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);
        var nonRegisteredUser = new UserData("nonRegisteredUser", "password", "user@byu.edu");
        Assertions.assertThrows(CodedException.class, () -> authService.createSession(nonRegisteredUser));
    }

    @Test
    public void deleteSessionPositive() throws CodedException {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        AuthData authData = authService.createSession(user);

        Assertions.assertDoesNotThrow(() -> authService.deleteSession(authData.getAuthToken()));
    }

    @Test
    public void deleteSessionNegative() {
        var dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        Assertions.assertDoesNotThrow(() -> authService.deleteSession("invalidToken"));
    }

    @Test
    public void getAuthDataPositiveTest() throws CodedException {
        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);
        var user = new UserData("bob", "password", "bob@byu.edu");
        userService.registerUser(user);

        var authService = new AuthService(dataAccess);
        AuthData authDataExpected = authService.createSession(user);
        Assertions.assertDoesNotThrow(() -> authService.getAuthData(authDataExpected.getAuthToken()));
    }

    @Test
    public void getAuthDataNegativeTest() {
        var dataAccess = new MemoryDataAccess();
        var authService = new AuthService(dataAccess);

        try {
            var result = authService.getAuthData("invalidToken");
            Assertions.assertNull(result);
        } catch (Exception e) {
            Assertions.fail("getAuthData threw an exception with an invalid token");
        }
    }
}