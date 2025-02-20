package ui;

import chess.ChessGame;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChessClient {

    private final String serverURL;
    private State currentState = State.SIGNEDOUT;
    private final ServerFacade server;
    private String authToken;
    private HashMap<Integer, GameID> IDMap = new HashMap<>();

    public ChessClient(String serverUrl) {
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
        authToken = null;

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
        Integer gameNumber = IDMap.size() + 1;

        IDMap.put(gameNumber, gameID);


        return "Game successfully created. Game Number is " + gameNumber;
    }

    public String list() throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        GameList gameList = server.listGames(authToken);
        StringBuilder returnString = new StringBuilder();
        int gameNumber = 1;

        for (GameResult game: gameList.games()){

            GameID gameID = new GameID(game.gameID());
            IDMap.put(gameNumber, gameID);

            returnString.append(gameNumber).append(". \n");
            returnString.append("Game Name: ").append(game.gameName()).append("\n");
            returnString.append("White username: ").append(game.whiteUsername()).append("\n");
            returnString.append("Black username: ").append(game.blackUsername()).append("\n");
            returnString.append("\n");
            gameNumber++;
        }

        return returnString.toString();
    }

    public String play(String... params) throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        if (params.length < 2){
            throw new ResponseException("Error: Expected <gameNumber> <color>");
        }

        Integer gameNumber = Integer.parseInt(params[0]);
        if (!IDMap.containsKey(gameNumber)){
            throw new ResponseException("Error: Game does not exist");
        }
        String playerColor = params[1];
        if (!playerColor.equalsIgnoreCase("WHITE") && !playerColor.equalsIgnoreCase("BLACK")){
            throw new ResponseException("Error: Invalid Color");
        }

        GameID gameID = IDMap.get(gameNumber);
        JoinRequest joinRequest = new JoinRequest(playerColor, gameID.gameID());

        server.playGame(joinRequest, authToken);
        if (playerColor.equalsIgnoreCase("WHITE")){
            PrintBoard.printWhitePerspective(new ChessGame());
        }
        else {
            PrintBoard.printBlackPerspective(new ChessGame());
        }

        //the new ChessGame is a placeholder

        return "Game Joined. Have Fun!";
    }

    public String observe(String... params) throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        if (params.length < 1){
            throw new ResponseException("Error: Expected <gameNumber>");
        }

        Integer gameNumber = Integer.parseInt(params[0]);
        GameID gameID = IDMap.get(gameNumber);
        GameList gameList = server.listGames(authToken);

        //GameResult does not actually contain a ChessGame. Find a way to fix that. New ChessGame is placeholder

        for (GameResult game: gameList.games()){
            GameID tempID = new GameID(game.gameID());
            if (Objects.equals(gameID, tempID)){
              PrintBoard.printWhitePerspective(new ChessGame());
              return "Game Found! Observing game " + gameNumber;
            }
        }

        return null;
    }


}
