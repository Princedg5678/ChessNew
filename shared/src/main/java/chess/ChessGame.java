package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard myBoard = new ChessBoard();
    private TeamColor currentColor = TeamColor.WHITE;
    private ChessBoard pretendBoard = new ChessBoard();

    public ChessGame() {
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public void copyBoard(){
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                ChessPosition tempPosition = new ChessPosition(i,j);
                pretendBoard.addPiece(tempPosition ,myBoard.getPiece(tempPosition));
            }
        }
    }


    public boolean pretendMove(ChessMove move){
        copyBoard();
        ChessPosition currentPosition = move.getStartPosition();
        ChessPosition endPosition =  move.getEndPosition();
        ChessPiece pretendPiece = pretendBoard.getPiece(currentPosition);
        if (pretendBoard.getPiece(endPosition) == null ||
                pretendBoard.getPiece(currentPosition).getTeamColor()
                        != pretendBoard.getPiece(endPosition).getTeamColor()) {
            pretendBoard.addPiece(endPosition, pretendPiece);
            pretendBoard.removePiece(currentPosition);
            return !checkNoCopy(pretendPiece.getTeamColor());
        }
        else {
            return false;
        }
    }


    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> potentialMoves = myBoard.getPiece(startPosition).pieceMoves(getBoard(),startPosition);
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves){
            if (pretendMove(move)){
                validMoves.add(move);
            }
        }
        return validMoves;

    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition currentPosition = move.getStartPosition();
        ChessPosition endPosition =  move.getEndPosition();
        ChessPiece piece = myBoard.getPiece(currentPosition);

        if (myBoard.getPiece(currentPosition) == null){
            throw new InvalidMoveException();
        }

        if (getTeamTurn() != piece.getTeamColor()){
            throw new InvalidMoveException();
        }

        if (myBoard.getPiece(endPosition) != null) {
            if (myBoard.getPiece(currentPosition).getTeamColor()
                    == myBoard.getPiece(endPosition).getTeamColor()) {
                throw new InvalidMoveException();
            }
        }

        Collection<ChessMove> validMoves = validMoves(currentPosition);

        if (!validMoves.contains(move)){
            throw new InvalidMoveException();
        }

        if (move.getPromotionPiece() != null){
            ChessPiece promotionPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            myBoard.addPiece(endPosition, promotionPiece);
            myBoard.removePiece(currentPosition);
        }
        else {
            myBoard.addPiece(endPosition, piece);
            myBoard.removePiece(currentPosition);
        }

        if (currentColor == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }

    }


    public ChessPosition findKing(TeamColor teamColor) {
        ChessPosition tempPosition;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                tempPosition = new ChessPosition(i, j);
                if (pretendBoard.getPiece(tempPosition) != null) {
                    if (pretendBoard.getPiece(tempPosition).getPieceType() == ChessPiece.PieceType.KING
                            && pretendBoard.getPiece(tempPosition).getTeamColor() == teamColor) {
                        return tempPosition;
                    }
                }
            }
        }
        return null;
    }

    public Set<ChessMove> opponentMoves(TeamColor teamColor) {
        TeamColor opponentColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE){
            opponentColor = TeamColor.BLACK;
        }

        HashSet<ChessMove> moveList = new HashSet<ChessMove>();
        ArrayList<ChessPosition> piecePositions = findPositions(opponentColor);

        for (ChessPosition position : piecePositions){
            ChessPiece piece = new ChessPiece(pretendBoard.getPiece(position).getTeamColor(),
                    pretendBoard.getPiece(position).getPieceType());
            moveList.addAll(piece.pieceMoves(pretendBoard, position));
        }
        return  moveList;
    }

    //add special case for pawns

    public ArrayList<ChessPosition> findPositions (TeamColor teamColor){

        ChessPosition tempPosition;
        ArrayList<ChessPosition> piecePositions = new ArrayList<ChessPosition>();
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++) {
                tempPosition = new ChessPosition(i, j);
                if (pretendBoard.getPiece(tempPosition) != null
                        && pretendBoard.getPiece(tempPosition).getTeamColor() == teamColor) {
                    piecePositions.add(tempPosition);
                }
            }
        }
        return piecePositions;
    }

    public boolean checkNoCopy(TeamColor teamColor) {
        Collection<ChessMove> opponentsMoves;
        opponentsMoves = opponentMoves(teamColor);
        ChessPosition kingPosition = findKing(teamColor);

        for (ChessMove move : opponentsMoves){
            if (move.getEndPosition().equals(kingPosition)){
                return true;
            }
        }
        return false;

    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        copyBoard();
        return checkNoCopy(teamColor);
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ArrayList<ChessMove> tempCount;
        tempCount = new ArrayList<>();
        if (isInCheck(teamColor)){
            for (int i = 1; i < 9; i++){
                for (int j = 1; j < 9; j++){
                    if (myBoard.getPiece(new ChessPosition(i,j)) != null) {
                        if (myBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                            tempCount.addAll(validMoves(new ChessPosition(i, j)));
                        }
                    }
                }
            }
            if (tempCount.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ArrayList<ChessMove> tempCount;
        tempCount = new ArrayList<>();
        if (!isInCheck(teamColor)){
            for (int i = 1; i < 9; i++){
                for (int j = 1; j < 9; j++){
                    if (myBoard.getPiece(new ChessPosition(i,j)) != null) {
                        if (myBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                            tempCount.addAll(validMoves(new ChessPosition(i, j)));

                        }
                    }
                }
            }
            if (tempCount.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

}