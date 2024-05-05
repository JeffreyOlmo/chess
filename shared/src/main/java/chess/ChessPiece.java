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
    public ChessGame.TeamColor getTeamColor(){
        return this.pieceColor;
    }

    public PieceType getPieceType(){
        return this.type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        switch (this.type) {
            case KING:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        ChessPosition newPosition = myPosition.adjust(dx, dy);
                        if(newPosition.inBoard()) {
                            ChessPiece otherPiece = board.getPiece(newPosition);
                            if (otherPiece != null) {
                                if (otherPiece.getTeamColor() != this.pieceColor) {
                                    validMoves.add(new ChessMove(myPosition, newPosition));
                                }
                            } else {
                                validMoves.add(new ChessMove(myPosition, newPosition));
                            }
                        }
                    }
                }
                break;
            case QUEEN:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            ChessPosition newPosition = myPosition.adjust(dx, dy);
                            while (newPosition.inBoard()) {
                                ChessPiece otherPiece = board.getPiece(newPosition);
                                if (otherPiece != null) {
                                    if (otherPiece.getTeamColor() != this.pieceColor) {
                                        validMoves.add(new ChessMove(myPosition, newPosition));
                                    }
                                    break;
                                }
                                validMoves.add(new ChessMove(myPosition, newPosition));
                                newPosition = newPosition.adjust(dx, dy);
                            }
                        }
                    }
                }
                break;
            case BISHOP:
                for (int dx = -1; dx <= 1; dx += 2) {
                    for (int dy = -1; dy <= 1; dy += 2) {
                        ChessPosition newPosition = myPosition.adjust(dx, dy);
                        while (newPosition.inBoard()) {
                            ChessPiece otherPiece = board.getPiece(newPosition);
                            if (otherPiece != null) {
                                if (otherPiece.getTeamColor() != this.pieceColor) {
                                    validMoves.add(new ChessMove(myPosition, newPosition));
                                }
                                break;
                            }
                            validMoves.add(new ChessMove(myPosition, newPosition));
                            newPosition = newPosition.adjust(dx, dy);
                        }
                    }
                }
                break;
            case KNIGHT:
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (Math.abs(dx) != Math.abs(dy) && Math.abs(dx) + Math.abs(dy) == 3) {
                            ChessPosition newPosition = myPosition.adjust(dx, dy);
                            if (newPosition.inBoard()) {
                                if (board.getPiece(newPosition) != null) {
                                    if (board.getPiece(newPosition).getTeamColor() != this.pieceColor) {
                                        validMoves.add(new ChessMove(myPosition, newPosition));
                                    }
                                    continue;
                                } else {
                                    validMoves.add(new ChessMove(myPosition, newPosition));
                                }
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
                            while (newPosition.inBoard()) {
                                ChessPiece otherPiece = board.getPiece(newPosition);
                                if(otherPiece != null) {
                                    if(otherPiece.getTeamColor() != this.pieceColor) {
                                        validMoves.add(new ChessMove(myPosition, newPosition));
                                    }
                                    break;
                                }
                                validMoves.add(new ChessMove(myPosition, newPosition));
                                newPosition = newPosition.adjust(dx, dy);
                            }
                        }
                    }
                }
                break;
            case PAWN:
                ChessPosition newPosition = this.pieceColor == TeamColor.WHITE ? myPosition.adjust(0, 1) : myPosition.adjust(0, -1);
                if (newPosition.inBoard() && board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition));

                    if (!this.hasMoved) {
                        ChessPosition twoSquaresForward = this.pieceColor == TeamColor.WHITE ? myPosition.adjust(0, 2) : myPosition.adjust(0, -2);
                        if (twoSquaresForward.inBoard() && board.getPiece(twoSquaresForward) == null) {
                            validMoves.add(new ChessMove(myPosition, twoSquaresForward));
                        }
                    }
                }

                ChessPosition[] takePositions = {
                        this.pieceColor == TeamColor.WHITE ? myPosition.adjust(-1, 1) : myPosition.adjust(-1, -1),
                        this.pieceColor == TeamColor.WHITE ? myPosition.adjust(1, 1) : myPosition.adjust(1, -1)
                };

                for (ChessPosition takePosition : takePositions) {
                    if (takePosition.inBoard()) {
                        ChessPiece potentialTarget = board.getPiece(takePosition);
                        if (potentialTarget != null && potentialTarget.getTeamColor() != this.pieceColor) {
                            validMoves.add(new ChessMove(myPosition, takePosition));
                        }
                    }
                }
                break;
        return validMoves;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPiece that = (ChessPiece) o;

        if (pieceColor != that.pieceColor) return false;
        return this.type == that.type;
    }

    @Override
    public int hashCode() {
        int result = pieceColor != null ? pieceColor.hashCode() : 0;
        result = 31 * result + (this.type != null ? type.hashCode() : 0);
        return result;
    }

}
