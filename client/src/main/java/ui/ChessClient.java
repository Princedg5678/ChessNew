package ui;

import model.AuthData;
import model.RegisterUser;
import model.UserData;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {

    //private final ServerFacade server;
    private final String serverURL;
    private final State currentState = State.SIGNEDOUT;
    private final ServerFacade server;
    private String authToken;


    public ChessClient(String serverUrl){
        this.serverURL = serverUrl;
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout(params);
                case "create" -> create(params);
                case "list" -> list(params);
                case "play" -> play(params);
                case "observe" -> observe(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String help(){
        if (currentState == State.SIGNEDOUT){
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - help
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - list 
                - play <gameNumber> <color>
                - observe <gameNumber>
                - help
                - logout
                """;
    }

    public String register(String... params) throws ResponseException{
        if (params.length < 3){
            throw new ResponseException("Error: Expected <username> <password> <email>");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        RegisterUser newUser = new RegisterUser(username, password, email);
        UserData registeredUser = server.registerUser(newUser);
        authToken = registeredUser.authToken();

        return null;
    }

    public String login(String... params) throws ResponseException{


        return null;
    }

    public String logout(String... params) throws ResponseException{


        return null;
    }

    public String create(String... params) throws ResponseException{


        return null;
    }

    public String list(String... params) throws ResponseException{


        return null;
    }

    public String play(String... params) throws ResponseException{


        return null;
    }

    public String observe(String... params) throws ResponseException{


        return null;
    }


}
