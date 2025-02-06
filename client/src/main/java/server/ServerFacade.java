package server;

import com.google.gson.Gson;

import ui.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url){

        this.serverUrl = url;

    }

//    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
//
//    }
//
//
//    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
//
//    }
//
//    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
//
//    }
//
//    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
//
//    }
//
//
//    private boolean isSuccessful(int status) {
//        return status / 100 == 2;
//    }

}
