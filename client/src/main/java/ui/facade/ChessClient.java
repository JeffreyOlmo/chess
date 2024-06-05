package facade;

import chess.*;
import model.GameData;
import webSocketMessages.userCommands.*;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;



public class ChessClient implements DisplayHandler {

    private State userState;
    private String authToken;
    private GameData gameData;
    private GameData[] games;
    final private ServerFacade server;
    final private WebSocketFacade webSocket;


    public ChessClient(String hostname) throws Exception {

    }

    public String eval(String input) {
        String output = "Error with command. Try: Help";
        try {
            String lowerCaseInput = input.toLowerCase();
            String[] inputTokens = lowerCaseInput.split(" ");
            if (inputTokens.length == 0) {
                inputTokens = new String[]{"Help"};
            }

            String[] methodParams = Arrays.copyOfRange(inputTokens, 1, inputTokens.length);
            try {
                output = (String) getClass().getDeclaredMethod(inputTokens[0], String[].class).invoke(this, (Object) methodParams);
            } catch (NoSuchMethodException e) {
                output = String.format("Unknown command\n%s", help(methodParams));
            }
        } catch (Throwable e) {}
        return output;
    }

    public void clear() throws Exception {
        server.clear();
    }

    private String clear(String[] ignored) throws Exception {
        clear();
        userState = State.LOGGED_OUT;
        gameData = null;
        return "Cleared the world";
    }

    private String help(String[] ignored) {

    }

    private String quit(String[] ignored) {
        return "quit";
    }


    private String login(String[] params) throws ResponseException {
        if (userState == State.LOGGED_OUT && params.length == 2) {
            var response = server.login(params[0], params[1]);
            authToken = response.getAuthToken();
            userState = State.LOGGED_IN;
            return String.format("Logged in as %s", params[0]);
        }
        return "Failure";
    }

    private String register(String[] params) throws ResponseException {
        if (userState == State.LOGGED_OUT && params.length == 3) {
            var response = server.register(params[0], params[1], params[2]);
            authToken = response.getAuthToken();
            userState = State.LOGGED_IN;
            return String.format("Logged in as %s", params[0]);
        }
        return "Failure";
    }

    private String logout(String[] ignore) throws ResponseException {
        verifyAuth();

        if (userState != State.LOGGED_OUT) {
            server.logout(authToken);
            userState = State.LOGGED_OUT;
            authToken = null;
            return "Logged out";
        }
        return "Failure";
    }

    private String create(String[] params) throws ResponseException {
        verifyAuth();

        if (params.length == 1 && userState == State.LOGGED_IN) {
            var gameData = server.createGame(authToken, params[0]);
            return String.format("Create %d", gameData.getGameID());
        }
        return "Failure";
    }

    private String list(String[] ignore) throws ResponseException {
        verifyAuth();
        games = server.listGames(authToken);
        StringBuilder buf = new StringBuilder();
        for (var i = 0; i < games.length; i++) {
            var game = games[i];
            var gameText = String.format("%d. %s white:%s black:%s state: %s%n", i, game.gameName(), game.whiteUsername(), game.blackUsername(), game.state());
            buf.append(gameText);
        }
        return buf.toString();
    }


    private String join(String[] params) throws Exception {
        verifyAuth();
        if (userState == State.LOGGED_IN) {
            if (params.length == 2 && ("WHITE".equalsIgnoreCase(params[1]) || "BLACK".equalsIgnoreCase(params[1]))) {
                int selectedGameIndex = Integer.parseInt(params[0]);
                if (games != null && selectedGameIndex >= 0 && selectedGameIndex < games.length) {
                    int selectedGameID = games[selectedGameIndex].gameID();
                    ChessGame.TeamColor selectedColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
                    gameData = server.joinGame(authToken, selectedGameID, selectedColor);
                    userState = (selectedColor == ChessGame.TeamColor.WHITE ? State.WHITE : State.BLACK);
                    webSocket.sendCommand(new JoinPlayerCommand(authToken, selectedGameID, selectedColor));
                    return String.format("Joined %d as %s", gameData.gameID(), selectedColor);
                }
            }
        }

        return "Failure";
    }


