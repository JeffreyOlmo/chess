package ui.facade;

import chess.ChessGame;
import chess.ChessGameDeserializer;
import chess.ChessGameSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.stream.Collectors;


public class ServerFacade {

    private final String serverUrl;
    private final Gson defaultGson = new Gson();
    private final Gson customGson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameSerializer())
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();


    public ServerFacade(String serverName) {
        serverUrl = String.format("http://%s", serverName);
    }

    public ServerFacade(int port) {
        serverUrl = String.format("http://localhost:%d", port);
    }

    public void clear() throws ResponseException {
        var r = this.makeRequest("DELETE", "/db", null, null, Map.class, false);
    }

    public AuthData register(String username, String password, String email) throws ResponseException {
        var request = Map.of("username", username, "password", password, "email", email);
        return this.makeRequest("POST", "/user", request, null, AuthData.class, false);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var userData = new UserData(username, password, ""); // Provide an empty string for the email field
        return this.makeRequest("POST", "/session", userData, null, AuthData.class, false);
    }

    public void logout(String authToken) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, authToken, null, false);
    }

    public GameData createGame(String authToken, String gameName) throws ResponseException {
        var request = Map.of("gameName", gameName);
        return this.makeRequest("POST", "/game", request, authToken, GameData.class, true);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        String responseBody = this.makeRequest("GET", "/game", null, authToken);
        try {
            Response response = customGson.fromJson(responseBody, Response.class);
            return (response != null && response.getGames() != null) ? response.getGames() : new GameData[0];
        } catch (Exception e) {
            System.err.println("Error during JSON parsing: " + e.getMessage());
            return new GameData[0];
        }
        }


    public GameData joinGame(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        var request = new JoinRequestData(color, gameID);
        this.makeRequest("PUT", "/game", request, authToken, GameData.class, true);
        return getGame(authToken, gameID);
    }

    private GameData getGame(String authToken, int gameID) throws ResponseException {
        var games = listGames(authToken);
        for (var game : games) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        throw new ResponseException(404, "Missing game");
    }

    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> clazz, boolean useCustomGson) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }

            Gson localGson = useCustomGson ? customGson : defaultGson; // Decide which Gson to use

            if (request != null) {
                http.addRequestProperty("Accept", "application/json");
                String reqData = localGson.toJson(request);
                try (OutputStream reqBody = http.getOutputStream()) {
                    reqBody.write(reqData.getBytes());
                }
            }

            http.connect();

            int responseCode = http.getResponseCode();

            if (responseCode == 200) {
                if (clazz != null) {
                    try (InputStream respBody = http.getInputStream()) {
                        InputStreamReader reader = new InputStreamReader(respBody);
                        return localGson.fromJson(reader, clazz);
                    }
                } else {
                    return null;
                }
            } else {
                try (InputStream errorStream = http.getErrorStream()) {
                }
                throw new ResponseException(responseCode, "Server returned non-OK status");
            }
        } catch (Exception ex){
          return null;
        }
    }



    private String makeRequest(String method, String path, Object request, String authToken) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            // Add request body if needed here

            http.connect();
            int responseCode = http.getResponseCode();

            if (responseCode == 200) {
                InputStream respBody = http.getInputStream();
                String responseBody = new BufferedReader(new InputStreamReader(respBody)).lines().collect(Collectors.joining("\n"));
                respBody.close();
                return responseBody;
            } else {
                throw new ResponseException(responseCode, "Server returned non-OK status");
            }
        } catch (Exception ex) {
            throw new ResponseException(500, "Failed to make request: " + ex.getMessage());
        }
    }


}

