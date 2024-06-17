package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.GameCommand;
import websocket.commands.JoinPlayerCommand;
import websocket.commands.MoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

@WebSocket
public class WebSocketHandler {

    private final DataAccess dataAccess;

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public static class Connection {
        public UserData user;
        public GameData game;
        public Session session;

        public Connection(UserData user, Session session) {
            this.user = user;
            this.session = session;
        }


        private void send(String msg) throws Exception {
            session.getRemote().sendString(msg);
        }

        private void sendError(String msg) throws Exception {
            sendError(session.getRemote(), msg);
        }

        private static void sendError(RemoteEndpoint endpoint, String msg) throws Exception {
            var errMsg = (new ErrorMessage(String.format("ERROR: %s", msg))).toString();
            endpoint.sendString(errMsg);
        }

    }

    public static class ConnectionManager {
        public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

        public void add(String username, Connection connection) {
            connections.put(username, connection);
        }

        public Connection get(String username) {
            return connections.get(username);
        }

        public void remove(Session session) {
            Connection removeConnection = null;
            for (var c : connections.values()) {
                if (c.session.equals(session)) {
                    removeConnection = c;
                    break;
                }
            }

            if (removeConnection != null) {
                connections.remove(removeConnection.user.getUsername());
            }
        }

        public void broadcast(int gameID, String excludeUsername, String msg) throws Exception {
            System.out.println("Broadcasting message to game ID: " + gameID);
            var removeList = new ArrayList<Connection>();
            for (var c : connections.values()) {
                if (c.session.isOpen()) {
                    if (c.game != null && c.game.getGameID() == gameID) {
                        System.out.println("Checking connection for user: " + c.user.getUsername() + ", gameID: " + c.game.getGameID() + ", c.game: " + c.game);
                        if (!StringUtil.isEqual(c.user.getUsername(), excludeUsername)) {
                            System.out.println("Sending message to user: " + c.user.getUsername());
                            c.send(msg);
                        }
                    }
                } else {
                    removeList.add(c);
                }
            }

            // Clean up any connections that were left open.
            for (var c : removeList) {
                connections.remove(c.user.getUsername());
            }
        }



