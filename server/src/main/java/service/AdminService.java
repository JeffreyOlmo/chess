package service;


import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import util.CodedException;


public class AdminService {

    private final DataAccess dataAccess;

    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearApplication() throws CodedException {
        try {
            dataAccess.clear();
        } catch (DataAccessException ex) {
            throw new CodedException(500, "Server error");
        }
    }
}

