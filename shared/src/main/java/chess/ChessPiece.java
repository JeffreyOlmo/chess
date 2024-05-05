package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
        throw new RuntimeException("Not implemented");
    }
        return this.pieceColor
    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }
        return this.type;
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
// ...

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        switch (type) {
            case KING:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        ChessPosition newPosition = myPosition.adjust(dx, dy);
                        if (newPosition.isValid()) {
                            validMoves.add(new ChessMove(myPosition, newPosition));
                        }
                    }
                }
                break;
            case QUEEN:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            ChessPosition newPosition = myPosition.adjust(dx, dy);
                            while (newPosition.isValid()) {
                                validMoves.add(new ChessMove(myPosition, newPosition));
                                newPosition = newPosition.adjust(dx, dy);
                            }
                        }
                    }
                }
                break;
            case BISHOP:
                for (int dx = -1; dx <= 1; dx+=2) {
                    for (int dy = -1; dy <= 1; dy+=2) {
                        ChessPosition newPosition = myPosition.adjust(dx, dy);
                        while (newPosition.isValid()) {
                            validMoves.add(new ChessMove(myPosition, newPosition));
                            newPosition = newPosition.adjust(dx, dy);
                        }
                    }
                }
                break;
            case KNIGHT:
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (Math.abs(dx * dy) == 2) {
                            ChessPosition newPosition = myPosition.adjust(dx, dy);
                            if (newPosition.isValid()) {
                                validMoves.add(new ChessMove(myPosition, newPosition));
                            }
                        }
                    }
                }
                break;
            case ROOK:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 || dy == 0) {
                            ChessPosition newPosition = myPosition.adjust(dx, dy);
                            while (newPosition.isValid()) {
                                validMoves.add(new ChessMove(myPosition, newPosition));
                                newPosition = newPosition.adjust(dx, dy);
                            }
                        }
                    }
                }
                break;
            case PAWN:
                ChessPosition newPosition = pieceColor == TeamColor.WHITE ? myPosition.adjust(0, 1) : myPosition.adjust(0, -1);
                if (newPosition.isValid()) {
                    validMoves.add(new ChessMove(myPosition, newPosition));
                }
                break;
        }
        return validMoves;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPiece that = (ChessPiece) o;

        if (pieceColor != that.pieceColor) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = pieceColor != null ? pieceColor.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

}
