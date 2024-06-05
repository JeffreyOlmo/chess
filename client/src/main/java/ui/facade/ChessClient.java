package facade;

import chess.*;
import model.GameData;
import webSocketMessages.userCommands.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;



public class ChessClient {

    private State userState;
    private String authToken;
    private GameData gameData;
    private GameData[] games;
    final private ServerFacade server;
    final private WebSocketFacade webSocket;


    public ChessClient(String hostname) throws Exception {

    }

    public String eval(String input) {

    }

    public void clear() throws Exception {

    }

    private String clear(String[] ignored) throws Exception {

    }

    private String help(String[] ignored) {

    }

    private String quit(String[] ignored) {
        return "quit";
    }


    private String login(String[] params) throws ResponseException {

    }

    private String register(String[] params) throws ResponseException {

    }

    private String logout(String[] ignore) throws ResponseException {

    }

    private String create(String[] params) throws ResponseException {

    }

    private String list(String[] ignore) throws ResponseException {

    }


    private String join(String[] params) throws Exception {

    }


    private String observe(String[] params) throws Exception {

    }

    private String redraw(String[] ignored) throws Exception {

    }

    private String legal(String[] params) throws Exception {

    }

    private String move(String[] params) throws Exception {

    }

    private String leave(String[] ignored) throws Exception {

    }

    private String resign(String[] ignored) throws Exception {

    }

    private void printGame() {

    }

    private void printGame(ChessGame.TeamColor color, Collection<ChessPosition> highlights) {

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
