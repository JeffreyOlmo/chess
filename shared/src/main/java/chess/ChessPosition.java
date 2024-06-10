package chess;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 */
public class ChessPosition {

    int row;
    int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public ChessPosition(String notation) throws Exception {
        notation = notation.toLowerCase(Locale.ROOT);
        if (notation.length() == 2) {
            col = notation.charAt(0) - 'a' + 1;
            row = notation.charAt(1) - '1' + 1;
            return;
        }
        throw new Exception("Invalid notation");
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return col;
    }

    /**
     * Adjusts this position by a certain amount
     * @return a new ChessPosition adjusted from the current one
     */
    public ChessPosition adjust(int rowChange, int colChange) {
        return new ChessPosition(row + rowChange, col + colChange);
    }

    /**
     * Checks if the position is valid on a chessboard
     * Assumes a standard 8x8 chess board and 1-based indexing for row and column
     * @return true if the position is valid, false otherwise
     */
    public boolean inBoard() {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return String.format("%d%d", row, col);
    }
}

