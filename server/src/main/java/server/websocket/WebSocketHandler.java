package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.HighlightCommand;
import websocket.commands.MoveCommand;
import websocket.messages.ErrorGameMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    AuthDAO authDAO;
    GameDAO gameDAO;
    String username;
    String playerColor;
    String authToken;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private final ConnectionManager connectionManager = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException,
            InvalidMoveException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        authToken = userGameCommand.getAuthToken();
        username = authDAO.getUsername(authToken);

        if (!authDAO.checkToken(authToken)) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Unauthorized");
            connectionManager.displayToRoot(session, errorMessage);
            return;
        }

        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(username, userGameCommand.getGameID(), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MoveCommand.class));
            case HIGHLIGHT -> highlight(new Gson().fromJson(message, HighlightCommand.class));
            case REDRAW -> redraw(userGameCommand);
            case LEAVE -> leave(userGameCommand);
            case RESIGN -> resign(userGameCommand);
        }
    }

    private void connect(String username, Integer gameID, Session session) throws DataAccessException, IOException {
        connectionManager.add(gameID, username, session);

        try {
            if (gameDAO.findGame(gameID) == null) {
                ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                        null, "Error: Game Not Found");
                connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
                return;
            }
        } catch (Exception e) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, e.getMessage());
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }

        playerColor = gameDAO.getPlayerColor(gameID, username);

        if (playerColor != null) {
            LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    gameDAO.findGame(gameID).game(), playerColor, null, null);
            connectionManager.broadcastToRoot(null, gameMessage, gameID, username);
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined the game as " + playerColor + ".");
            connectionManager.broadcast(username, serverMessage, gameID);
        }
        else {
            LoadGameMessage observeMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    gameDAO.findGame(gameID).game(), "WHITE", null, null);
            connectionManager.broadcastToRoot(null, observeMessage, gameID, username);
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined the game as an observer.");
            connectionManager.broadcast(username, serverMessage, gameID);
        }

    }

    private void makeMove(MoveCommand moveCommand) throws DataAccessException, IOException, InvalidMoveException {
        Integer gameID = moveCommand.getGameID();
        ChessMove newMove = moveCommand.getMove();
        ChessGame currentGame = gameDAO.findGame(gameID).game();
        playerColor = gameDAO.getPlayerColor(gameID, username);
        if (!authDAO.checkToken(authToken)) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Unauthorized");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }
        if (currentGame.isGameOver()){
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Game Ended");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }
        if (gameDAO.getPlayerColor(gameID, username) == null) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Observers can't make moves");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }
        if (!checkTurn(playerColor, gameID)){
            return;
        }
        try {
            currentGame.makeMove(newMove);
            gameDAO.updateGame(currentGame, gameID);
            LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    currentGame, playerColor, null,  null);
            if (playerColor.equalsIgnoreCase("WHITE")){
                connectionManager.broadcastGame(gameDAO.findGame(gameID).blackUsername(), gameMessage, gameID);
                gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        currentGame, "BLACK", null,  null);
                connectionManager.broadcastToRoot(null, gameMessage, gameID,
                        gameDAO.findGame(gameID).blackUsername());
            } else {
                connectionManager.broadcastToRoot(null, gameMessage, gameID, username);
                gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                        currentGame, "WHITE", null,  null);
                connectionManager.broadcastGame(username, gameMessage, gameID);
            }

            ChessPosition startPosition = newMove.getStartPosition();
            int startRow = startPosition.getRow();
            String startCol = convertNumberToLetter(startPosition.getColumn());

            ChessPosition endPosition = newMove.getEndPosition();
            int endRow = endPosition.getRow();
            String endCol = convertNumberToLetter(endPosition.getColumn());

            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has moved " + startCol +  startRow +
                            " to " + endCol + endRow + ".");
            connectionManager.broadcast(username, serverMessage, gameID);
        } catch (InvalidMoveException e) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Invalid Move");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
        }

        ChessGame.TeamColor currentColor = (playerColor.equalsIgnoreCase("WHITE") ?
                ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);
        ChessGame.TeamColor opposingColor = (currentColor == ChessGame.TeamColor.WHITE ?
                ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE);

        String opposingUsername;
        if (username.equalsIgnoreCase(gameDAO.findGame(gameID).whiteUsername())){
            opposingUsername = gameDAO.findGame(gameID).blackUsername();
        }
        else {
            opposingUsername = gameDAO.findGame(gameID).whiteUsername();
        }


        if (currentGame.isInCheckmate(opposingColor)) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    opposingUsername + " is in checkmate. " + username + " wins!");
            connectionManager.broadcast(null, serverMessage, gameID);
            currentGame.endGame();
            gameDAO.updateGame(currentGame, gameID);
        }
        else if (currentGame.isInCheck(opposingColor)){
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    opposingUsername + " is in check.");
            connectionManager.broadcast(null, serverMessage, gameID);
        }
        else if (currentGame.isInStalemate(opposingColor)) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    "Stalemate. Game Over.");
            connectionManager.broadcast(null, serverMessage, gameID);
            currentGame.endGame();
            gameDAO.updateGame(currentGame, gameID);
        }
    }

    private String convertNumberToLetter(int number){
        String letter = "";

        switch (number){
            case 1 -> letter = "a";
            case 2 -> letter = "b";
            case 3 -> letter = "c";
            case 4 -> letter = "d";
            case 5 -> letter = "e";
            case 6 -> letter = "f";
            case 7 -> letter = "g";
            case 8 -> letter = "h";
        }

        return letter;
    }

    private boolean checkTurn(String playerColor, Integer gameID) throws DataAccessException, IOException {
        ChessGame currentGame = gameDAO.findGame(gameID).game();
        ChessGame.TeamColor currentColor;
        if(playerColor.equalsIgnoreCase("WHITE")){
            currentColor = ChessGame.TeamColor.WHITE;
        }
        else {
            currentColor = ChessGame.TeamColor.BLACK;
        }

        if (currentColor != currentGame.getTeamTurn()){
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: It is not your turn");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return false;
        }

        return true;
    }

    private void highlight(HighlightCommand highlightCommand) throws DataAccessException, IOException {
        Integer gameID = highlightCommand.getGameID();
        ChessPosition position = highlightCommand.getPosition();
        ChessGame currentGame = gameDAO.findGame(gameID).game();
        playerColor = gameDAO.getPlayerColor(gameID, username);

        if (playerColor == null){
            playerColor = "WHITE";
        }

        if (currentGame.getBoard().getPiece(position) != null) {
            LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    currentGame, playerColor, "",  position);
            connectionManager.broadcastToRoot(null, gameMessage, gameID, username);
        }
        else {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Game is over");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
        }

    }

    private void leave(UserGameCommand gameCommand) throws IOException, DataAccessException {
        Integer gameID = gameCommand.getGameID();
        playerColor = gameDAO.getPlayerColor(gameID, username);
        connectionManager.remove(gameID, username);
        if (playerColor != null) {
            gameDAO.removePlayer(gameID, playerColor);
        }

        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has left the game");
        connectionManager.broadcast(username, serverMessage, gameID);
    }

    private void resign(UserGameCommand gameCommand) throws IOException, DataAccessException {
        Integer gameID = gameCommand.getGameID();
        ChessGame currentGame = gameDAO.findGame(gameID).game();
        if (currentGame.isGameOver()){
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Game is over");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }
        if (gameDAO.getPlayerColor(gameID, username) == null) {
            ErrorGameMessage errorMessage = new ErrorGameMessage(ServerMessage.ServerMessageType.ERROR,
                    null, "Error: Observers can't resign");
            connectionManager.broadcastToRoot(errorMessage, null, gameID, username);
            return;
        }
        currentGame.endGame();
        gameDAO.updateGame(currentGame, gameID);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has resigned. Game Over.");
        connectionManager.broadcast(null, serverMessage, gameID);
    }

    private void redraw(UserGameCommand gameCommand) throws DataAccessException, IOException {
        Integer gameID = gameCommand.getGameID();
        ChessGame currentGame = gameDAO.findGame(gameID).game();
        playerColor = gameDAO.getPlayerColor(gameID, username);

        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                currentGame, playerColor, "", null);
        connectionManager.broadcastToRoot(null, gameMessage, gameID, username);
    }
}
