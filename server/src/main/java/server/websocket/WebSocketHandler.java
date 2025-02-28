package server.websocket;

import dataaccess.*;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {

    AuthDAO authDAO;
    String username;

    public WebSocketHandler(AuthDAO authDAO){
        this.authDAO = authDAO;
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
                         Integer gameID, Session session){
        connectionManager.add(gameID, username, session);

    }

}
