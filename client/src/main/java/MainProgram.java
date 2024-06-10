import ui.facade.ChessClient;
import ui.facade.ResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class MainProgram {
    public static void main(String[] args) {
        try {
            var serverName = args.length > 0 ? args[0] : "localhost:8080";

            ChessClient client = new ChessClient(serverName);
            System.out.println("👑 Welcome to 240 chess. Type Help to get started. 👑");
            System.out.println("   Connected to " + serverName);
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
                } catch (Exception e) {throw new RuntimeException(e);}
                try {
                    result = client.eval(input);
                    System.out.print(RESET_TEXT_COLOR + result);
                } catch (Throwable e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to connect to the server");
        }
    }
}
