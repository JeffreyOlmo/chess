package facade;

import chess.ChessGame;
import com.google.gson.Gson;
import requests.*;
import responses.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private final String baseUrl;

    public ServerFacade(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private <T> T sendRequest(String method, String endpoint, String requestBody, String authToken, Class<T> responseClass) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);

        if (authToken != null) {
            connection.setRequestProperty("Authorization", authToken);
        }

        if (requestBody != null) {
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.getBytes());
            }
        }

        int responseCode = connection.getResponseCode();
        InputStream responseBodyStream = null;

        if (responseCode == HttpURLConnection.HTTP_OK) {
            responseBodyStream = connection.getInputStream();
        } else {
            responseBodyStream = connection.getErrorStream();
        }

        InputStreamReader responseBodyReader = new InputStreamReader(responseBodyStream);
        T responseObject = new Gson().fromJson(responseBodyReader, responseClass);

        responseBodyReader.close();
        connection.disconnect();

        return responseObject;
    }

    public AuthResponse registerUser(RegisterRequest request) throws IOException {

    }

    public AuthResponse loginUser(LoginRequest request) throws IOException {

    }

    public BaseResponse logoutUser(BaseRequest request, String authToken) throws IOException {

    }

    public CreateGameResponse createGame(CreateGameRequest request, String authToken) throws IOException {

    }

    public BaseResponse joinGame(JoinGameRequest request, String authToken) throws IOException {

    }

    public ListGamesResponse listGames(BaseRequest request, String authToken) throws IOException {

    }

    public BaseResponse clearDatabase(BaseRequest request) throws IOException {

    }

    public AccessGameResponse getChessGame(AccessGameRequest request, String authToken) throws IOException {

    }

}