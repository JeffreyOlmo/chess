package myJava.service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.CodedException;
import service.UserService;

public class UserServiceTests {

    @Test
    public void registerUser() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("joe", "password", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
    }


    @Test
    public void registerUserDuplicate() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("joe", "password", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyUsername() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("", "password", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyPassword() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("jane", "", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }

    @Test
    public void registerUserEmptyUsernameAndPassword() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("", "", "jane@byu.edu");

        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }
}

