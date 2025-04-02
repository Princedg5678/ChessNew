package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static ui.EscapeSequences.*;

public class PrintBoard {


    public static void main(String[] args){
        printWhitePerspective(new ChessGame(), new ChessPosition(4, 4));
        printBlackPerspective(new ChessGame(), new ChessPosition(5, 6));
    }

    public static void printWhitePerspective(ChessGame ourGame, ChessPosition position){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawTopOrBottomRow(out, true);
        drawBoard(out, ourGame, true, position);
        drawTopOrBottomRow(out, true);
        out.print("\n");

    }

    public static void printBlackPerspective(ChessGame ourGame, ChessPosition position){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawTopOrBottomRow(out, false);
        drawBoard(out, ourGame, false, position);
        drawTopOrBottomRow(out, false);
        out.print("\n");

    }

    private static void drawBoard(PrintStream out, ChessGame game, boolean whitePerspective, ChessPosition position){

        ChessBoard theBoard = game.getBoard();
        ArrayList<ChessPosition> endPositions = new ArrayList<>();

        if (position != null) {
            Collection<ChessMove> possibleMoves = game.validMoves(position);
            for (ChessMove move: possibleMoves){
                endPositions.add(move.getEndPosition());
            }
        }

        if (whitePerspective) {
            for (int i = 8; i > 0; i--) {
                printSide(i, out);
                for (int j = 1; j < 9; j++) {
                    printBoardRow(i, j, out, theBoard, endPositions, position);
                }
                printSide(i, out);
                out.print(RESET_BG_COLOR);
                out.print("\n");
            }
        }
        else {
            for (int i = 1; i < 9; i++) {
                printSide(i, out);
                for (int j = 8; j > 0; j--) {
                    printBoardRow(i, j, out, theBoard, endPositions, position);
                }
                printSide(i, out);
                out.print(RESET_BG_COLOR);
                out.print("\n");
            }
        }
    }

    private static void printSide(int rowNumber, PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(" ");
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(rowNumber);
        out.print(" ");
    }

    private static void printBoardRow(int row, int column, PrintStream out, ChessBoard board,
                                      ArrayList<ChessPosition> possibleMoves, ChessPosition piecePosition){

        ChessPosition position = new ChessPosition(row, column);


        if (piecePosition != null && (piecePosition.getRow() == row && piecePosition.getColumn() == column)){
            out.print(SET_BG_COLOR_YELLOW);
        }
        else if ((9 - row + column) % 2 == 0) {
            if (possibleMoves.contains(position)) {
                out.print(SET_BG_COLOR_GREEN);
            }
            else {
                out.print(SET_BG_COLOR_WHITE);
            }
        }
        else {
            if (possibleMoves.contains(position)) {
                out.print(SET_BG_COLOR_DARK_GREEN);
            }
            else {
                out.print(SET_BG_COLOR_BLACK);
            }
        }

        ChessPiece piece = board.getPiece(new ChessPosition(row, column));

        if (piece == null) {
            out.print("   ");
        } else {
            printPiece(out, piece);
        }
    }

    private static void printPiece(PrintStream out, ChessPiece piece){
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();


        if (color == ChessGame.TeamColor.BLACK) {
            out.print(SET_TEXT_COLOR_DARK_BLUE);
        }
        else{
            out.print(SET_TEXT_COLOR_RED);
        }

        switch (type){
            case PAWN -> {
                 if (color == ChessGame.TeamColor.BLACK) {
                     out.print(" P ");
                 }
                 else{
                     out.print(" P ");
                 }
            }
            case ROOK -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" R ");
                }
                else{
                    out.print(" R ");
                }
            }
            case KNIGHT -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" N ");
                }
                else{
                    out.print(" N ");
                }
            }
            case BISHOP -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" B ");
                }
                else{
                    out.print(" B ");
                }
            }
            case QUEEN -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" Q ");
                }
                else{
                    out.print(" Q ");
                }
            }
            case KING -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" K ");
                }
                else{
                    out.print(" K ");
                }
            }
        }


    }

    private static void drawTopOrBottomRow(PrintStream out, boolean whitePerspective){
        ArrayList<String> letters = new ArrayList<>(Arrays.asList("a","b","c","d","e","f","g","h"));

        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("   ");

        if (!whitePerspective){
            Collections.reverse(letters);
        }

        for (String letter: letters){
            out.print(" ");
            out.print(letter);
            out.print(" ");
        }

        out.print("   ");
        out.print(RESET_BG_COLOR);
        out.print("\n");

    }
}
