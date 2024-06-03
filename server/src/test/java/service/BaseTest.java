package service;

import org.junit.jupiter.api.*;
import passoff.server.TestServerFacade;
import server.Server;


public class BaseTest {

    protected static Server server;
    protected static TestServerFacade serverFacade;

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
    public static void stopServer() {
        server.stop();
    }
}
