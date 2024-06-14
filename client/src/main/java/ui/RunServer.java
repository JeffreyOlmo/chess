package ui;

import server.Server;

public class RunServer {
    public static void main(String[] args) {
        Server server = new Server();
        int port = server.run(8080); // Use 0 to assign a random available port
        String serverName = "localhost:" + port;
        System.out.println("Server started at " + serverName);
    }
}

