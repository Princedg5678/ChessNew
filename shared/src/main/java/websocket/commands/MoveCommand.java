package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand{

    private final ChessMove move;

    public MoveCommand(CommandType commandType, String authToken, Integer gameID,
                       String playerColor, ChessMove move){
        super(commandType, authToken, gameID, playerColor);
        this.move = move;

    }

    public ChessMove getMove(){
        return move;
    }
}
