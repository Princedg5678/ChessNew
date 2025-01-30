package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class PrintBoard {


    public static void main(String[] args){
        printWhitePerspective(new ChessGame());
    }

    public static void printWhitePerspective(ChessGame ourGame){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawTopOrBottomRow(out);
        drawBoardWhite(out, ourGame);
        drawTopOrBottomRow(out);

    }

    public static void printBlackPerspective(ChessGame ourGame){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);


    }

    private static void drawBoardWhite(PrintStream out, ChessGame game){

        out.print("\n");

        ChessBoard theBoard = game.getBoard();

        for (int i = 8; i > 0; i--){
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(i);
            out.print(" ");
            for (int j = 8; j > 0; j--){

                if ((9 - i + j) % 2 == 0){
                    out.print(SET_BG_COLOR_BLACK);
                }
                else {
                    out.print(SET_BG_COLOR_WHITE);
                }

                ChessPiece piece = theBoard.getPiece(new ChessPosition(i, j));

                if (piece == null){
                    out.print("   ");
                }
                else{
                    printPiece(out, piece);
                }
            }
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(SET_TEXT_COLOR_WHITE);
            out.print(i);
            out.print(" ");
            out.print(RESET_BG_COLOR);
            out.print("\n");
        }



//        out.print(SET_TEXT_COLOR_DARK_BLUE);
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(" r ");
//        out.print(SET_BG_COLOR_DARK_GREEN);
//        out.print(" n ");
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(" b ");
    }

    private static void printPiece(PrintStream out, ChessPiece piece){
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();

        //fix piece orientation

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
                     out.print(" p ");
                 }
            }
            case ROOK -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" R ");
                }
                else{
                    out.print(" r ");
                }
            }
            case KNIGHT -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" N ");
                }
                else{
                    out.print(" n ");
                }
            }
            case BISHOP -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" B ");
                }
                else{
                    out.print(" b ");
                }
            }
            case QUEEN -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" Q ");
                }
                else{
                    out.print(" q ");
                }
            }
            case KING -> {
                if (color == ChessGame.TeamColor.BLACK) {
                    out.print(" K ");
                }
                else{
                    out.print(" k ");
                }
            }
        }


    }

    private static void drawTopOrBottomRow(PrintStream out){
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print("   ");
        out.print(" a ");
        out.print(" b ");
        out.print(" c ");
        out.print(" d ");
        out.print(" e ");
        out.print(" f ");
        out.print(" g ");
        out.print(" h ");
        out.print("   ");
        out.print(RESET_BG_COLOR);
    }
}
