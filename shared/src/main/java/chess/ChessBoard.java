package chess;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] theBoard;

    public ChessBoard() {
        theBoard = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        theBoard[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    public void removePiece(ChessPosition position) {
        theBoard[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return theBoard[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        resetPawns(2, ChessGame.TeamColor.WHITE);
        resetPawns(7, ChessGame.TeamColor.BLACK);

        resetPieces(1, ChessGame.TeamColor.WHITE);
        resetPieces(8, ChessGame.TeamColor.BLACK);
    }

    private void resetPawns(int row, ChessGame.TeamColor color){
        for (int i = 1; i < 9; i++){
            addPiece(new ChessPosition(row, i),
                    new ChessPiece(color, ChessPiece.PieceType.PAWN));
        }
    }

    private void resetPieces(int row, ChessGame.TeamColor color){
        addPiece(new ChessPosition(row,1), new ChessPiece(color, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row,2), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row,3), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row,4), new ChessPiece(color, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row,5), new ChessPiece(color, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(row,6), new ChessPiece(color, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row,7), new ChessPiece(color, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row,8), new ChessPiece(color, ChessPiece.PieceType.ROOK));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(theBoard, that.theBoard);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(theBoard);
    }

    @Override
    public String toString() {
        return Arrays.deepToString(theBoard);
    }
}
