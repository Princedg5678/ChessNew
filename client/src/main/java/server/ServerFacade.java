package server;

import com.google.gson.Gson;

import model.RegisterUser;
import model.UserData;
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


    public UserData registerUser(RegisterUser newUser){


        return null;
    }






}
