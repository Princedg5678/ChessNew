package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int theRow;
    private final int theColumn;

    public ChessPosition(int row, int col) {
        this.theRow = row;
        this.theColumn = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return theRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return theColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition position = (ChessPosition) o;
        return theRow == position.theRow && theColumn == position.theColumn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(theRow, theColumn);
    }

    @Override
    public String toString() {
        return  theRow + theColumn + "";
    }
}
