package websocket.commands;

import chess.ChessPosition;

public class HighlightCommand extends UserGameCommand{

    private final ChessPosition position;

    public HighlightCommand(CommandType commandType, String authToken, Integer gameID,
                            String playerColor, ChessPosition chessPosition) {
        super(commandType, authToken, gameID, playerColor);
        this.position = chessPosition;
    }

    public ChessPosition getPosition() {
        return position;
    }
}
