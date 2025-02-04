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
//                case "quit" -> ;
//                case "login" -> ;
//                case "register" -> ;
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



}
