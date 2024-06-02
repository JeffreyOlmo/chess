package service;

import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;
import util.CodedException;
import service.UserService;

public class UserServiceTests {

    private UserService service;
    private MySqlDataAccess dataAccess;

    @BeforeEach
    public void setup() {
        try{
            dataAccess = new MySqlDataAccess();
            service = new UserService(dataAccess);
        }
        catch (DataAccessException e){}
    }

    @AfterEach
    public void tearDown() {
        try{
            dataAccess.clear();
        }
        catch (DataAccessException e){}

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

