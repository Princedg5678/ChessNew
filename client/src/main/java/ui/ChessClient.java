package ui;

import java.util.Arrays;

public class ChessClient {

    //private final ServerFacade server;
    private final String serverURL;
    private final State currentState = State.SIGNEDOUT;


    public ChessClient(String serverUrl){
        this.serverURL = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "login" -> ;
                case "register" -> register(params);
//                case "logout" -> ;
//                case "create" -> ;
//                case "list" -> ;
//                case "play" -> ;
//                case "observe" -> ;
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

        return null;
    }


}
