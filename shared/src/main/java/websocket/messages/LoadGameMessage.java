package websocket.messages;

import chess.ChessGame;
import chess.ChessPosition;

public class LoadGameMessage extends ServerMessage{

    private final ChessGame game;
    private final String color;
    private final ChessPosition position;

    public LoadGameMessage(ServerMessageType type, ChessGame game, String playerColor, String message,
                           ChessPosition position) {
        super(type, message);
        this.game = game;
        this.color = playerColor;
        this.position = position;
    }

    public ChessGame getGame(){
        return game;
    }

    public String getColor(){
        return color;
    }

    public ChessPosition getPosition() {
        return position;
    }
}
