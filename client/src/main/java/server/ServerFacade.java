package server;

import com.google.gson.Gson;

import model.*;
import ui.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url){

        this.serverUrl = url;

    }


    public UserData registerUser(RegisterUser newUser) throws ResponseException {

        return this.makeRequest("POST","/user", newUser, UserData.class, null);
    }

    public UserData loginUser(LoginUser returningUser) throws ResponseException {

        return this.makeRequest("POST","/session", returningUser, UserData.class, null);
    }

    public void logoutUser(String authToken) throws ResponseException {
        this.makeRequest("DELETE", "/session", null, null, authToken);
    }

    public GameID createGame(GameName gameName, String authToken) throws ResponseException {
        return this.makeRequest("POST", "/game", gameName, GameID.class, authToken);
    }

    public GameList listGames(String authToken) throws ResponseException {
        return this.makeRequest("GET", "/game", null, GameList.class, authToken);
    }

    public void playGame(JoinRequest joinRequest, String authToken) throws ResponseException {
        this.makeRequest("PUT", "/game", joinRequest, null, authToken);
    }


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null){
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)){
            if (status == 401){
                throw new ResponseException("Error: unauthorized");
            }
            else if (status == 400){
                throw new ResponseException("Error: bad request");
            }
            else if (status == 403){
                throw new ResponseException("Error: already taken");
            }
            else {
                throw new ResponseException("Error: something went wrong");
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null){
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }

        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }




}
