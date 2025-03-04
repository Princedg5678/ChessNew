package websocket;

import model.GameID;
import ui.ResponseException;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint{

    Session session;
    ServerMessageHandler serverMessageHandler;

    public WebSocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message){
                    serverMessageHandler.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ex.getMessage());
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void playGame(Integer gameID, String playerColor, String authToken) throws ResponseException {
        try {
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                    authToken, gameID, playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    //work on above function

}