    private String observe(String[] params) throws Exception {
        verifyAuth();
        if (State.LOGGED_IN == userState) {
            if (1 == params.length) {
                int observedGameID = Integer.parseInt(params[0]);
                gameData = server.joinGame(authToken, observedGameID, null);
                userState = State.OBSERVING;
                UserGameCommand.CommandType commandType = UserGameCommand.CommandType.JOIN_OBSERVER;
                webSocket.sendCommand(new GameCommand(commandType, authToken, observedGameID));
                return String.format("Joined %d as observer", gameData.getGameID());
            }
        }

        return "Failure";
    }

    private String redraw(String[] ignored) throws Exception {
        verifyAuth();
        if (isPlaying() || isObserving()) {
            printGame();
            return "";
        }
        return "Failure";
    }

    private String legal(String[] params) throws Exception {
        verifyAuth();
        if (isPlaying() || isObserving()) {
            if (params.length == 1) {
                ChessPosition selectedPosition = new ChessPosition(params[0]);
                ArrayList<ChessPosition> highlightedPositions = new ArrayList<>();
                highlightedPositions.add(selectedPosition);
                gameData.game().validMoves(selectedPosition).forEach(move -> highlightedPositions.add(move.getEndPosition()));
                ChessGame.TeamColor currentColor = (userState == State.BLACK) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                printGame(currentColor, highlightedPositions);
                return "";
            }
        }
        return "Failure";
    }

    private String move(String[] params) throws Exception {
        verifyAuth();
        if (params.length == 1) {
            var move = new ChessMove(params[0]);
            if (isMoveLegal(move)) {
                webSocket.sendCommand(new MoveCommand(authToken, gameData.gameID(), move));
                return "Success";
            }
        }
        return "Failure";
    }

    private String leave(String[] ignored) throws Exception {
        if (isPlaying() || isObserving()) {
            userState = State.LOGGED_IN;
            webSocket.sendCommand(new GameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameData.gameID()));
            gameData = null;
            return "Left game";
        }
        return "Failure";
    }

    private String resign(String[] ignored) throws Exception {
        if (isPlaying()) {
            webSocket.sendCommand(new GameCommand(GameCommand.CommandType.RESIGN, authToken, gameData.gameID()));
            userState = State.LOGGED_IN;
            gameData = null;
            return "Resigned";
        }
        return "Failure";
    }

    private void printGame() {
        var color = userState == State.BLACK ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        printGame(color, null);
    }

    private void printGame(ChessGame.TeamColor color, Collection<ChessPosition> highlights) {
        System.out.println("\n");
        System.out.print((gameData.getGame().getBoard()).toString(color, highlights));
        System.out.println();
    }

    public void printPrompt() {

    }

    public boolean isMoveLegal(ChessMove move) {

    }

    public boolean isPlaying() {

    }

    private String getHelp(List<Help> help) {

    }

    private void verifyAuth() throws ResponseException {

    }


    /**
     * All of all the possible client commands.
     */
    private static class HelpEntry {
        String command;
        String description;

        public HelpEntry(String cmd, String desc) {
            this.command = cmd;
            this.description = desc;
        }
    }

    static final List<HelpEntry> loggedOutHelpEntries = Arrays.asList(
            new HelpEntry("register <USERNAME> <PASSWORD> <EMAIL>", "create a new user account"),
            new HelpEntry("login <USERNAME> <PASSWORD>", "authenticate and start playing"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> loggedInHelpEntries = Arrays.asList(
            new HelpEntry("create <NAME>", "initiate a new game session"),
            new HelpEntry("list", "view a list of available games"),
            new HelpEntry("join <ID> [WHITE|BLACK]", "participate in an existing game"),
            new HelpEntry("observe <ID>", "watch an ongoing game"),
            new HelpEntry("logout", "end the current session"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> observingHelpEntries = Arrays.asList(
            new HelpEntry("legal", "show legal moves for the current board position"),
            new HelpEntry("redraw", "refresh the board display"),
            new HelpEntry("leave", "stop observing the game"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> playingHelpEntries = Arrays.asList(
            new HelpEntry("redraw", "refresh the board display"),
            new HelpEntry("leave", "forfeit and exit the game"),
            new HelpEntry("move <crcr> [q|r|b|n]", "make a move and optionally promote a pawn"),
            new HelpEntry("resign", "forfeit the game without exiting"),
            new HelpEntry("legal <cr>", "show legal moves for a specific piece"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

}
