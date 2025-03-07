package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{

    private final ChessGame game;
    private final String color;

    public LoadGameMessage(ServerMessageType type, ChessGame game, String playerColor, String message) {
        super(type, message);
        this.game = game;
        this.color = playerColor;
    }

    public ChessGame getGame(){
        return game;
    }

    public String getColor(){
        return color;
    }


}
