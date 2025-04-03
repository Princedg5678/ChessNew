package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.GameID;
import ui.ResponseException;
import com.google.gson.Gson;
import websocket.commands.HighlightCommand;
import websocket.commands.MoveCommand;
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

    public void playGame(Integer gameID, String authToken) throws ResponseException {
        try {
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                    authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void observeGame(Integer gameID, String authToken) throws ResponseException {
        try{
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                    authToken, gameID, null);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(ChessMove newMove, Integer currentID,
                         String currentColor, String authToken) throws ResponseException {
        try{
            MoveCommand moveCommand = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, currentID,
                    currentColor, newMove);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    public void highlightMoves(ChessPosition piecePosition, Integer currentID,
                               String currentColor, String authToken) throws ResponseException {
        try{
            HighlightCommand highlightCommand = new HighlightCommand(UserGameCommand.CommandType.HIGHLIGHT, authToken,
                    currentID, currentColor, piecePosition);
            this.session.getBasicRemote().sendText(new Gson().toJson(highlightCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leave(Integer currentID, String currentColor, String authToken) throws ResponseException {
        try{
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken,
                    currentID, currentColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void resign(Integer currentID, String currentColor, String authToken) throws ResponseException {
        try{
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken,
                    currentID, currentColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void redraw(Integer currentID, String currentColor, String authToken) throws ResponseException {
        try{
            UserGameCommand gameCommand = new UserGameCommand(UserGameCommand.CommandType.REDRAW, authToken,
                    currentID, currentColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(gameCommand));
        } catch (IOException ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
}
