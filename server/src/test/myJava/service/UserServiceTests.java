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
        var user = new UserData("joe", "info", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
    }


    @Test
    public void registerUserDuplicate() {
        var service = new UserService(new MemoryDataAccess());
        var user = new UserData("juan", "info", "joe@byu.edu");

        Assertions.assertDoesNotThrow(() -> service.registerUser(user));
        Assertions.assertThrows(CodedException.class, () -> service.registerUser(user));
    }
}
