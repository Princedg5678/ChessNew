package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType currentType;

    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.color = pieceColor;
        this.currentType = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return currentType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(myPosition);
        PieceType type = piece.getPieceType();

        switch (type){
            case PAWN -> moves.addAll(pawnMoves(board, myPosition));
            case ROOK -> moves.addAll(rookMoves(board, myPosition));
            case KNIGHT -> moves.addAll(knightMoves(board, myPosition));
            case BISHOP -> moves.addAll(bishopMoves(board, myPosition));
            case QUEEN -> moves.addAll(queenMoves(board, myPosition));
            case KING -> moves.addAll(kingMoves(board, myPosition));
        }

        return moves;
    }

    private boolean checkInBounds(ChessPosition possibleMove){
        return (possibleMove.getRow() < 9 && possibleMove.getRow() > 0
                && possibleMove.getColumn() < 9 && possibleMove.getColumn() > 0);

    }

    private ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition){
        if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            return movePawn(board, myPosition, 1);
        }
        else {
            return movePawn(board, myPosition, -1);
        }
    }

    private ArrayList<ChessMove> movePawn(ChessBoard board, ChessPosition myPosition, int moveDirection) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + moveDirection,
                myPosition.getColumn());

        if (checkInBounds(possibleMove)){
            if (board.getPiece(possibleMove) == null){
                moveList.addAll(pawnAddMove(myPosition, possibleMove));
            }
        }

        if (moveList.size() == 1 && ((myPosition.getRow() == 2  && moveDirection == 1)
                || (myPosition.getRow() == 7 && moveDirection == -1))){
            ChessPosition doubleMove = new ChessPosition(myPosition.getRow() + (moveDirection * 2),
                    myPosition.getColumn());
            if (board.getPiece(doubleMove) == null){
                moveList.add(new ChessMove(myPosition, doubleMove, null));
            }
        }

        moveList.addAll(movePawnCapturing(board, myPosition, moveDirection, 1));
        moveList.addAll(movePawnCapturing(board, myPosition, moveDirection, -1));
        moveList.removeIf(Objects::isNull);

        return moveList;
    }

    private ArrayList<ChessMove> movePawnCapturing(ChessBoard board, ChessPosition myPosition, int moveDirection,
                                                   int captureDirection) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + moveDirection,
                myPosition.getColumn() + captureDirection);

        if (checkInBounds(possibleMove)){
            if (board.getPiece(possibleMove) != null
                    && board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                moveList.addAll(pawnAddMove(myPosition, possibleMove));
            }
        }

        return moveList;
    }

    private ArrayList<ChessMove> pawnAddMove(ChessPosition myPosition, ChessPosition possibleMove){
        ArrayList<ChessMove> moveList = new ArrayList<>();
        if (possibleMove.getRow() == 8 || possibleMove.getRow() == 1){
            moveList.addAll(promotePawn(myPosition, possibleMove));
        }
        else{
            moveList.add(new ChessMove(myPosition, possibleMove, null));
        }

        return moveList;
    }

    private ArrayList<ChessMove> promotePawn(ChessPosition myPosition, ChessPosition possibleMove) {
        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.add(new ChessMove(myPosition, possibleMove, PieceType.ROOK));
        moveList.add(new ChessMove(myPosition, possibleMove, PieceType.KNIGHT));
        moveList.add(new ChessMove(myPosition, possibleMove, PieceType.BISHOP));
        moveList.add(new ChessMove(myPosition, possibleMove, PieceType.QUEEN));

        return moveList;
    }

    private ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.addAll(moveInDirection(board, myPosition, 1, 0));
        moveList.addAll(moveInDirection(board, myPosition, 0, 1));
        moveList.addAll(moveInDirection(board, myPosition, -1, 0));
        moveList.addAll(moveInDirection(board, myPosition, 0, -1));

        return moveList;

    }

    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition){

        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.add(fixedMoveDirection(board, myPosition, 2, 1));
        moveList.add(fixedMoveDirection(board, myPosition, 2, -1));
        moveList.add(fixedMoveDirection(board, myPosition, 1, 2));
        moveList.add(fixedMoveDirection(board, myPosition, -1, 2));
        moveList.add(fixedMoveDirection(board, myPosition, -2, 1));
        moveList.add(fixedMoveDirection(board, myPosition, -2, -1));
        moveList.add(fixedMoveDirection(board, myPosition, 1, -2));
        moveList.add(fixedMoveDirection(board, myPosition, -1, -2));

        moveList.removeIf(Objects::isNull);

        return moveList;

    }

    private ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition){

        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.addAll(moveInDirection(board, myPosition, 1, 1));
        moveList.addAll(moveInDirection(board, myPosition, -1, 1));
        moveList.addAll(moveInDirection(board, myPosition, -1, -1));
        moveList.addAll(moveInDirection(board, myPosition, 1, -1));

        return moveList;

    }

    private ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.addAll(rookMoves(board, myPosition));
        moveList.addAll(bishopMoves(board, myPosition));

        return moveList;
    }

    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition){

        ArrayList<ChessMove> moveList = new ArrayList<>();
        moveList.add(fixedMoveDirection(board, myPosition, 1, 0));
        moveList.add(fixedMoveDirection(board, myPosition, 0, 1));
        moveList.add(fixedMoveDirection(board, myPosition, -1, 0));
        moveList.add(fixedMoveDirection(board, myPosition, 0, -1));
        moveList.add(fixedMoveDirection(board, myPosition, 1, 1));
        moveList.add(fixedMoveDirection(board, myPosition, -1, 1));
        moveList.add(fixedMoveDirection(board, myPosition, -1, -1));
        moveList.add(fixedMoveDirection(board, myPosition, 1, -1));

        moveList.removeIf(Objects::isNull);

        return moveList;
    }

    private ChessMove fixedMoveDirection(ChessBoard board, ChessPosition myPosition,
                                         int rowDirection, int columnDirection){
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + rowDirection,
                myPosition.getColumn() + columnDirection);

        if (checkInBounds(possibleMove)){

            if (board.getPiece(possibleMove) == null){
                return new ChessMove(myPosition, possibleMove, null);
            }
            else if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                return new ChessMove(myPosition, possibleMove, null);
            }
        }

        return null;
    }

    private ArrayList<ChessMove> moveInDirection(ChessBoard board, ChessPosition myPosition,
                                                 int rowDirection, int columnDirection){
        ArrayList<ChessMove> moveList = new ArrayList<>();
        ChessPosition possibleMove = new ChessPosition(myPosition.getRow() + rowDirection,
                myPosition.getColumn() + columnDirection);

        while (checkInBounds(possibleMove)){

            if (board.getPiece(possibleMove) == null){
                moveList.add(new ChessMove(myPosition, possibleMove, null));
                possibleMove = new ChessPosition(possibleMove.getRow() + rowDirection,
                        possibleMove.getColumn() + columnDirection);
            }
            else if (board.getPiece(possibleMove).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                moveList.add(new ChessMove(myPosition, possibleMove, null));
                break;
            }
            else{
                break;
            }

        }

        return moveList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return color == that.color && currentType == that.currentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, currentType);
    }

    @Override
    public String toString() {
        return color + " " + currentType;
    }
}
