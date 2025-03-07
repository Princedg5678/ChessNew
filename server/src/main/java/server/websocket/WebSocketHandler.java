package server.websocket;

import chess.ChessGame;
import dataaccess.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;

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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        String authToken = userGameCommand.getAuthToken();
        username = authDAO.getUsername(authToken);
        switch (userGameCommand.getCommandType()){
            case CONNECT -> connect(username, authToken, userGameCommand.getColor(),
                    userGameCommand.getGameID(), session);
//            case MAKE_MOVE -> ;
//            case LEAVE -> ;
//            case RESIGN -> ;
        }
    }

    private void connect(String username, String authToken, String playerColor,
                         Integer gameID, Session session) throws DataAccessException, IOException {
        connectionManager.add(gameID, username, session);
        LoadGameMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                gameDAO.findGame(gameID).game(), playerColor, "You joined the game.");
        connectionManager.broadcastToRoot(gameMessage, gameID, username);
        ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                username + " has joined the game.");
        connectionManager.broadcast(username, serverMessage, gameID);

    }

}
