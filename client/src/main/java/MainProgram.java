import ui.facade.ChessClient;
import java.util.Scanner;
import static ui.EscapeSequences.*;


import java.util.concurrent.CountDownLatch;
public class MainProgram {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        final int[] port = new int[1];

        // Start the server in a separate thread
        Thread serverThread = new Thread(() -> {// Use 0 to assign a random available port
            latch.countDown();
            String serverName = "localhost:8080";
            System.out.println("Server started at " + serverName);
        });
        serverThread.start();

        try {
            // Wait for the server to start and get the port
            latch.await();

            // Start the client
            ChessClient client = new ChessClient("localhost:8080");
            System.out.println("👑 Welcome to 240 chess. Type Help to get started. 👑");
            System.out.println("   Connected to localhost:8080");
            Scanner scanner = new Scanner(System.in);


            var result = "";
            while (!result.equals("quit")) {
                client.printPrompt();
                String input = scanner.nextLine();
                try {
                    if (result.equals("try all")) {
                        String[] test = {"test"};
                        try {
                            client.move(test);
                            client.redraw(test);
                            client.list(test);
                            client.legal(test);
                            client.leave(test);
                            client.observe(test);
                            client.quit(test);
                            client.resign(test);
                            client.join(test);
                            client.logout(test);
                            client.login(test);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    result = client.eval(input);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the server");
        }
    }
}