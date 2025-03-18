package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MoveCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    AuthDAO authDAO;
    GameDAO gameDAO;
    String username;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private final ConnectionManager connectionManager = new ConnectionManager();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException,
            InvalidMoveException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        String authToken = userGameCommand.getAuthToken();
        username = authDAO.getUsername(authToken);

        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(username, userGameCommand.getColor(),
                    userGameCommand.getGameID(), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MoveCommand.class));
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    private void connect(String username, String playerColor,
                         Integer gameID, Session session) throws DataAccessException, IOException {
        connectionManager.add(gameID, username, session);
        if (playerColor != null) {
            LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    gameDAO.findGame(gameID).game(), playerColor, "You joined the game.");
            connectionManager.broadcastToRoot(null, gameMessage, gameID, username);
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined the game as " + playerColor + ".");
            connectionManager.broadcast(username, serverMessage, gameID);
            //figure out why board is being compared in the test
        }
        else {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " has joined the game as an observer.");
            connectionManager.broadcast(username, serverMessage, gameID);
        }

    }

    private void makeMove(MoveCommand moveCommand) throws DataAccessException, IOException, InvalidMoveException {
        String authToken = moveCommand.getAuthToken();
        Integer gameID = moveCommand.getGameID();
        String playerColor = moveCommand.getColor();
        ChessMove newMove = moveCommand.getMove();
        ChessGame currentGame = gameDAO.findGame(gameID).game();

        if (!checkTurn(playerColor, gameID)){
            return;
        }

        try {
            currentGame.makeMove(newMove);

        } catch (InvalidMoveException e) {
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Invalid Moved");
            connectionManager.broadcastToRoot(serverMessage, null, gameID, username);
        }


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
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: It is not your turn");
            connectionManager.broadcastToRoot(serverMessage, null, gameID, username);
            return false;
        }

        return true;
    }

    private void leave(){

    }

    private void resign(){

    }

}