        @Override
        public String toString() {
            var sb = new StringBuilder("[\n");
            for (var c : connections.values()) {
                sb.append(String.format("  {'game':%d, 'user': %s}%n", c.game.getGameID(), c.user));
            }
            sb.append("]");
            return sb.toString();
        }
    }

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        System.out.println("WebSocket connection established: " + session.getRemoteAddress());
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println("Received WebSocket message: " + message);
        try {
            var command = readJson(message, GameCommand.class);
            System.out.println("Parsed command: " + command);
            var connection = getConnection(command.getAuthString(), session);
            if (connection != null) {
                System.out.println("command type: " + command.getCommandType());
                switch (command.getCommandType()) {
                    case JOIN_PLAYER, CONNECT -> join(connection, readJson(message, JoinPlayerCommand.class));
                    case JOIN_OBSERVER -> observe(connection, readJson(message, JoinPlayerCommand.class));
                    case MAKE_MOVE -> move(connection, readJson(message, MoveCommand.class));
                    case LEAVE -> leave(connection, command);
                    case RESIGN -> resign(connection, command);
                }
            } else {
                System.out.println("Unknown user");
                Connection.sendError(session.getRemote(), "unknown user");
            }
        } catch (Exception e) {
            System.out.println("Error processing message: " + e.getMessage());
            Connection.sendError(session.getRemote(), util.ExceptionUtil.getRoot(e).getMessage());
        }
    }


    private void join(Connection connection, JoinPlayerCommand command) throws Exception {
        System.out.println("Join was called");
        var gameData = dataAccess.readGame(command.gameID);
        if (gameData != null) {
            System.out.println("Player color according to command: " + command.playerColor);
            var expectedUsername = (command.playerColor == ChessGame.TeamColor.BLACK) ? gameData.getBlackUsername() : gameData.getWhiteUsername();
            System.out.println("Expected username: " + expectedUsername);
            System.out.println("Connection username: " + connection.user.getUsername());

            connection.game = gameData;
            System.out.println("Joined game ID: " + gameData.getGameID());
            var loadMsg = (new LoadMessage(gameData)).toString();
            connection.send(loadMsg);

            var notificationMsg = (new NotificationMessage(String.format("%s joined %s as %s", connection.user.getUsername(), gameData.getGameName(), command.playerColor))).toString();
            connections.broadcast(gameData.getGameID(), connection.user.getUsername(), notificationMsg);

        } else {
            connection.sendError("unknown game");
        }
    }


    private void observe(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameID);
        if (gameData != null) {
            connection.game = gameData;
            var loadMsg = (new LoadMessage(gameData)).toString();
            connection.send(loadMsg);

            var notificationMsg = (new NotificationMessage(String.format("%s observing %s", connection.user.getUsername(), gameData.getGameName()))).toString();
            connections.broadcast(gameData.getGameID(), connection.user.getUsername(), notificationMsg);
        } else {
            connection.sendError("unknown game");
        }
    }

    private void move(Connection connection, MoveCommand command) throws Exception {
        System.out.println("Received move command: " + command.move);
        var gameData = dataAccess.readGame(command.gameID);
        if (gameData != null) {
            System.out.println("Game data found for game ID: " + command.gameID);
            if (!gameData.isGameOver()) {
                System.out.println("Game is not over, validating turn...");
                if (isTurn(gameData, command.move, connection.user.getUsername())) {
                    System.out.println("Valid turn, making move: " + command.move);
                    gameData.getGame().makeMove(command.move);
                    var notificationMsg = (new NotificationMessage(String.format("%s moved to %s%s",
                            connection.user.getUsername(),getColumnLetter(command.move.getStartPosition().getColumn()),
                            command.move.getEndPosition().getRow()))).toString();
                    System.out.println("Broadcasting move notification: " + notificationMsg);
                    connections.broadcast(gameData.getGameID(), connection.user.getUsername(), notificationMsg);

                    gameData = handleGameStateChange(gameData);
                    dataAccess.updateGame(gameData);
                    connection.game = gameData;

                    var loadMsg = (new LoadMessage(gameData)).toString();
                    System.out.println("Broadcasting load message: " + loadMsg);
                    connections.broadcast(gameData.getGameID(), "", loadMsg);
                } else {
                    System.out.println("Invalid move or not player's turn: " + command.move);
                    connection.sendError("invalid move: " + command.move);
                }
            } else {
                System.out.println("Game is over: " + gameData.getState());
                connection.sendError("game is over: " + gameData.getState());
            }
        } else {
            System.out.println("Unknown game ID: " + command.gameID);
            connection.sendError("unknown game");
        }
    }

    public String moveToString(ChessMove move, ChessPiece.PieceType pieceType) {
        String startColumn = getColumnLetter(move.getStartPosition().getColumn());
        String startRow = String.valueOf(move.getStartPosition().getRow());
        String endColumn = getColumnLetter(move.getEndPosition().getColumn());
        String endRow = String.valueOf(move.getEndPosition().getRow());

        String pieceName = getPieceName(pieceType);

        return String.format("%s from %s%s to %s%s", pieceName, startColumn, startRow, endColumn, endRow);
    }

    private String getColumnLetter(int column) {
        return String.valueOf((char) ('a' + column - 1));
    }

    private String getPieceName(ChessPiece.PieceType pieceType) {
        return switch (pieceType) {
            case PAWN -> "Pawn";
            case ROOK -> "Rook";
            case KNIGHT -> "Knight";
            case BISHOP -> "Bishop";
            case QUEEN -> "Queen";
            case KING -> "King";
        };
    }


    private void leave(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameID);
        if (gameData != null) {
            if (StringUtil.isEqual(gameData.getBlackUsername(), connection.user.getUsername())) {
                gameData = gameData.setBlack(null);
            } else if (StringUtil.isEqual(gameData.getWhiteUsername(), connection.user.getUsername())) {
                gameData = gameData.setWhite(null);
            }
            dataAccess.updateGame(gameData);
            connections.remove(connection.session);
            var notificationMsg = (new NotificationMessage(String.format("%s left", connection.user.getUsername()))).toString();
            connections.broadcast(gameData.getGameID(), "", notificationMsg);
        } else {
            connection.sendError("unknown game");
        }
    }

    private void resign(Connection connection, GameCommand command) throws Exception {
        var gameData = dataAccess.readGame(command.gameID);
        if (gameData != null && !gameData.isGameOver()) {
            var color = getPlayerColor(gameData, connection.user.getUsername());
            if (color != null) {
                var state = color == ChessGame.TeamColor.WHITE ? GameData.State.BLACK : GameData.State.WHITE;
                gameData = gameData.setState(state);
                dataAccess.updateGame(gameData);
                connection.game = gameData;

                var notificationMsg = (new NotificationMessage(String.format("%s resigned", connection.user.getUsername()))).toString();
                connections.broadcast(gameData.getGameID(), "", notificationMsg);
            } else {
                connection.sendError("only players can resign");
            }
        } else {
            connection.sendError("unknown game");
        }
    }

    private boolean isTurn(GameData gameData, ChessMove move, String username) {
        var piece = gameData.getGame().getBoard().getPiece(move.getStartPosition());
        var turn = gameData.getGame().getTeamTurn();
        var turnUsername = turn.equals(WHITE) ? gameData.getWhiteUsername() : gameData.getBlackUsername();
        System.out.println("turn username: " + turnUsername);
        System.out.println("Peice: " + piece);
        System.out.println("Turn: " + turn);
        System.out.println("Color of the Piece" + piece.getTeamColor());
        return (turnUsername.equals(username) && piece != null && piece.getTeamColor().equals(turn));
    }

    private ChessGame.TeamColor getPlayerColor(GameData gameData, String username) {
        if (StringUtil.isEqual(gameData.getBlackUsername(), username)) {
            return ChessGame.TeamColor.BLACK;
        } else if (StringUtil.isEqual(gameData.getWhiteUsername(), username)) {
            return WHITE;
        }
        return null;
    }

    private GameData handleGameStateChange(GameData gameData) throws Exception {
        NotificationMessage notificationMsg = null;
        var game = gameData.getGame();
        if (game.isInStalemate(WHITE) || game.isInStalemate(BLACK)) {
            gameData = gameData.setState(GameData.State.DRAW);
            notificationMsg = new NotificationMessage("game is a draw");
        } else if (game.isInCheckmate(WHITE)) {
            gameData = gameData.setState(GameData.State.BLACK);
            notificationMsg = new NotificationMessage(String.format("Black player, %s, wins!", gameData.getBlackUsername()));
        } else if (game.isInCheckmate(BLACK)) {
            gameData = gameData.setState(GameData.State.WHITE);
            notificationMsg = new NotificationMessage(String.format("White player, %s, wins!", gameData.getWhiteUsername()));
        } else if (game.isInCheck(WHITE)) {
            notificationMsg = new NotificationMessage(String.format("White player, %s, is in check!", gameData.getWhiteUsername()));
        } else if (game.isInCheck(BLACK)) {
            notificationMsg = new NotificationMessage(String.format("Black player, %s, is in check!", gameData.getBlackUsername()));
        }

        if (notificationMsg != null) {
            connections.broadcast(gameData.getGameID(), "", notificationMsg.toString());
        }
        return gameData;
    }


    private static <T> T readJson(String json, Class<T> clazz) throws IOException {
        var gson = new Gson();
        var obj = gson.fromJson(json, clazz);
        if (obj == null) {
            throw new IOException("Invalid JSON");
        }
        return obj;
    }


    private Connection getConnection(String id, Session session) throws Exception {
        Connection connection = null;
        var authData = isAuthorized(id);
        if (authData != null) {
            connection = connections.get(authData.getUsername());
            if (connection == null) {
                var user = dataAccess.readUser(authData.getUsername());
                connection = new Connection(user, session);
                connections.add(authData.getUsername(), connection);
            }
        }
        return connection;
    }


    public AuthData isAuthorized(String token) throws DataAccessException {
        if (token != null) {
            return dataAccess.readAuth(token);
        }
        return null;
    }

}

