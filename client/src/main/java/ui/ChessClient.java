package ui;

import model.*;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {

    private final String serverURL;
    private State currentState = State.SIGNEDOUT;
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
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
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
        if (currentState == State.SIGNEDIN){
            throw new ResponseException("Error: Already logged in");
        }

        if (params.length < 3){
            throw new ResponseException("Error: Expected <username> <password> <email>");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];

        RegisterUser newUser = new RegisterUser(username, password, email);
        UserData registeredUser = server.registerUser(newUser);
        authToken = registeredUser.authToken();

        currentState = State.SIGNEDIN;
        System.out.println("You have successfully registered as " + username + ".");
        return help();
    }

    public String login(String... params) throws ResponseException{
        if (currentState == State.SIGNEDIN){
            throw new ResponseException("Error: Already logged in");
        }

        if (params.length < 2){
            throw new ResponseException("Error: Expected <username> <password>");
        }

        String username = params[0];
        String password = params[1];

        LoginUser returningUser = new LoginUser(username, password);
        UserData loggedInUser = server.loginUser(returningUser);
        authToken = loggedInUser.authToken();

        currentState = State.SIGNEDIN;
        System.out.println("Login Successful. Welcome back " + username + ".");
        return help();
    }

    public String logout() throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        server.logoutUser(authToken);

        currentState = State.SIGNEDOUT;
        System.out.println("Logout Successful. See you next time!");
        return help();
    }

    public String create(String... params) throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        if (params.length < 1){
            throw new ResponseException("Error: Expected <gameName>");
        }

        String tempName = params[0];
        GameName gameName = new GameName(tempName);
        GameID gameID = server.createGame(gameName, authToken);
        //Figure out what to do with this

        return "Game successfully created";
    }

    public String list() throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        GameList gameList = server.listGames(authToken);
        StringBuilder returnString = new StringBuilder();
        int i = 1;

        for (GameResult game: gameList.games()){
            returnString.append(i).append(". \n");
            returnString.append("Game Name: ").append(game.gameName()).append("\n");
            returnString.append("White username: ").append(game.whiteUsername()).append("\n");
            returnString.append("Black username: ").append(game.blackUsername()).append("\n");
            returnString.append("\n");
            i++;
        }

        return returnString.toString();
    }

    public String play(String... params) throws ResponseException{


        return null;
    }

    public String observe(String... params) throws ResponseException{


        return null;
    }


}
