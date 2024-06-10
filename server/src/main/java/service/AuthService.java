package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import util.CodedException;
import org.mindrot.jbcrypt.BCrypt;


/**
 * Provides endpoints for authorizing access.
 */
public class AuthService {

    private final DataAccess dataAccess;

    public AuthService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData createSession(UserData user) throws CodedException {
        try {
            UserData loggedInUser = dataAccess.readUser(user.getUsername());
            System.out.println((loggedInUser != null && BCrypt.checkpw(user.getPassword(), loggedInUser.getPassword())));
            System.out.println(user.getPassword());
            System.out.println(loggedInUser == null);
            if (loggedInUser != null && BCrypt.checkpw(user.getPassword(), loggedInUser.getPassword())) {
                return dataAccess.writeAuth(loggedInUser.getUsername());
            }
            System.out.println("Throwing Coded exception 401");
            throw new CodedException(401, "Invalid username or password");
        } catch (DataAccessException ex) {
            throw new CodedException(500, "Internal server error");
        }
    }

    /**
     * Deletes a user's session. If the token is not valid then no error is generated.
     *
     * @param authToken that currently represents a user.
     */
    public void deleteSession(String authToken) throws CodedException {
        try {
            dataAccess.deleteAuth(authToken);
        } catch (DataAccessException ex) {
            throw new CodedException(500, "Internal server error");
        }
    }

    public AuthData getAuthData(String authToken) throws CodedException {
        try {
            return dataAccess.readAuth(authToken);
        } catch (DataAccessException ignored) {
            throw new CodedException(500, "Internal server error");
        }
    }
}

