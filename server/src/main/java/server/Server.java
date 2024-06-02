package server;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MySqlDataAccess;
import model.*;
import service.*;
import spark.*;
import util.CodedException;

import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;


public class Server {
    DataAccess dataAccess;
    UserService userService;
    GameService gameService;
    AdminService adminService;
    AuthService authService;
    public static final Logger LOG = Logger.getLogger("chess");
    public Server() {
    }
    public int run(int desiredPort) {
        try {
            initializeServices();
            configureSpark(desiredPort);
        } catch (Exception ex) {
            System.out.printf("Unable to start server: %s", ex.getMessage());
            System.exit(1);
        }
        Spark.awaitInitialization();
        return Spark.port();
    }

    private void initializeServices() throws Exception {
        dataAccess = new MySqlDataAccess();
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        adminService = new AdminService(dataAccess);
        authService = new AuthService(dataAccess);
    }

    private void configureSpark(int desiredPort) {
        Spark.port(desiredPort);

        var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        Spark.externalStaticFileLocation(webDir.toString());
        Spark.delete("/db", this::clearApplication);
        Spark.post("/user", this::registerUser);
        Spark.delete("/session", this::deleteSession);
        Spark.post("/session", this::login);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.afterAfter(this::log);

        Spark.get("/", (req, res) -> {
            return "CS 240 Chess Server Web API";
        });

        Spark.exception(CodedException.class, this::errorHandler);
        Spark.exception(Exception.class, (e, req, res) -> errorHandler(new CodedException(500, e.getMessage()), req, res));
        Spark.notFound((req, res) -> {
            var msg = String.format("[%s] %s not found", req.requestMethod(), req.pathInfo());
            return errorHandler(new CodedException(404, msg), req, res);
        });
    }

    public void stop() {
        Spark.stop();
    }
    public Object errorHandler(CodedException e, Request req, Response res) {
        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));
        res.type("application/json");
        res.status(e.statusCode());
        res.body(body);
        return body;
    }
    private void log(Request req, Response res) {
        LOG.info(String.format("[%s] %s - %s", req.requestMethod(), req.pathInfo(), res.status()));
    }
    /**
     * Endpoint for [DELETE] /db
     */
    public Object clearApplication(Request ignoreReq, Response res) throws CodedException {
        adminService.clearApplication();
        return send();
    }
    /**
     * Endpoint for [POST] /user - Register user
     * <pre>{ "username":"", "password":"", "email":"" }</pre>
     */
    private Object registerUser(Request req, Response ignore) throws CodedException {
        var user = getBody(req, UserData.class);
        var authToken = userService.registerUser(user);
        return send("username", user.getUsername(), "authToken", authToken.getAuthToken());
    }

    /**
     * Endpoint for [POST] /session
     * <pre>{ "username":"", "password":"" }</pre>
     */
    public Object login(Request req, Response ignore) throws CodedException {
        var user = getBody(req, UserData.class);
        var authData = authService.createSession(user);
        return send("username", user.getUsername(), "authToken", authData.getAuthToken());
    }
    /**
     * Endpoint for [DELETE] /session
     * Authorization header required.
     */
    public Object deleteSession(Request req, Response ignore) throws CodedException {
        var authData = throwIfUnauthorized(req);
        authService.deleteSession(authData.getAuthToken());
        return send();
    }

    /**
     * Endpoint for [GET] /game
     * Authorization header required.
     */
    public Object listGames(Request req, Response ignoreRes) throws CodedException {
        throwIfUnauthorized(req);
        var games = gameService.listGames();
        return send("games", games.toArray());
    }
    /**
     * Endpoint for [POST] / game
     * Authorization header required.
     */
    public Object createGame(Request req, Response ignoreRes) throws CodedException {
        throwIfUnauthorized(req);
        var gameData = getBody(req, GameData.class);
        gameData = gameService.createGame(gameData.getGameName());
        return send("gameID", gameData.getGameID());
    }
    /**
     * Endpoint for [PUT] /
     * Authorization header required.
     * <pre>{ "playerColor":"WHITE/BLACK/empty", "gameID": 1234 }</pre>
     */
    public Object joinGame(Request req, Response ignoreRes) throws CodedException {
        var authData = throwIfUnauthorized(req);
        var joinReq = getBody(req, JoinRequest.class);

        if (joinReq.getPlayerColor() == null) {
            throw new CodedException(400, "Invalid player color");
        }
        gameService.joinGame(authData.getUsername(), joinReq.getPlayerColor(), joinReq.getGameID());
        return send();
    }

    private <T> T getBody(Request request, Class<T> clazz) throws CodedException {
        var body = new Gson().fromJson(request.body(), clazz);
        if (body == null) {
            throw new CodedException(400, "Missing body");
        }
        return body;
    }
    private String send(Object... props) {
        Map<Object, Object> map = new HashMap<>();
        for (var i = 0; i + 1 < props.length; i = i + 2) {
            map.put(props[i], props[i + 1]);
        }
        return new Gson().toJson(map);
    }
    private AuthData throwIfUnauthorized(Request req) throws CodedException {
        var authToken = req.headers("authorization");
        if (authToken != null) {
            var authData = authService.getAuthData(authToken);
            if (authData != null) {
                return authData;
            }
        }
        throw new CodedException(401, "Not authorized");
    }
}