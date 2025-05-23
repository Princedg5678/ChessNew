package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.*;
import server.ServerFacade;
import websocket.WebSocketFacade;
import websocket.ServerMessageHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class ChessClient {

    private final String serverURL;
    private State currentState = State.SIGNEDOUT;
    private final ServerFacade server;
    private final ServerMessageHandler sms;
    private WebSocketFacade ws;
    private String authToken;
    private HashMap<Integer, GameID> idMap = new HashMap<>();
    private Integer currentGameID;
    private String currentColor;

    public ChessClient(String serverUrl, ServerMessageHandler serverMessageHandler) {
        this.serverURL = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.sms = serverMessageHandler;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (currentState == State.SIGNEDOUT){
                return switch (cmd) {
                    case "login" -> login(params);
                    case "register" -> register(params);
                    default -> help();
                };
            }
            else if (currentState == State.SIGNEDIN) {
                return switch (cmd) {
                    case "logout" -> logout();
                    case "create" -> create(params);
                    case "list" -> list();
                    case "play" -> play(params);
                    case "observe" -> observe(params);
                    default -> help();
                };
            } else if (currentState == State.RESIGN) {
                return switch (cmd) {
                    case "yes" -> resign();
                    case "no" -> resignCancel();
                    default -> help();
                };
            } else {
                return switch (cmd) {
                    case "move" -> move(params);
                    case "redraw" -> redraw();
                    case "highlight" -> highlight(params);
                    case "resign" -> resignState();
                    case "leave" -> leave();
                    default -> help();
                };
            }
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
        else if (currentState == State.PLAYINGGAME){
            return """
                    - redraw
                    - move <piece> <space> <promotion>
                    - highlight <piece>
                    - help
                    - resign
                    - leave
                    """;
        }
        else if (currentState == State.RESIGN){
            return """
                    - yes
                    - no
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
        Integer gameNumber = idMap.size() + 1;

        idMap.put(gameNumber, gameID);


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
            idMap.put(gameNumber, gameID);

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
        else if (currentState == State.PLAYINGGAME){
            throw new ResponseException("Error: Already In Game");
        }

        if (params.length < 2){
            throw new ResponseException("Error: Expected <gameNumber> <color>");
        }

        Integer gameNumber;

        try {
            gameNumber = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            throw new ResponseException("Error: Expected <gameNumber> <color>");
        }


        if (!idMap.containsKey(gameNumber)){
            throw new ResponseException("Error: Game does not exist");
        }
        String playerColor = params[1];
        if (!playerColor.equalsIgnoreCase("WHITE") && !playerColor.equalsIgnoreCase("BLACK")){
            throw new ResponseException("Error: Invalid Color");
        }



        GameID gameID = idMap.get(gameNumber);
        JoinRequest joinRequest = new JoinRequest(playerColor, gameID.gameID());

        server.playGame(joinRequest, authToken);
        ws = new WebSocketFacade(serverURL, sms);
        ws.connectToGame(gameID.gameID(), authToken);

        if(playerColor.equalsIgnoreCase("WHITE")){
            currentColor = "WHITE";
        }
        else {
            currentColor = "BLACK";
        }

        currentGameID = gameID.gameID();
        currentState = State.PLAYINGGAME;
        System.out.println("Game Joined. Have Fun!");

        return help();
    }

    public String observe(String... params) throws ResponseException{
        if (currentState == State.SIGNEDOUT){
            throw new ResponseException("Error: Not Logged in");
        }

        if (params.length < 1){
            throw new ResponseException("Error: Expected <gameNumber>");
        }

        Integer gameNumber = Integer.parseInt(params[0]);
        GameID gameID = idMap.get(gameNumber);
        GameList gameList = server.listGames(authToken);

        for (GameResult game: gameList.games()){
            GameID tempID = new GameID(game.gameID());
            if (Objects.equals(gameID, tempID)){

                ws = new WebSocketFacade(serverURL, sms);
                ws.connectToGame(gameID.gameID(), authToken);

                currentGameID = gameID.gameID();
                currentState = State.PLAYINGGAME;
                return "Game Found! Observing game " + gameNumber;
            }
        }

        return null;
    }

    public String move(String... params) throws ResponseException {
        if (params.length < 2){
            throw new ResponseException("Error: Expected <piece> <space> <promotion>");
        }

        if (!params[0].matches("^[a-h][1-8]$") && !params[1].matches("^[a-h][1-8]$")){
            throw new ResponseException("Error: Invalid Coordinates");
        }

        String tempStart = params[0];
        String tempEnd = params[1];
        ChessPiece.PieceType promotionPiece = null;

        if (params.length == 3){
            String tempPromotion = params[2];
            if (tempPromotion.equalsIgnoreCase("ROOK")){
                promotionPiece = ChessPiece.PieceType.ROOK;
            }
            else if (tempPromotion.equalsIgnoreCase("KNIGHT")){
                promotionPiece = ChessPiece.PieceType.KNIGHT;
            }
            else if (tempPromotion.equalsIgnoreCase("BISHOP")){
                promotionPiece = ChessPiece.PieceType.BISHOP;
            }
            else if (tempPromotion.equalsIgnoreCase("QUEEN")){
                promotionPiece = ChessPiece.PieceType.QUEEN;
            }
            else {
                throw new ResponseException("Error: Invalid Promotion Piece");
            }
        }

        char rowNumber = tempStart.charAt(1);
        char colNumber = tempStart.charAt(0);

        ChessPosition startPosition = new ChessPosition(Integer.parseInt(String.valueOf(rowNumber)),
                convertLetterToNumber(colNumber));

        rowNumber = tempEnd.charAt(1);
        colNumber = tempEnd.charAt(0);

        ChessPosition endPosition = new ChessPosition(Integer.parseInt(String.valueOf(rowNumber)),
                convertLetterToNumber(colNumber));

        ChessMove newMove = new ChessMove(startPosition, endPosition, promotionPiece);
        ws.makeMove(newMove, currentGameID, authToken);


        return "";
    }

    public int convertLetterToNumber(char letter){
        letter = Character.toLowerCase(letter);
        return letter - 'a' + 1;
    }

    public String highlight(String... params) throws ResponseException {
        if (params.length < 1){
            throw new ResponseException("Error: Expected <piece>");
        }
        if (!params[0].matches("^[a-h][1-8]$") && !params[1].matches("^[a-h][1-8]$")){
            throw new ResponseException("Error: Invalid Coordinates");
        }

        String tempPosition = params[0];
        char rowNumber = tempPosition.charAt(1);
        char colNumber = tempPosition.charAt(0);
        ChessPosition piecePosition = new ChessPosition(Integer.parseInt(String.valueOf(rowNumber)),
                convertLetterToNumber(colNumber));

        ws.highlightMoves(piecePosition, currentGameID, authToken);

        return "";
    }

    public String redraw() throws ResponseException {

        ws.redraw(currentGameID, authToken);

        return "";
    }

    public String resignState(){
        currentState = State.RESIGN;
        System.out.println("Are you sure you want to resign?");
        return help();
    }

    public String resignCancel(){
        currentState = State.PLAYINGGAME;
        return help();
    }

    public String resign() throws ResponseException {

        ws.resign(currentGameID, authToken);
        currentState = State.PLAYINGGAME;

        return help();
    }

    public String leave() throws ResponseException {

        ws.leave(currentGameID, authToken);
        currentState = State.SIGNEDIN;

        return help();
    }


}
