package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.CodedException;
import service.UserService;

public class UserServiceTests {

    @Test
    public void registerUser() throws DataAccessException {
        var service = new UserService(new MySqlDataAccess());
        var user = new UserData("joe", "password", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
    }


    @Test
    public void registerUserDuplicate() throws DataAccessException {
        var service = new UserService(new MySqlDataAccess());
        var user = new UserData("joe", "password", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyUsername() throws DataAccessException {
        var service = new UserService(new MySqlDataAccess());
        var user = new UserData("", "password", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyPassword() throws DataAccessException {
        var service = new UserService(new MySqlDataAccess());
        var user = new UserData("jane", "", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyUsernameAndPassword() throws DataAccessException {
        var service = new UserService(new MySqlDataAccess());
        var user = new UserData("", "", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }
}

