package chess;

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
    public boolean isValid() {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
