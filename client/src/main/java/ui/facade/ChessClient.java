package ui.facade;

import chess.*;
import model.GameData;
import websocketmessages.usercommands.*;
import ui.websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;


public class ChessClient implements DisplayHandler {

    private State userState;
    private String authToken;
    private GameData gameData;
    private GameData[] games;
    final private ServerFacade server;
    final private WebSocketFacade webSocket;


    public ChessClient(String hostname) throws Exception {
        server = new ServerFacade(hostname);
        webSocket = new WebSocketFacade(hostname, this);

    }
    public boolean isObserving() {
        return (gameData != null && (userState == State.OBSERVING));
    }
    public boolean isGameOver() {
        return (gameData != null && gameData.isGameOver());
    }
    public boolean isTurn() {
        return (isPlaying() && userState.isTurn(gameData.getGame().getTeamTurn()));
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

    public String clear(String[] ignored) throws Exception {
        clear();
        userState = State.LOGGED_OUT;
        gameData = null;
        return "Cleared the world";
    }

    private String help(String[] ignored) {
        return switch (userState) {
            case LOGGED_IN -> getHelp(LOGGED_IN_HELP_ENTRIES);
            case OBSERVING -> getHelp(OBSERVING_HELP_ENTRIES);
            case BLACK, WHITE -> getHelp(PLAYING_HELP_ENTRIES);
            default -> getHelp(LOGGED_OUT_HELP_ENTRIES);
        };
    }

    public String quit(String[] ignored) {
        return "quit";
    }


    public String login(String[] params) throws ResponseException {
        if (userState == State.LOGGED_OUT && params.length == 2) {
            var response = server.login(params[0], params[1]);
            authToken = response.getAuthToken();
            userState = State.LOGGED_IN;
            return String.format("Logged in as %s", params[0]);
        }
        return "Failure";
    }

    public String register(String[] params) throws ResponseException {
        if (userState == State.LOGGED_OUT && params.length == 3) {
            var response = server.register(params[0], params[1], params[2]);
            authToken = response.getAuthToken();
            userState = State.LOGGED_IN;
            return String.format("Logged in as %s", params[0]);
        }
        return "Failure";
    }

    public String logout(String[] ignore) throws ResponseException {
        verifyAuth();

        if (userState != State.LOGGED_OUT) {
            server.logout(authToken);
            userState = State.LOGGED_OUT;
            authToken = null;
            return "Logged out";
        }
        return "Failure";
    }

    public String create(String[] params) throws ResponseException {
        verifyAuth();

        if (params.length == 1 && userState == State.LOGGED_IN) {
            var gameData = server.createGame(authToken, params[0]);
            return String.format("Create %d", gameData.getGameID());
        }
        return "Failure";
    }

    public String list(String[] ignore) throws ResponseException {
        verifyAuth();
        games = server.listGames(authToken);
        StringBuilder buf = new StringBuilder();
        for (var i = 0; i < games.length; i++) {
            var game = games[i];
            var gameText = String.format("%d. %s white:%s black:%s state: %s%n", i, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername(), game.getState());
            buf.append(gameText);
        }
        return buf.toString();
    }


    public String join(String[] params) throws Exception {
        verifyAuth();
        if (userState == State.LOGGED_IN) {
            if (params.length == 2 && ("WHITE".equalsIgnoreCase(params[1]) || "BLACK".equalsIgnoreCase(params[1]))) {
                int selectedGameIndex = Integer.parseInt(params[0]);
                if (games != null && selectedGameIndex >= 0 && selectedGameIndex < games.length) {
                    int selectedGameID = games[selectedGameIndex].getGameID();
                    ChessGame.TeamColor selectedColor = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
                    gameData = server.joinGame(authToken, selectedGameID, selectedColor);
                    userState = (selectedColor == ChessGame.TeamColor.WHITE ? State.WHITE : State.BLACK);
                    webSocket.sendCommand(new JoinPlayerCommand(authToken, selectedGameID, selectedColor));
                    return String.format("Joined %d as %s", gameData.getGameID(), selectedColor);
                }
            }
        }

        return "Failure";
    }


    public String observe(String[] params) throws Exception {
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

    public String redraw(String[] ignored) throws Exception {
        verifyAuth();
        if (isPlaying() || isObserving()) {
            printGame();
            return "";
        }
        return "Failure";
    }

    public String legal(String[] params) throws Exception {
        verifyAuth();
        if (isPlaying() || isObserving()) {
            if (params.length == 1) {
                ChessPosition selectedPosition = new ChessPosition(params[0]);
                ArrayList<ChessPosition> highlightedPositions = new ArrayList<>();
                highlightedPositions.add(selectedPosition);
                gameData.getGame().validMoves(selectedPosition).forEach(move -> highlightedPositions.add(move.getEndPosition()));
                ChessGame.TeamColor currentColor = (userState == State.BLACK) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
                printGame(currentColor, highlightedPositions);
                return "";
            }
        }
        return "Failure";
    }

    public String move(String[] params) throws Exception {
        verifyAuth();
        if (params.length == 1) {
            var move = new ChessMove(params[0]);
            if (isMoveLegal(move)) {
                webSocket.sendCommand(new MoveCommand(authToken, gameData.getGameID(), move));
                return "Success";
            }
        }
        return "Failure";
    }

    public String leave(String[] ignored) throws Exception {
        if (isPlaying() || isObserving()) {
            userState = State.LOGGED_IN;
            webSocket.sendCommand(new GameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameData.getGameID()));
            gameData = null;
            return "Left game";
        }
        return "Failure";
    }

    public String resign(String[] ignored) throws Exception {
        if (isPlaying()) {
            webSocket.sendCommand(new GameCommand(GameCommand.CommandType.RESIGN, authToken, gameData.getGameID()));
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
        String gameState = "Not playing";
        if (gameData != null) {
            gameState = switch (gameData.getState()) {
                case UNDECIDED -> String.format("%s's turn", gameData.getGame().getTeamTurn());
                case DRAW -> "Draw";
                case BLACK -> "Black won";
                case WHITE -> "White Won";
            };
        }
        System.out.print(RESET_TEXT_COLOR + String.format("\n[%s: %s] >>> ", userState, gameState) + SET_TEXT_COLOR_GREEN);
    }

    public boolean isMoveLegal(ChessMove move) {
        if (isTurn()) {
            var board = gameData.getGame().getBoard();
            var piece = board.getPiece(move.getStartPosition());
            if (piece != null) {
                var validMoves = piece.pieceMoves(board, move.getStartPosition());
                if (validMoves.contains(move)) {
                    return gameData.getGame().validMoves(move.getStartPosition()).contains(move);
                }
            }
        }
        return false;
    }

    public boolean isPlaying() {
        return (gameData != null && (userState == State.WHITE || userState == State.BLACK) && !isGameOver());
    }



    private void verifyAuth() throws ResponseException {
        if (authToken == null) {
            throw new ResponseException(401, "Please login or register");
        }
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

    static final List<HelpEntry> LOGGED_OUT_HELP_ENTRIES = Arrays.asList(
            new HelpEntry("register <USERNAME> <PASSWORD> <EMAIL>", "create a new user account"),
            new HelpEntry("login <USERNAME> <PASSWORD>", "authenticate and start playing"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> LOGGED_IN_HELP_ENTRIES = Arrays.asList(
            new HelpEntry("create <NAME>", "initiate a new game session"),
            new HelpEntry("list", "view a list of available games"),
            new HelpEntry("join <ID> [WHITE|BLACK]", "participate in an existing game"),
            new HelpEntry("observe <ID>", "watch an ongoing game"),
            new HelpEntry("logout", "end the current session"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> OBSERVING_HELP_ENTRIES = Arrays.asList(
            new HelpEntry("legal", "show legal moves for the current board position"),
            new HelpEntry("redraw", "refresh the board display"),
            new HelpEntry("leave", "stop observing the game"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    static final List<HelpEntry> PLAYING_HELP_ENTRIES = Arrays.asList(
            new HelpEntry("redraw", "refresh the board display"),
            new HelpEntry("leave", "forfeit and exit the game"),
            new HelpEntry("move <crcr> [q|r|b|n]", "make a move and optionally promote a pawn"),
            new HelpEntry("resign", "forfeit the game without exiting"),
            new HelpEntry("legal <cr>", "show legal moves for a specific piece"),
            new HelpEntry("quit", "exit the application"),
            new HelpEntry("help", "display list of available commands")
    );

    private String getHelp(List<HelpEntry> help) {
        StringBuilder sb = new StringBuilder();
        for (var me : help) {
            sb.append(String.format("  %s%s%s - %s%s%s%n", SET_TEXT_COLOR_BLUE, me.command, RESET_TEXT_COLOR, SET_TEXT_COLOR_MAGENTA, me.description, RESET_TEXT_COLOR));
        }
        return sb.toString();
    }

    @Override
    public void updateBoard(GameData newGameData) {
        gameData = newGameData;
        printGame();
        printPrompt();

        if (isGameOver()) {
            userState = State.LOGGED_IN;
            printPrompt();
            gameData = null;
        }
    }

    @Override
    public void message(String message) {
        System.out.println();
        System.out.println(SET_TEXT_COLOR_MAGENTA + "NOTIFY: " + message);
        printPrompt();
    }

    @Override
    public void error(String message) {
        System.out.println();
        System.out.println(SET_TEXT_COLOR_RED + "NOTIFY: " + message);
        printPrompt();

    }

}
