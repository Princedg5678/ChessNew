package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class PrintBoard {


    public static void main(String[] args){
        printWhitePerspective(new ChessGame());
    }

    public static void printWhitePerspective(ChessGame ourGame){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        drawTopRow(out);
    }

    public static void printBlackPerspective(ChessGame ourGame){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);


    }

    private static void drawTopRow(PrintStream out){
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
    }
}
